package org.mastodon.kdtree;

import org.mastodon.collection.ref.IntRefArrayMap;
import org.mastodon.collection.ref.RefArrayPriorityQueue;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.BooleanAttribute;
import org.mastodon.pool.attributes.ByteArrayAttribute;
import org.mastodon.pool.attributes.DoubleArrayAttribute;
import org.mastodon.pool.attributes.DoubleAttribute;
import org.mastodon.pool.attributes.IndexAttribute;
import org.mastodon.pool.attributes.IntAttribute;

import gnu.trove.iterator.TIntIterator;
import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;

/**
 * Implementation of incremental nearest neighbor search for kd-trees.
 *
 * @author Tobias Pietzsch
 */
public final class IncrementalNearestNeighborSearchOnKDTree< O extends RealLocalizable, T extends MappedElement > implements RealCursor< O >
{
	private final KDTree< O, T > tree;

	private final int n;

	private final double[] pos;

	private final KDTreeNode< O, T > node;

	private final O obj;

	private final NodeDataPool pool;

	private final RefArrayPriorityQueue< NodeData > queue;

	private boolean hasNext;

	private final NodeData ref1;

	private final NodeData ref2;

	private final KDTreeNode< O, T > currentNode;

	private O currentObj;

	public IncrementalNearestNeighborSearchOnKDTree( final KDTree< O, T > tree )
	{
		this.tree = tree;
		n = tree.numDimensions();
		pos = new double[ n ];
		node = tree.createRef();
		pool = new NodeDataPool( n );
		queue = new RefArrayPriorityQueue<>( pool );
		obj = tree.getObjectPool().createRef();
		ref1 = pool.createRef();
		ref2 = pool.createRef();
		currentNode = tree.createRef();
	}

	private IncrementalNearestNeighborSearchOnKDTree( final IncrementalNearestNeighborSearchOnKDTree< O, T > that )
	{
		this.tree = that.tree;
		this.n = that.n;
		this.pos = that.pos.clone();
		this.node = tree.createRef().refTo( that.node );
		pool = new NodeDataPool( n );
		queue = new RefArrayPriorityQueue<>( pool );
		this.hasNext = that.hasNext;
		obj = tree.getObjectPool().createRef();
		currentObj = that.currentObj == null ? null : tree.getObjectPool().getObject( tree.getObjectPool().getId( that.currentObj ), obj );
		ref1 = pool.createRef();
		ref2 = pool.createRef();
		currentNode = tree.createRef().refTo( that.currentNode );

		final IntRefArrayMap< NodeData > map = new IntRefArrayMap<>( pool, 2 * that.pool.size() );
		for ( final NodeData nd : that.pool )
			map.put( nd.getInternalPoolIndex(), pool.create( ref1 ).init( nd ) );
		final TIntIterator it = that.queue.getIndexCollection().iterator();
		while( it.hasNext() )
			queue.offer( map.get( it.next(), ref1 ) );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	public void search( final RealLocalizable query )
	{
		query.localize( pos );
		reset();
	}

	@Override
	public O get()
	{
		return currentObj;
	}

	@Override
	public void fwd()
	{
		nextToFront();
		if ( !queue.isEmpty() )
		{
			tree.getObject( queue.peek( ref1 ).getNodeIndex(), currentNode );
			currentObj = tree.getObjectPool().getObject( currentNode.getDataIndex(), obj );
		}
	}

	@Override
	public boolean hasNext()
	{
		return hasNext;
	}

	@Override
	public O next()
	{
		fwd();
		return get();
	}

	@Override
	public void localize( final float[] position )
	{
		currentNode.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		currentNode.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return currentNode.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return currentNode.getDoublePosition( d );
	}

	@Override
	public IncrementalNearestNeighborSearchOnKDTree< O, T > copy()
	{
		return new IncrementalNearestNeighborSearchOnKDTree<>( this );
	}

	@Override
	public IncrementalNearestNeighborSearchOnKDTree< O, T > copyCursor()
	{
		return copy();
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( int i = 0; i < steps; ++i )
			fwd();
	}

	@Override
	public void reset()
	{
		queue.reset();
		pool.clear();
		if ( tree.size() <= 0 )
		{
			hasNext = false;
			return;
		}

		final NodeData root = pool.create( ref1 ).init( tree.rootIndex, 0 );
		double squDistance = 0;
		for ( int d = 0; d < n; ++d )
		{
			double diff = tree.realMin( d ) - pos[ d ];
			if ( diff > 0 ) // pos < xmin
			{
				root.setOrient( d, -1 );
				root.setAxisSquDistance( d, diff * diff );
				squDistance += diff * diff;
			}
			else
			{
				diff = pos[ d ] - tree.realMax( d );
				if ( diff >= 0 ) // xmax <= pos
				{
					root.setOrient( d, 1 );
					root.setAxisSquDistance( d, diff * diff );
					squDistance += diff * diff;
				}
			}
		}
		root.setSquDistance( squDistance );
		queue.offer( root );
		hasNext = true;
	}

	private void nextToFront()
	{
		if ( !queue.isEmpty() && queue.peek( ref1 ).isPoint() )
			pool.delete( queue.poll( ref1 ) );

		while ( !queue.isEmpty() && !queue.peek( ref1 ).isPoint() )
		{
			final NodeData current = queue.poll( ref1 );
			// we know that "current" is a box because of the loop condition

			// create (at most) two new boxes for left and right child box
			tree.getObject( current.getNodeIndex(), node );
			final int d = current.getSplitDim();
			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			final int leftIndex = node.getLeftIndex();
			final int rightIndex = node.getRightIndex();
			final double axisdiff = node.getPosition( d ) - pos[ d ];

			// add the left branch
			if ( leftIndex != -1 )
			{
				final NodeData left = pool.create( ref2 ).init( leftIndex, dChild, current );

				final int o = left.getOrient( d );
				if ( o > 0 || axisdiff <= 0 ) // xmax <= pos
				{
					if ( o == 0 )
						left.setOrient( d, 1 );
					final double asd2 = axisdiff * axisdiff;
					left.setAxisSquDistance( d, asd2 );
					left.setSquDistance( left.getSquDistance() - current.getAxisSquDistance( d ) + asd2 );
				}
				queue.offer( left );
			}

			// add the right branch
			if ( rightIndex != -1 )
			{
				final NodeData right = pool.create( ref2 ).init( rightIndex, dChild, current );

				final int o = right.getOrient( d );
				if ( o < 0 || axisdiff > 0 ) // pos < xmin
				{
					if ( o == 0 )
						right.setOrient( d, -1 );
					final double asd = axisdiff * axisdiff;
					right.setAxisSquDistance( d, asd );
					right.setSquDistance( right.getSquDistance() - current.getAxisSquDistance( d ) + asd );
				}
				queue.offer( right );
			}

			// make "current" box into a point and put it back on the queue
			current.setIsPoint( true );
			current.setSquDistance( node.squDistanceTo( pos ) );
			queue.offer( current );
		}

		hasNext = queue.size() > 1;
	}

	static class NodeDataLayout extends PoolObjectLayout
	{
		final IndexField nodeIndex;
		final IntField splitDim;
		final BooleanField isPoint;
		final ByteArrayField orient;
		final DoubleArrayField axisSquareDistance;
		final DoubleField squDistance;

		NodeDataLayout( final int numDimensions )
		{
			this.numDimensions = numDimensions;
			nodeIndex = indexField();
			splitDim = intField();
			isPoint = booleanField();
			orient = byteArrayField( numDimensions );
			axisSquareDistance = doubleArrayField( numDimensions );
			squDistance = doubleField();
		}

		final int numDimensions;
	}

	static class NodeDataPool extends Pool< NodeData, ByteMappedElement >
	{
		public NodeData create()
		{
			return super.create( createRef() );
		}

		@Override
		public NodeData create( final NodeData obj )
		{
			return super.create( obj );
		}

		@Override
		public void delete( final NodeData obj )
		{
			super.delete( obj );
		}

		final NodeDataLayout layout;

		/**
		 * {@link KDTreeNode} corresponding to this point or box.
		 */
		final IndexAttribute< NodeData > nodeIndex;

		/**
		 * split dimensions of the corresponding {@link KDTreeNode}.
		 */
		final IntAttribute< NodeData > splitDim;

		/**
		 * true == point. false == box.
		 */
		final BooleanAttribute< NodeData > isPoint;

		/**
		 * for each dimension[d]:
		 * -1 : query[d] < boxmin[d]
		 *  0 : boxmin[d] <= query[d] < boxmax[d]
		 *  1 : boxmax[d] <= query[d]
		 */
		final ByteArrayAttribute< NodeData > orient;

		/**
		 * contribution to squared distance foe each dimension
		 */
		final DoubleArrayAttribute< NodeData > axisSquDistance;

		/**
		 * For point: squared distance to query point. For box: minimum squared
		 * distance from any point in box to query point.
		 *
		 * Sorts priority queue. Smaller means higher priority.
		 */
		final DoubleAttribute< NodeData > squDistance;

		NodeDataPool( final int numDimensions )
		{
			this( new NodeDataLayout( numDimensions ) );
		}

		private NodeDataPool( final NodeDataLayout layout )
		{
			super( 50, layout, NodeData.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
			this.layout = layout;

			nodeIndex = new IndexAttribute<>( layout.nodeIndex, this );
			splitDim = new IntAttribute<>( layout.splitDim, this );
			isPoint = new BooleanAttribute<>( layout.isPoint, this );
			orient = new ByteArrayAttribute<>( layout.orient, this );
			axisSquDistance = new DoubleArrayAttribute<>( layout.axisSquareDistance, this );
			squDistance = new DoubleAttribute<>( layout.squDistance, this );
		}

		@Override
		protected NodeData createEmptyRef()
		{
			return new NodeData( this );
		}
	}

	static class NodeData extends PoolObject< NodeData, NodeDataPool, ByteMappedElement > implements Comparable< NodeData >
	{
		final int n;

		protected NodeData( final NodeDataPool pool )
		{
			super( pool );
			n = pool.layout.numDimensions;
		}

		@Override
		protected void setToUninitializedState()
		{}

		@Override
		public int compareTo( final NodeData o )
		{
			return Double.compare( pool.squDistance.get( this ), pool.squDistance.get( o ) );
		}

		public int getNodeIndex()
		{
			return pool.nodeIndex.get( this );
		}

		public int getSplitDim()
		{
			return pool.splitDim.get( this );
		}

		public boolean isPoint()
		{
			return pool.isPoint.get( this );
		}

		public void setIsPoint( final boolean b )
		{
			pool.isPoint.setQuiet( this, b );
		}

		public int getOrient( final int d )
		{
			return pool.orient.get( this, d );
		}

		public void setOrient( final int d, final int value )
		{
			pool.orient.setQuiet( this, d, ( byte ) value );
		}

		public double getAxisSquDistance( final int d )
		{
			return pool.axisSquDistance.get( this, d );
		}

		public void setAxisSquDistance( final int d, final double value )
		{
			pool.axisSquDistance.setQuiet( this, d, value );
		}

		public double getSquDistance()
		{
			return pool.squDistance.get( this );
		}

		public void setSquDistance( final double value )
		{
			pool.squDistance.setQuiet( this, value );
		}

		public NodeData init( final int nodeIndex, final int splitDim )
		{
			pool.isPoint.setQuiet( this, false );
			pool.nodeIndex.setQuiet( this, nodeIndex );
			pool.splitDim.setQuiet( this, splitDim );
			for ( int d = 0; d < n; ++d )
			{
				pool.orient.setQuiet( this, d, ( byte ) 0 );
				pool.axisSquDistance.setQuiet( this, d, 0 );
			}
			pool.squDistance.setQuiet( this, 0 );
			return this;
		};

		public NodeData init( final int nodeIndex, final int splitDim, final NodeData parent )
		{
			pool.isPoint.setQuiet( this, false );
			pool.nodeIndex.setQuiet( this, nodeIndex );
			pool.splitDim.setQuiet( this, splitDim );
			for ( int d = 0; d < n; ++d )
			{
				pool.orient.setQuiet( this, d, pool.orient.get( parent, d ) );
				pool.axisSquDistance.setQuiet( this, d, pool.axisSquDistance.get( parent, d ) );
			}
			pool.squDistance.setQuiet( this, pool.squDistance.get( parent ) );
			return this;
		}

		public NodeData init( final NodeData other )
		{
			pool.isPoint.setQuiet( this, other.isPoint() );
			pool.nodeIndex.setQuiet( this, other.getNodeIndex() );
			pool.splitDim.setQuiet( this, other.getSplitDim() );
			for ( int d = 0; d < n; ++d )
			{
				pool.orient.setQuiet( this, d, ( byte ) other.getOrient( d ) );
				pool.axisSquDistance.setQuiet( this, d, other.getAxisSquDistance( d ) );
			}
			pool.squDistance.setQuiet( this, other.getSquDistance() );
			return this;
		}
	}
}
