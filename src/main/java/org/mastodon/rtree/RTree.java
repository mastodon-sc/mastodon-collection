package org.mastodon.rtree;

import java.util.Collection;

import org.mastodon.Ref;
import org.mastodon.RefPool;
import org.mastodon.collection.RefStack;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.collection.ref.RefArrayStack;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.MemPool.Factory;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;

import gnu.trove.stack.TIntStack;
import gnu.trove.stack.array.TIntArrayStack;
import net.imglib2.RealInterval;

public class RTree< O extends Geometry & Ref< O >, T extends MappedElement >
extends Pool< RTreeNode< O, T >, T >
{

	private final int n;

	private final RefPool< O > objectPool;

	final RTreeNodeLayout layout;

	private long size = 0l;

	private final int rootNodeId = 0;

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private RTree(
			final int initialCapacity,
			final RTreeNodeLayout layout,
			final Factory< T > memPoolFactory,
			final RefPool< O > objectPool )
	{
		super( initialCapacity, layout, ( Class ) RTreeNode.class, memPoolFactory );
		this.layout = layout;
		this.n = layout.n;
		this.objectPool = objectPool;
		this.parents = new RefArrayStack<>( this );
		this.parentsEntry = new TIntArrayStack();
	}

	@Override
	protected RTreeNode< O, T > createEmptyRef()
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
	private final RefStack< RTreeNode< O, T > > parents;

	/**
	 * Entry index of the child node in the parent entries.
	 */
	private final TIntStack parentsEntry;

	RefPool< O > getObjectPool()
	{
		return objectPool;
	}

	public static < O extends Geometry & Ref< O >, T extends MappedElement >
	RTree< O, T > rtree( final Collection< O > objects, final RefPool< O > objectPool, final MemPool.Factory< T > poolFactory )
	{
		final int capacity = objects.size();
		final int numDimensions = getNumDimensions( objects, objectPool );
		final RTreeNodeLayout layout = new RTreeNodeLayout( numDimensions, 2, 4 );
		final RTree< O, T > rtree = new RTree<>( capacity, layout, poolFactory, objectPool );
		rtree.build( objects );
		return rtree;
	}

	private void build( final Collection< O > objects )
	{
		// TODO
	}

	public void add( final O o, final RTreeNode< O, T > ref )
	{
		add( o, ref, 1 );
		size++;
	}

	private void add( final O o, final RTreeNode< O, T > ref, final int level )
	{
		/*
		 * I1 [Find position for new record] Invoke ChooseLeaf to select a leaf
		 * node L in which to place o.
		 */
		final RTreeNode< O, T > node = chooseNode( o, ref, level );

		/*
		 * I2 [Add record to leaf node] If L has room for another entry, install
		 * E. Otherwise invoke SplitNode to obtain L and LL containing E and all
		 * the old entries of L.
		 */
		if ( node.getNEntries() < layout.maxNEntries )
			node.addEntry( o );
		else
		{
			final RTreeNode< O, T > ref2 = createEmptyRef();
			final RTreeNode< O, T > newLeaf = splitNode( node, o, ref2, objectPool );

			final RTreeNode< O, T > ref3 = createEmptyRef();
			final RTreeNode< O, T > newNode = adjustTree( node, newLeaf, ref3 );

			releaseRef( ref2 );
			releaseRef( ref3 );
		}

//
//		// I3 [Propagate changes upwards] Invoke AdjustTree on L, also passing
//		// LL
//		// if a split was performed
//
//		// I4 [Grow tree taller] If node split propagation caused the root to
//		// split, create a new root whose children are the two resulting nodes.
//		if ( newNode != null )
//		{
//			final int oldRootNodeId = rootNodeId;
//			final Node oldRoot = getNode( oldRootNodeId );
//
//			rootNodeId = getNextNodeId();
//			treeHeight++;
//			final Node root = new Node( rootNodeId, treeHeight, maxNodeEntries );
//			root.addEntry( newNode.mbrMinX, newNode.mbrMinY, newNode.mbrMaxX, newNode.mbrMaxY, newNode.nodeId );
//			root.addEntry( oldRoot.mbrMinX, oldRoot.mbrMinY, oldRoot.mbrMaxX, oldRoot.mbrMaxY, oldRoot.nodeId );
//			nodeMap.put( rootNodeId, root );
//		}
	}

	/**
	 * Ascend from a leaf node L to the root, adjusting covering rectangles and
	 * propagating node splits as necessary.
	 *
	 * @param ref3
	 */
	private RTreeNode< O, T > adjustTree( final RTreeNode< O, T > node, final RTreeNode< O, T > newLeaf, final RTreeNode< O, T > ref3 )
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
			final RTreeNode< O, T > parent = parents.pop( ref3 );
			parent.recalculateMBR();

			/*
			 * AT4 [Propagate node split upward] If N has a partner NN resulting
			 * from an earlier split, create a new entry Enn with Ennp pointing
			 * to NN and Enni enclosing all rectangles in NN. Add Enn to P if
			 * there is room. Otherwise, invoke splitNode to produce P and PP
			 * containing Enn and all P's old entries.
			 */
			RTreeNode< O, T > newNode = null;
			if ( newLeaf != null )
			{
				if ( parent.getNEntries() < layout.maxNEntries )
				{
					parent.addEntry( newLeaf );
				}
				else
				{
					final RTreeNode< O, T > ref4 = createEmptyRef();
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

	private < K extends Ref< K > & RealInterval > RTreeNode< O, T > splitNode( final RTreeNode< O, T > node, final K o, final RTreeNode< O, T > nodeRef,
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

		final RTreeNode< O, T > newNode = create( nodeRef ).init( o1 );
		node.reset();
		node.addEntry( o2 );

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
					newNode.addEntry( seedsForSplit.get( j, objectRef1 ) );
				break A;
			}
			if ( node.getNEntries() == ( layout.minNEntries - nRemainingEntries ) )
			{
				// Give all entries to this node, discard other cases.
				for ( int j = 0; j < seedsForSplit.size(); j++ )
					node.addEntry( seedsForSplit.get( j, objectRef1 ) );
				break A;
			}

			final K oi = seedsForSplit.get( i, objectRef1 );

			// 1. minimal area enlargement.
			final double enlargement1 = GeometryUtil.enlargement( oi, newNode );
			final double enlargement2 = GeometryUtil.enlargement( oi, node );
			if ( enlargement1 < enlargement2 )
			{
				newNode.addEntry( oi );
			}
			else if ( enlargement2 < enlargement1 )
			{
				node.addEntry( oi );
			}
			else
			{
				// Tie 2. Solve with areas.
				final double area1 = GeometryUtil.area( newNode );
				final double area2 = GeometryUtil.area( node );
				if ( area1 < area2 )
				{
					newNode.addEntry( oi );
				}
				else if ( area2 < area1 )
				{
					node.addEntry( oi );
				}
				else if ( newNode.getNEntries() < node.getNEntries() )
				{
					// Tie 3. Solve with number of elements.
					newNode.addEntry( oi );
				}
				else
				{
					node.addEntry( oi );
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
	private RTreeNode< O, T > chooseNode( final O o, final RTreeNode< O, T > ref, final int level )
	{
		final RTreeNode< O, T > ref2 = createEmptyRef();

		// CL1 [Initialize] Set N to be the root node
		RTreeNode< O, T > node = getObject( rootNodeId, ref );
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
				final RTreeNode< O, T > child = getObject( id, ref2 );
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