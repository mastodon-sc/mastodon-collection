package org.mastodon.kdtree;

import org.mastodon.collection.ref.IntRefArrayMap;
import org.mastodon.kdtree.IncrementalNearestNeighborSearchOnKDTree.NodeData;
import org.mastodon.kdtree.IncrementalNearestNeighborSearchOnKDTree.NodeDataPool;
import org.mastodon.kdtree.IncrementalNearestNeighborSearchOnKDTree.NodeDataQueue;
import org.mastodon.pool.MappedElement;

import gnu.trove.iterator.TIntIterator;
import net.imglib2.RealLocalizable;

/**
 * Implementation of incremental nearest neighbor search for kd-trees.
 * 
 * @param <O>
 *            type of objects stored in the tree.
 * @param <T>
 *            the {@link MappedElement} type of the created pool of nodes.
 * @author Tobias Pietzsch
 */
public final class IncrementalNearestValidNeighborSearchOnKDTree< O extends RealLocalizable, T extends MappedElement >
		implements IncrementalNearestNeighborSearch< O >
{
	private final KDTree< O, T > tree;

	private final int n;

	private final double[] pos;

	private final KDTreeNode< O, T > node;

	private final O obj;

	private final NodeDataPool pool;

	private final NodeDataQueue queue;

	private final NodeData ref1;

	private final NodeData ref2;

	private final KDTreeNode< O, T > currentNode;

	private O currentObj;

	private int nextNodeIndex;

	private double currentSquDistance;

	private double nextSquDistance;

	public IncrementalNearestValidNeighborSearchOnKDTree( final KDTree< O, T > tree )
	{
		this.tree = tree;
		n = tree.numDimensions();
		pos = new double[ n ];
		node = tree.createRef();
		pool = new NodeDataPool( n );
		queue = new NodeDataQueue( pool );
		obj = tree.getObjectPool().createRef();
		ref1 = pool.createRef();
		ref2 = pool.createRef();
		currentNode = tree.createRef();
	}

	private IncrementalNearestValidNeighborSearchOnKDTree( final IncrementalNearestValidNeighborSearchOnKDTree< O, T > that )
	{
		this.tree = that.tree;
		this.n = that.n;
		this.pos = that.pos.clone();
		this.node = tree.createRef().refTo( that.node );
		pool = new NodeDataPool( n );
		queue = new NodeDataQueue( pool );
		obj = tree.getObjectPool().createRef();
		currentObj = that.currentObj == null ? null : tree.getObjectPool().getObject( tree.getObjectPool().getId( that.currentObj ), obj );
		ref1 = pool.createRef();
		ref2 = pool.createRef();
		currentNode = tree.createRef().refTo( that.currentNode );
		this.nextNodeIndex = that.nextNodeIndex;
		this.nextSquDistance = that.nextSquDistance;
		this.currentSquDistance = that.currentSquDistance;

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

	@Override
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
		hasNext();
		tree.getObject( nextNodeIndex, currentNode );
		currentObj = tree.getObjectPool().getObject( currentNode.getDataIndex(), obj );
		currentSquDistance = nextSquDistance;
		nextNodeIndex = -1;
	}

	@Override
	public boolean hasNext()
	{
		if ( nextNodeIndex < 0 )
		{
			nextToFront();
			if ( queue.isEmpty() )
				return false;
			nextNodeIndex = queue.peek( ref1 ).getNodeIndex();
			nextSquDistance = queue.peek( ref1 ).getSquDistance();
		}
		return true;
	}

	@Override
	public O next()
	{
		fwd();
		return get();
	}

	@Override
	public double getSquareDistance()
	{
		return currentSquDistance;
	}

	@Override
	public double getDistance()
	{
		return Math.sqrt( currentSquDistance );
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
	public IncrementalNearestValidNeighborSearchOnKDTree< O, T > copy()
	{
		return new IncrementalNearestValidNeighborSearchOnKDTree<>( this );
	}

	@Override
	public IncrementalNearestValidNeighborSearchOnKDTree< O, T > copyCursor()
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
			nextNodeIndex = -1;
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
		nextToFront();
		nextNodeIndex = queue.isEmpty() ? -1 : queue.peek( ref1 ).getNodeIndex();
		nextSquDistance = queue.isEmpty() ? -1 : queue.peek( ref1 ).getSquDistance();
	}

	private void nextToFront()
	{
		if ( !queue.isEmpty() && queue.peek( ref1 ).isPoint() )
			pool.delete( queue.poll( ref1 ) );

		while ( !queue.isEmpty() )
		{
			final NodeData current = queue.peek( ref1 );
			tree.getObject( current.getNodeIndex(), node );
			if ( current.isPoint() )
			{
				if ( node.isValid() )
					break;
				pool.delete( queue.poll( ref1 ) );
				continue;
			}

			// create (at most) two new boxes for left and right child box
			final int leftIndex = node.getLeftIndex();
			final int rightIndex = node.getRightIndex();
			final double squDist = current.getSquDistance();

			// make "current" box into a point and put it back on the queue
			current.setIsPoint( true );
			current.setSquDistance( node.squDistanceTo( pos ) );
			queue.siftDown( 0 );

			if ( leftIndex != -1 || rightIndex != -1 )
			{
				final int d = current.getSplitDim();
				final int dChild = ( d + 1 == n ) ? 0 : d + 1;
				final double axisdiff = node.getPosition( d ) - pos[ d ];
				final double asd = axisdiff * axisdiff;
				final int o = current.getOrient( d );

				// add the left branch
				if ( leftIndex != -1 )
				{
					final NodeData left = pool.create( ref2 ).init( leftIndex, dChild, current );

					if ( o > 0 || axisdiff <= 0 ) // xmax <= pos
					{
						if ( o == 0 )
							left.setOrient( d, 1 );
						left.setAxisSquDistance( d, asd );
						left.setSquDistance( squDist - current.getAxisSquDistance( d ) + asd );
					}
					else
						left.setSquDistance( squDist );

					queue.offer( left );
				}

				// add the right branch
				if ( rightIndex != -1 )
				{
					final NodeData right = pool.create( ref2 ).init( rightIndex, dChild, current );

					if ( o < 0 || axisdiff > 0 ) // pos < xmin
					{
						if ( o == 0 )
							right.setOrient( d, -1 );
						right.setAxisSquDistance( d, asd );
						right.setSquDistance( squDist - current.getAxisSquDistance( d ) + asd );
					}
					else
						right.setSquDistance( squDist );

					queue.offer( right );
				}
			}
		}
	}
}
