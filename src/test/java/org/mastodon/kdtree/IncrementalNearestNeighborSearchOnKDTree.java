package org.mastodon.kdtree;

import java.util.PriorityQueue;

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
		final HeapElement rootElement = new HeapElement( tree.rootIndex, 0 );
		rootElement.squDistance = 0;
		for ( int d = 0; d < n; ++d )
		{
			double diff = tree.realMin( d ) - pos[ d ];
			if ( diff > 0 ) // pos < xmin
			{
				rootElement.orient[ d ] = -1;
				rootElement.axisSquareDistance[ d ] = diff * diff;
				rootElement.squDistance += rootElement.axisSquareDistance[ d ];
			}
			else
			{
				diff = pos[ d ] - tree.realMax( d );
				if ( diff >= 0 ) // xmax <= pos
				{
					rootElement.orient[ d ] = 1;
					rootElement.axisSquareDistance[ d ] = diff * diff;
					rootElement.squDistance += rootElement.axisSquareDistance[ d ];
				}
			}
		}
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
				System.out.println( Math.sqrt( current.squDistance ) );
//				System.out.println( "visited " + i + " nodes" );
				continue;
			}

			// get one new point and two new boxes
			tree.getObject( current.nodeIndex, node );

			final int d = current.splitDim;
			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			final int leftIndex = node.getLeftIndex();
			final int rightIndex = node.getRightIndex();

			final double axisdiff = node.getPosition( d ) - pos[ d ];

			// add the left branch
			if ( leftIndex != -1 )
			{
				final HeapElement left = new HeapElement( leftIndex, dChild );

				System.arraycopy( current.orient, 0, left.orient, 0, n );
				System.arraycopy( current.axisSquareDistance, 0, left.axisSquareDistance, 0, n );
				left.squDistance = current.squDistance;
				if ( left.orient[ d ] < 0 )
				{
					// do nothing
				}
				else if ( left.orient[ d ] > 0 )
				{
					left.axisSquareDistance[ d ] = axisdiff * axisdiff;
					left.squDistance += - current.axisSquareDistance[ d ] + left.axisSquareDistance[ d ];
				}
				else
				{
					if ( axisdiff <= 0 ) // xmax <= pos
					{
						left.orient[ d ] = 1;
						left.axisSquareDistance[ d ] = axisdiff * axisdiff;
						left.squDistance += - current.axisSquareDistance[ d ] + left.axisSquareDistance[ d ];
					}
				}

				queue.add( left );
			}

			// add the right branch
			if ( rightIndex != -1 )
			{
				final HeapElement right = new HeapElement( rightIndex, dChild );

				System.arraycopy( current.orient, 0, right.orient, 0, n );
				System.arraycopy( current.axisSquareDistance, 0, right.axisSquareDistance, 0, n );
				right.squDistance = current.squDistance;
				if ( right.orient[ d ] < 0 )
				{
					right.axisSquareDistance[ d ] = axisdiff * axisdiff;
					right.squDistance += - current.axisSquareDistance[ d ] + right.axisSquareDistance[ d ];
				}
				else if ( right.orient[ d ] > 0 )
				{
					// do nothing
				}
				else
				{
					if ( axisdiff > 0 ) // pos < xmin
					{
						right.orient[ d ] = -1;
						right.axisSquareDistance[ d ] = axisdiff * axisdiff;
						right.squDistance += - current.axisSquareDistance[ d ] + right.axisSquareDistance[ d ];
					}
				}

				queue.add( right );
			}

			// add current node as a point
			current.isPoint = true;
			current.squDistance = node.squDistanceTo( pos );
			queue.offer( current );
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
		final NodeDataLayout layout;

		final IndexAttribute nodeIndex;

		final IntAttribute splitDim;

		final BooleanAttribute isPoint;

		final ByteArrayAttribute orient;

		final DoubleArrayAttribute axisSquareDistance;

		final DoubleAttribute squDistance;

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
			axisSquareDistance = new DoubleArrayAttribute<>( layout.axisSquareDistance, this );
			squDistance = new DoubleAttribute<>( layout.squDistance, this );
		}

		@Override
		protected NodeData createEmptyRef()
		{
			return new NodeData( this );
		}
	}

	static class NodeData extends PoolObject< NodeData, NodeDataPool, ByteMappedElement >
	{
		protected NodeData( final NodeDataPool pool )
		{
			super( pool );
		}

		@Override
		protected void setToUninitializedState()
		{}

//		public NodeData init( boolean isPoint, int nodeIndex, int splitDim ){};
	}

	class HeapElement implements Comparable< HeapElement >
	{

		public HeapElement( final int nodeIndex, final int splitDim )
		{
			this.nodeIndex = nodeIndex;
			this.splitDim = splitDim;
			this.isPoint = false;
		}

		final int nodeIndex;

		final int splitDim;

		/**
		 * true == point. false == box.
		 */
		boolean isPoint;

		/**
		 * for each dimension[d]:
		 * -1 : query[d] < boxmin[d]
		 *  0 : boxmin[d] <= query[d] < boxmax[d]
		 *  1 : boxmax[d] <= query[d]
		 */
		final int[] orient = new int[ n ];

		/**
		 * contribution to squared distance foe each dimension
		 */
		final double[] axisSquareDistance = new double[ n ];

		/**
		 * For point: squared distance to query point. For box: minimum squared
		 * distance from any point in box to query point.
		 *
		 * Sorts priority queue. Smaller means higher priority.
		 */
		double squDistance;

		@Override
		public int compareTo( final HeapElement o )
		{
			return Double.compare( squDistance, o.squDistance );
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
			}

			return builder.toString();
		}
	}
}
