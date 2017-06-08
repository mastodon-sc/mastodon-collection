package org.mastodon.rtree;

import java.util.Collection;

import org.mastodon.Ref;
import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefStack;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.collection.ref.RefArrayStack;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;

import gnu.trove.stack.TIntStack;
import gnu.trove.stack.array.TIntArrayStack;
import net.imglib2.RealInterval;

public class RTree< O extends Geometry & Ref< O > >
extends Pool< RTreeNode< O >, ByteMappedElement >
{

	private final int n;

	private final RefPool< O > objectPool;

	final RTreeNodeLayout layout;

	private long size = 0l;

	private int rootNodeId = -1;

//	public public RTree()
//	{
//		// TODO Auto-generated constructor stub
//	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private RTree(
			final int initialCapacity,
			final RTreeNodeLayout layout,
			final RefPool< O > objectPool )
	{
		super( initialCapacity, layout, ( Class ) RTreeNode.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		this.layout = layout;
		this.n = layout.n;
		this.objectPool = objectPool;
		this.parents = new RefArrayStack<>( this );
		this.parentsEntry = new TIntArrayStack();
	}

	@Override
	protected RTreeNode< O > createEmptyRef()
	{
		return new RTreeNode<>( this, n );
	}

	static class RTreeNodeLayout extends PoolObjectLayout
	{
		final BooleanField isLeaf;

		final IndexArrayField entries;

		final IntField nEntries;

		final DoubleArrayField mbr;

		final int n;

		final int minNEntries;

		final int maxNEntries;

		RTreeNodeLayout( final int n, final int minNEntries, final int maxNEntries )
		{
			this.n = n;
			this.minNEntries = minNEntries;
			this.maxNEntries = maxNEntries;
			this.isLeaf = booleanField();
			this.entries = indexArrayField( maxNEntries );
			this.nEntries = intField();
			this.mbr = doubleArrayField( 2 * n );
		}
	}

	/**
	 * Path of root nodes from the chosen node by <code>chooseNode()</code> as
	 * stack from root to the selected leaf. Enables fast lookup of nodes when a
	 * split is propagated up the tree by the <code>adjustTree</code> method.
	 */
	private final RefStack< RTreeNode< O > > parents;

	/**
	 * Entry index of the child node in the parent entries.
	 */
	private final TIntStack parentsEntry;

	RefPool< O > getObjectPool()
	{
		return objectPool;
	}

	public static < O extends Geometry & Ref< O > >
	RTree< O > rtree( final RefCollection< O > objects, final RefPool< O > objectPool )
	{
		final int capacity = objects.size();
		final int numDimensions = getNumDimensions( objects, objectPool );
		final RTreeNodeLayout layout = new RTreeNodeLayout( numDimensions, 2, 4 );
		final RTree< O > rtree = new RTree<>( capacity, layout, objectPool );
		rtree.build( objects );
		return rtree;
	}

	private void build( final Collection< O > objects )
	{
		// TODO
	}

	public void add( final O o, final RTreeNode< O > ref )
	{
		size++;

		if ( rootNodeId < 0 )
		{
			// First node, this is the root.
			final RTreeNode< O > root = create( ref ).init( o );
			root.setIsLeaf( true );
			rootNodeId = root.getInternalPoolIndex();
			return;
		}

		/*
		 * I1 [Find position for new record] Invoke ChooseLeaf to select a leaf
		 * node L in which to place o.
		 */
		final RTreeNode< O > node = chooseNode( o, ref );

		/*
		 * I2 [Add record to leaf node] If L has room for another entry, install
		 * E. Otherwise invoke SplitNode to obtain L and LL containing E and all
		 * the old entries of L.
		 */
		final RTreeNode< O > ref3 = createEmptyRef();
		RTreeNode< O > newNode = null;
		if ( node.getNEntries() < layout.maxNEntries )
			node.addEntry( o );
		else
		{
			final RTreeNode< O > ref2 = createEmptyRef();
			final RTreeNode< O > newLeaf = splitNode( node, o, ref2, objectPool );

			newNode = adjustTree( node, newLeaf, ref3 );

			releaseRef( ref2 );
		}

		/*
		 * I3 [Propagate changes upwards] Invoke AdjustTree on L, also passing
		 * LL if a split was performed.
		 */

		/*
		 * I4 [Grow tree taller] If node split propagation caused the root to
		 * split, create a new root whose children are the two resulting nodes.
		 */
		if ( newNode != null )
		{
			final int oldRootNodeId = rootNodeId;
			final RTreeNode< O > ref4 = createEmptyRef();
			final RTreeNode< O > oldRoot = getObject( oldRootNodeId, ref4 );
			final RTreeNode< O > root = create( ref4 ).init( oldRoot );
			rootNodeId = root.getInternalPoolIndex();
			root.addChild( newNode );

			releaseRef( ref4 );

		}
		releaseRef( ref3 );
	}

	/**
	 * Ascend from a leaf node L to the root, adjusting covering rectangles and
	 * propagating node splits as necessary.
	 *
	 * @param ref3
	 */
	private RTreeNode< O > adjustTree( final RTreeNode< O > node, final RTreeNode< O > newLeaf, final RTreeNode< O > ref3 )
	{
		/*
		 * AT1 [Initialize] Set N=L. If L was split previously, set NN to be the
		 * resulting second node.
		 */

		// AT2 [Check if done] If N is the root, stop
		while ( rootNodeId != node.getInternalPoolIndex() )
		{

			/*
			 * AT3 [Adjust covering rectangle in parent entry] Let P be the
			 * parent node of N, and let En be N's entry in P. Adjust EnI so
			 * that it tightly encloses all entry rectangles in N.
			 */
			final RTreeNode< O > parent = parents.pop( ref3 );
			parent.recalculateMBR();

			/*
			 * AT4 [Propagate node split upward] If N has a partner NN resulting
			 * from an earlier split, create a new entry Enn with Ennp pointing
			 * to NN and Enni enclosing all rectangles in NN. Add Enn to P if
			 * there is room. Otherwise, invoke splitNode to produce P and PP
			 * containing Enn and all P's old entries.
			 */
			RTreeNode< O > newNode = null;
			if ( newLeaf != null )
			{
				if ( parent.getNEntries() < layout.maxNEntries )
				{
					parent.addChild( newLeaf );
				}
				else
				{
					final RTreeNode< O > ref4 = createEmptyRef();
					newNode = splitNode( parent, newLeaf, ref4, this );
					newLeaf.refTo( newNode );
				}
			}

			// AT5 [Move up to next level] Set N = P and set NN = PP if a split
			// occurred. Repeat from AT2
			node.refTo( parent );
		}

		return newLeaf;
	}

	private < K extends Ref< K > & RealInterval > RTreeNode< O > splitNode( final RTreeNode< O > node, final K o, final RTreeNode< O > nodeRef,
			final RefPool< K > oPool )
	{
		/*
		 * Create pool refs.
		 */

		final K objectRef1 = oPool.createRef();
		final K objectRef2 = oPool.createRef();

		/*
		 * Store all entries + new entry
		 */

		final RefArrayList< K > seedsForSplit = new RefArrayList<>( oPool, layout.maxNEntries + 1 );
		seedsForSplit.add( o );
		for ( int i = 0; i < node.getNEntries(); i++ )
		{
			final int entryID = node.getEntry( i );
			final K e = oPool.getObject( entryID, objectRef1 );
			seedsForSplit.add( e );
		}

		/*
		 * Find the two entries separated by the max distance.
		 */

		int source = -1;
		int target = -1;
		double maxDistance = Double.NEGATIVE_INFINITY;
		for ( int i = 0; i < seedsForSplit.size() - 1; i++ )
		{
			for ( int j = i + 1; j < seedsForSplit.size(); j++ )
			{
				final double d = GeometryUtil.distance( seedsForSplit.get( i, objectRef1 ), seedsForSplit.get( i, objectRef2 ) );
				if ( d > maxDistance )
				{
					maxDistance = d;
					source = i;
					target = j;
				}
			}
		}
		final K o1 = seedsForSplit.get( source, objectRef1 );
		final K o2 = seedsForSplit.get( target, objectRef2 );
		seedsForSplit.remove( o1 );
		seedsForSplit.remove( o2 );

		/*
		 * Create a new nodes with o1 as first entry. Clear existing node and
		 * add o2 as sole entry.
		 */

		final RTreeNode< O > newNode = create( nodeRef ).init( o1 );
		node.reset();
		node.add( o2 );

		/*
		 * Examine remaining nodes and assign them to the new node or the
		 * specified node choosing according to: 0. make sure all nodes have at
		 * least minNEntries entries; 1. minimal area enlargement; 2. if ties,
		 * minimal area; 3. if ties, minimal number of entries.
		 */
		A: for ( int i = 0; i < seedsForSplit.size(); i++ )
		{

			// 0. ensure min N entries is obtained.
			final int nRemainingEntries = seedsForSplit.size() - i - 1;
			if ( newNode.getNEntries() == ( layout.minNEntries - nRemainingEntries ) )
			{
				// Give all entries to this node, discard other cases.
				for ( int j = 0; j < seedsForSplit.size(); j++ )
					newNode.add( seedsForSplit.get( j, objectRef1 ) );
				break A;
			}
			if ( node.getNEntries() == ( layout.minNEntries - nRemainingEntries ) )
			{
				// Give all entries to this node, discard other cases.
				for ( int j = 0; j < seedsForSplit.size(); j++ )
					node.add( seedsForSplit.get( j, objectRef1 ) );
				break A;
			}

			final K oi = seedsForSplit.get( i, objectRef1 );

			// 1. minimal area enlargement.
			final double enlargement1 = GeometryUtil.enlargement( oi, newNode );
			final double enlargement2 = GeometryUtil.enlargement( oi, node );
			if ( enlargement1 < enlargement2 )
			{
				newNode.add( oi );
			}
			else if ( enlargement2 < enlargement1 )
			{
				node.add( oi );
			}
			else
			{
				// Tie 2. Solve with areas.
				final double area1 = GeometryUtil.area( newNode );
				final double area2 = GeometryUtil.area( node );
				if ( area1 < area2 )
				{
					newNode.add( oi );
				}
				else if ( area2 < area1 )
				{
					node.add( oi );
				}
				else if ( newNode.getNEntries() < node.getNEntries() )
				{
					// Tie 3. Solve with number of elements.
					newNode.add( oi );
				}
				else
				{
					node.add( oi );
				}
			}
		}

		/*
		 * Release pool refs.
		 */

		oPool.releaseRef( objectRef1 );
		oPool.releaseRef( objectRef2 );

		return newNode;
	}

	/**
	 * Used by add(). Chooses a leaf to add the geometry to.
	 */
	private RTreeNode< O > chooseNode( final O o, final RTreeNode< O > ref )
	{
		final RTreeNode< O > ref2 = createEmptyRef();

		// CL1 [Initialize] Set N to be the root node
		RTreeNode< O > node = getObject( rootNodeId, ref );
		parents.clear();
		parentsEntry.clear();

		while ( true )
		{
			// CL2 [Leaf check] If N is a leaf, return N
			if ( node.isLeaf() ) { return node; }

			/*
			 * CL3 [Choose subtree] If N is not at the desired level, let F be
			 * the entry in N whose rectangle FI needs least enlargement to
			 * include EI. Resolve ties by choosing the entry with the rectangle
			 * of smaller area.
			 */
			double leastEnlargement = Double.POSITIVE_INFINITY;
			double leastArea = Double.POSITIVE_INFINITY;
			int index = 0;
			for ( int i = 0; i < node.getNEntries(); i++ )
			{
				final int id = node.getEntry( i );
				final RTreeNode< O > child = getObject( id, ref2 );
				final double tempEnlargement = GeometryUtil.enlargement( child, o );
				if ( ( tempEnlargement < leastEnlargement ) ||
						( ( tempEnlargement == leastEnlargement ) && ( GeometryUtil.area( child ) < leastArea ) ) )
				{
					index = i;
					leastEnlargement = tempEnlargement;
					leastArea = GeometryUtil.area( child );
				}
			}

			/*
			 * CL4 [Descend until a leaf is reached] Set N to be the child node
			 * pointed to by Fp and repeat from CL2.
			 */
			node = getObject( node.getEntry( index ), ref );

			parents.push( node );
			parentsEntry.push( index );
		}
	}

	private static < O extends Geometry > int getNumDimensions( final Collection< O > objects, final RefPool< O > objectPool )
	{
		final int n;
		if ( objects.isEmpty() )
		{
			final O ref = objectPool.createRef();
			n = ref.numDimensions();
			objectPool.releaseRef( ref );
		}
		else
			n = objects.iterator().next().numDimensions();
		return n;
	}
}
