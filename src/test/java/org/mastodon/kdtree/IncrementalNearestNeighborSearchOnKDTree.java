package org.mastodon.kdtree;

import java.util.PriorityQueue;

import org.mastodon.pool.MappedElement;

import net.imglib2.RealLocalizable;
import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * Implementation of {@link NearestNeighborSearch} search for kd-trees.
 *
 *
 * @author Tobias Pietzsch
 */
public final class IncrementalNearestNeighborSearchOnKDTree< O extends RealLocalizable, T extends MappedElement >
//	implements Cursor< O >
{
	private final KDTree< O, T > tree;

	private final int n;

	private final double[] pos;

	private final KDTreeNode< O, T > node;

	private int bestPointNodeIndex;

	private double bestSquDistance;

	private final O obj;

	public IncrementalNearestNeighborSearchOnKDTree( final KDTree< O, T > tree )
	{
		n = tree.numDimensions();
		pos = new double[ n ];
		bestPointNodeIndex = -1;
		bestSquDistance = Double.MAX_VALUE;
		this.tree = tree;
		this.node = tree.createRef();
		this.obj = tree.getObjectPool().createRef();
	}

	public int numDimensions()
	{
		return n;
	}

	PriorityQueue< HeapElement > queue = new PriorityQueue<>();

	public void search( final RealLocalizable p )
	{
		if ( tree.size() <= 0 )
			return;

		queue.clear();

		p.localize( pos );
		bestSquDistance = Double.MAX_VALUE;

		// create root
		final HeapElement rootElement = new HeapElement();
		rootElement.isPoint = false;
		tree.realMin( rootElement.xmin );
		tree.realMax( rootElement.xmax );
		rootElement.distance = distanceToBox( pos, rootElement.xmin, rootElement.xmax );
		rootElement.nodeIndex = tree.rootIndex;
		rootElement.splitDim = 0;
		queue.add( rootElement );

		System.out.println( rootElement );

		int i = 0;
		while ( true )
		{
			++i;
			final HeapElement current = queue.poll();
			if ( current == null )
				break;

			if ( current.isPoint )
			{
//				System.out.println( "found " + current );
				System.out.println( Math.sqrt( current.distance ) );
//				System.out.println( "visited " + i + " nodes" );
				continue;
			}

			// get one new point and two new boxes
			tree.getObject( current.nodeIndex, node );

			final HeapElement point = new HeapElement();
			point.isPoint = true;
			point.distance = node.squDistanceTo( pos );
			point.nodeIndex = current.nodeIndex;
			queue.offer( point );

			final int d = current.splitDim;
			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			final int leftIndex = node.getLeftIndex();
			final int rightIndex = node.getRightIndex();

			// add the near branch
			if ( leftIndex != -1 )
			{
				final HeapElement left = new HeapElement();
				left.isPoint = false;
				left.nodeIndex = leftIndex;
				left.splitDim = dChild;
				System.arraycopy( current.xmax, 0, left.xmax, 0, n );
				System.arraycopy( current.xmin, 0, left.xmin, 0, n );
				left.xmax[ d ] = node.getPosition( d );
				left.distance = distanceToBox( pos, left.xmin, left.xmax );
				queue.add( left );
			}

			// add the away branch
			if ( rightIndex != -1 )
			{
				final HeapElement right = new HeapElement();
				right.isPoint = false;
				right.nodeIndex = rightIndex;
				right.splitDim = dChild;
				System.arraycopy( current.xmax, 0, right.xmax, 0, n );
				System.arraycopy( current.xmin, 0, right.xmin, 0, n );
				right.xmin[ d ] = node.getPosition( d );
				right.distance = distanceToBox( pos, right.xmin, right.xmax );
				queue.add( right );
			}
		}
		System.out.println( "visited " + i + " nodes" );
	}

	double distanceToBox( final double[] point, final double[] xmin, final double[] xmax )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double p = point[ d ];
			final double l = xmin[ d ];
			final double h = xmax[ d ];
			if ( p < l )
				sum += ( l - p ) * ( l - p );
			else if ( p > h )
				sum += ( p - h ) * ( p - h );
		}
		return sum;
	}

	class HeapElement implements Comparable< HeapElement >
	{
		int nodeIndex;

		int splitDim;

		/**
		 * true == point. false == box.
		 */
		boolean isPoint;

		/**
		 * For point: squared distance to query point. For box: minimum squared
		 * distance from any point in box to query point.
		 *
		 * Sorts priority queue. Smaller means higher priority.
		 */
		double distance;

		final double[] xmin = new double[ n ];

		final double[] xmax = new double[ n ];

		@Override
		public int compareTo( final HeapElement o )
		{
			return Double.compare( distance, o.distance );
		}

		@Override
		public String toString()
		{
			final StringBuilder builder = new StringBuilder();

			builder.append( "(" );
			builder.append( nodeIndex );
			builder.append( ") " );

			if ( isPoint )
			{
				builder.append( "point " );

				final KDTreeNode< O, T > node = tree.createRef();
				final O obj = tree.getObjectPool().createRef();
				tree.getObject( nodeIndex, node );
				builder.append( tree.getObjectPool().getObject( node.getDataIndex(), obj ).toString() );
				tree.getObjectPool().releaseRef( obj );
				tree.releaseRef( node );
			}
			else
			{
				builder.append( "box " );
				builder.append( "split d=" );
				builder.append( splitDim );
				for ( int d = 0; d < n; ++d )
				{
					builder.append( "\n  " );
					builder.append( xmin[ d ] );
					builder.append( " .. " );
					builder.append( xmax[ d ] );
				}
			}

			return builder.toString();
		}
	}

	private final void searchNode( final int currentNodeIndex, final int d )
	{
		// consider the current node
		tree.getObject( currentNodeIndex, node );
		final double distance = node.squDistanceTo( pos );
		if ( distance < bestSquDistance )
		{
			bestSquDistance = distance;
			bestPointNodeIndex = currentNodeIndex;
		}

		final double axisDiff = pos[ d ] - node.getPosition( d );
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		final int nearChildNodeIndex = leftIsNearBranch ? node.getLeftIndex() : node.getRightIndex();
		final int awayChildNodeIndex = leftIsNearBranch ? node.getRightIndex() : node.getLeftIndex();
		if ( nearChildNodeIndex != -1 )
			searchNode( nearChildNodeIndex, d + 1 == n ? 0 : d + 1 );

		// search the away branch - maybe
		if ( ( awayChildNodeIndex != -1 ) && ( axisDiff * axisDiff <= bestSquDistance ) )
			searchNode( awayChildNodeIndex, d + 1 == n ? 0 : d + 1 );
	}

	public RealLocalizable getPosition()
	{
		if ( bestPointNodeIndex == -1 )
			return null;

		tree.getObject( bestPointNodeIndex, node );
		return node;
	}

	public double getSquareDistance()
	{
		return bestSquDistance;
	}

	public double getDistance()
	{
		return Math.sqrt( bestSquDistance );
	}

	public O get()
	{
		if ( bestPointNodeIndex == -1 )
			return null;

		tree.getObject( bestPointNodeIndex, node );
		return tree.getObjectPool().getObject( node.getDataIndex(), obj );
	}
}
