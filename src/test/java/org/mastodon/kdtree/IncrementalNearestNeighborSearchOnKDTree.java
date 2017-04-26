package org.mastodon.kdtree;

import java.util.PriorityQueue;

import org.mastodon.collection.ref.RefArrayHeap;
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

	private final NodeDataPool pool;

	public IncrementalNearestNeighborSearchOnKDTree( final KDTree< O, T > tree )
	{
		n = tree.numDimensions();
		pool = new NodeDataPool( n );
		queue2 = new RefArrayHeap<>( pool );
		pos = new double[ n ];
		this.tree = tree;
		this.node = tree.createRef();
	}

	public int numDimensions()
	{
		return n;
	}

	PriorityQueue< HeapElement > queue = new PriorityQueue<>();

	RefArrayHeap< NodeData > queue2;

	public void search( final RealLocalizable p )
	{
		if ( tree.size() <= 0 )
			return;

		queue.clear();
		queue2.clear();
		pool.clear();
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
		queue.offer( rootElement );

		final NodeData rootElement2 = pool.create().init( tree.rootIndex, 0 );
		double squDistance = 0;
		for ( int d = 0; d < n; ++d )
		{
			double diff = tree.realMin( d ) - pos[ d ];
			if ( diff > 0 ) // pos < xmin
			{
				rootElement2.setOrient( d, -1 );
				rootElement2.setAxisSquDistance( d, diff * diff );
				squDistance += diff * diff;
			}
			else
			{
				diff = pos[ d ] - tree.realMax( d );
				if ( diff >= 0 ) // xmax <= pos
				{
					rootElement2.setOrient( d, 1 );
					rootElement2.setAxisSquDistance( d, diff * diff );
					squDistance += diff * diff;
				}
			}
		}
		rootElement2.setSquDistance( squDistance );
		queue2.offer( rootElement2 );

		int i = 0;
		while ( true )
		{
			System.out.println(  );
			++i;
			final NodeData current2 = queue2.poll();
			System.out.println( "queue2.poll(): " + current2 );
			if ( current2 == null )
				break;

			final HeapElement current = queue.poll();
			System.out.println( "queue.poll(): " + current );
			if ( current == null )
				break;

			if ( current2.isPoint() )
			{
//				System.out.println( "found " + current2 );
				System.out.println( " ----> " + Math.sqrt( current2.getSquDistance() ) );
//				System.out.println( "visited " + i + " nodes" );
			}
			if ( current.isPoint )
			{
//				System.out.println( "found " + current );
				System.out.println( Math.sqrt( current.squDistance ) );
//				System.out.println( "visited " + i + " nodes" );
				continue;
			}


			// get one new point and two new boxes
			tree.getObject( current2.getNodeIndex(), node );

			final int d2 = current2.getSplitDim();
			final int dChild2 = ( d2 + 1 == n ) ? 0 : d2 + 1;
			final int leftIndex2 = node.getLeftIndex();
			final int rightIndex2 = node.getRightIndex();

			final double axisdiff2 = node.getPosition( d2 ) - pos[ d2 ];

			// add the left branch
			if ( leftIndex2 != -1 )
			{
				final NodeData left = pool.create().init( leftIndex2, dChild2, current2 );

				final int o = left.getOrient( d2 );
				if ( o > 0 || axisdiff2 <= 0 ) // xmax <= pos
				{
					if ( o == 0 )
						left.setOrient( d2, 1 );
					final double asd2 = axisdiff2 * axisdiff2;
					left.setAxisSquDistance( d2, asd2 );
					left.setSquDistance( left.getSquDistance() - current2.getAxisSquDistance( d2 ) + asd2 );
				}
				queue2.offer( left );
			}

			// add the right branch
			if ( rightIndex2 != -1 )
			{
				final NodeData right = pool.create().init( rightIndex2, dChild2, current2 );

				final int o = right.getOrient( d2 );
				if ( o < 0 || axisdiff2 > 0 ) // pos < xmin
				{
					if ( o == 0 )
						right.setOrient( d2, -1 );
					final double asd2 = axisdiff2 * axisdiff2;
					right.setAxisSquDistance( d2, asd2 );
					right.setSquDistance( right.getSquDistance() - current2.getAxisSquDistance( d2 ) + asd2 );
				}
				queue2.offer( right );
			}

			// add current node as a point
			current2.setIsPoint( true );
			current2.setSquDistance( node.squDistanceTo( pos ) );
			queue2.offer( current2 );






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

	class NodeDataPool extends Pool< NodeData, ByteMappedElement >
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

		@SuppressWarnings( { "rawtypes", "unchecked" } )
		private NodeDataPool( final NodeDataLayout layout )
		{
			super( 50, layout, ( Class ) NodeData.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
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

	class NodeData extends PoolObject< NodeData, NodeDataPool, ByteMappedElement > implements Comparable< NodeData >
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

		@Override
		public String toString()
		{
			final StringBuilder builder = new StringBuilder();

			builder.append( "(" );
			builder.append( getNodeIndex() );
			builder.append( ") " );

			if ( isPoint() )
			{
				builder.append( "point " );

				final KDTreeNode< O, T > node = tree.createRef();
				final O obj = tree.getObjectPool().createRef();
				tree.getObject( getNodeIndex(), node );
				builder.append( tree.getObjectPool().getObject( node.getDataIndex(), obj ).toString() );
				tree.getObjectPool().releaseRef( obj );
				tree.releaseRef( node );
			}
			else
			{
				builder.append( "box " );
				builder.append( "split d=" );
				builder.append( getSplitDim() );
			}

			return builder.toString();
		}
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
