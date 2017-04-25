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

	public IncrementalNearestNeighborSearchOnKDTree( final KDTree< O, T > tree )
	{
		n = tree.numDimensions();
		pos = new double[ n ];
		this.tree = tree;
		this.node = tree.createRef();
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

		// create root
		final HeapElement rootElement = new HeapElement();
		rootElement.isPoint = false;
		tree.realMin( rootElement.xmin );
		tree.realMax( rootElement.xmax );
		rootElement.distance = distanceToBox( pos, rootElement.xmin, rootElement.xmax );
		rootElement.nodeIndex = tree.rootIndex;
		rootElement.splitDim = 0;

		rootElement.ddistdistance = 0;
		for ( int d = 0; d < n; ++d )
		{
			double ddiff = rootElement.xmin[ d ] - pos[ d ];
			if ( ddiff > 0 ) // pos < xmin
			{
				rootElement.dorient[ d ] = -1;
				rootElement.ddist[ d ] = ddiff * ddiff;
				rootElement.ddistdistance += rootElement.ddist[ d ];
			}
			else
			{
				ddiff = pos[ d ] - rootElement.xmax[ d ];
				if ( ddiff >= 0 ) // xmax <= pos
				{
					rootElement.dorient[ d ] = 1;
					rootElement.ddist[ d ] = ddiff * ddiff;
					rootElement.ddistdistance += rootElement.ddist[ d ];
				}
			}
		}
		if ( Math.abs( rootElement.distance - rootElement.ddistdistance ) > 0.0000000001 )
			System.out.println( "oh noooooo! (root)" );

		queue.add( rootElement );

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
			point.ddistdistance = node.squDistanceTo( pos );
			point.distance = point.ddistdistance;
			point.nodeIndex = current.nodeIndex;
			queue.offer( point );

			final int d = current.splitDim;
			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			final int leftIndex = node.getLeftIndex();
			final int rightIndex = node.getRightIndex();


			final double nodeposd = node.getPosition( d );

			// add the left branch
			if ( leftIndex != -1 )
			{
				final HeapElement left = new HeapElement();
				left.isPoint = false;
				left.nodeIndex = leftIndex;
				left.splitDim = dChild;
				System.arraycopy( current.xmax, 0, left.xmax, 0, n );
				System.arraycopy( current.xmin, 0, left.xmin, 0, n );
				left.xmax[ d ] = nodeposd;
				left.distance = distanceToBox( pos, left.xmin, left.xmax );



				System.arraycopy( current.dorient, 0, left.dorient, 0, n );
				System.arraycopy( current.ddist, 0, left.ddist, 0, n );
				left.ddistdistance = current.ddistdistance;
				if ( left.dorient[ d ] < 0 )
				{
					// do nothing
				}
				else if ( left.dorient[ d ] > 0 )
				{
					final double ddiff = nodeposd - pos[ d ];
					left.ddist[ d ] = ddiff * ddiff;
					left.ddistdistance += - current.ddist[ d ] + left.ddist[ d ];
				}
				else
				{
					final double ddiff = nodeposd - pos[ d ];
					if ( ddiff <= 0 ) // xmax <= pos
					{
						left.dorient[ d ] = 1;
						left.ddist[ d ] = ddiff * ddiff;
						left.ddistdistance += - current.ddist[ d ] + left.ddist[ d ];
					}
				}
				if ( Math.abs( left.distance - left.ddistdistance ) > 0.0000000001 )
					System.out.println( "oh noooooo! (left) " + Math.abs( left.distance - left.ddistdistance ) );


				queue.add( left );
			}

			// add the right branch
			if ( rightIndex != -1 )
			{
				final HeapElement right = new HeapElement();
				right.isPoint = false;
				right.nodeIndex = rightIndex;
				right.splitDim = dChild;
				System.arraycopy( current.xmax, 0, right.xmax, 0, n );
				System.arraycopy( current.xmin, 0, right.xmin, 0, n );
				right.xmin[ d ] = nodeposd;
				right.distance = distanceToBox( pos, right.xmin, right.xmax );



				System.arraycopy( current.dorient, 0, right.dorient, 0, n );
				System.arraycopy( current.ddist, 0, right.ddist, 0, n );
				right.ddistdistance = current.ddistdistance;
				if ( right.dorient[ d ] < 0 )
				{
					final double ddiff = nodeposd - pos[ d ];
					right.ddist[ d ] = ddiff * ddiff;
					right.ddistdistance += - current.ddist[ d ] + right.ddist[ d ];
				}
				else if ( right.dorient[ d ] > 0 )
				{
					// do nothing
				}
				else
				{
					final double ddiff = nodeposd - pos[ d ];
					if ( ddiff > 0 ) // pos < xmin
					{
						right.dorient[ d ] = -1;
						right.ddist[ d ] = ddiff * ddiff;
						right.ddistdistance += - current.ddist[ d ] + right.ddist[ d ];
					}
				}
				if ( Math.abs( right.distance - right.ddistdistance ) > 0.0000000001 )
					System.out.println( "oh noooooo! (right) " + Math.abs( right.distance - right.ddistdistance ) );



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

		// shortcuts?
		final int[] dorient = new int[ n ];
		final double[] ddist = new double[ n ];
		double ddistdistance;

		@Override
		public int compareTo( final HeapElement o )
		{
			return Double.compare( ddistdistance, o.ddistdistance );
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
}
