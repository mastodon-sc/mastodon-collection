/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.kdtree;

import java.util.Arrays;
import java.util.Queue;

import org.mastodon.collection.ref.IntRefArrayMap;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.BooleanAttribute;
import org.mastodon.pool.attributes.DoubleArrayAttribute;
import org.mastodon.pool.attributes.IndexAttribute;
import org.mastodon.pool.attributes.IntArrayAttribute;
import org.mastodon.pool.attributes.IntAttribute;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.imglib2.RealLocalizable;

/**
 * Implementation of incremental nearest neighbor search for kd-trees.
 * 
 * @param <O>
 *            type of objects stored in the tree.
 * @param <T>
 *            the {@link MappedElement} type of the created pool of nodes.
 * 
 * @author Tobias Pietzsch
 */
public final class IncrementalNearestNeighborSearchOnKDTree< O extends RealLocalizable, T extends MappedElement >
		implements IncrementalNearestNeighborSearch< O >
{
	private final KDTree< O, T > tree;

	private final int n;

	private final double[] pos;

	private final KDTreeNode< O, T > node;

	private final O obj;

	private final NodeDataPool pool;

	private final NodeDataQueue queue;

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
		queue = new NodeDataQueue( pool );
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
		queue = new NodeDataQueue( pool );
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
	public double getSquareDistance()
	{
		return queue.peek( ref1 ).getSquDistance();
	}

	@Override
	public double getDistance()
	{
		return Math.sqrt( queue.peek( ref1 ).getSquDistance() );
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

		while ( !queue.isEmpty() )
		{
			final NodeData current = queue.peek( ref1 );
			if ( current.isPoint() )
				break;

			// create (at most) two new boxes for left and right child box
			tree.getObject( current.getNodeIndex(), node );
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

		hasNext = queue.size() > 1;
	}

	static class NodeDataLayout extends PoolObjectLayout
	{
		final IndexField nodeIndex;
		final IntField splitDim;
		final BooleanField isPoint;
		final IntArrayField orient;
		final DoubleArrayField axisSquareDistance;

		NodeDataLayout( final int numDimensions )
		{
			this.numDimensions = numDimensions;
			nodeIndex = indexField();
			splitDim = intField();
			isPoint = booleanField();
			orient = intArrayField( numDimensions );
			axisSquareDistance = doubleArrayField( numDimensions );
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
			super.create( obj );
			if ( distances.length <= obj.getInternalPoolIndex() )
				distances = Arrays.copyOf( distances, distances.length * 2 );
			return obj;
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
		final IntArrayAttribute< NodeData > orient;

		/**
		 * contribution to squared distance foe each dimension
		 */
		final DoubleArrayAttribute< NodeData > axisSquDistance;

		NodeDataPool( final int numDimensions )
		{
			this( new NodeDataLayout( numDimensions ) );
		}

		/**
		 * For point: squared distance to query point. For box: minimum squared
		 * distance from any point in box to query point.
		 *
		 * Sorts priority queue. Smaller means higher priority.
		 */
		double[] distances = new double[ 256 ];

		private NodeDataPool( final NodeDataLayout layout )
		{
			super( 256, layout, NodeData.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
			this.layout = layout;

			nodeIndex = new IndexAttribute<>( layout.nodeIndex, this );
			splitDim = new IntAttribute<>( layout.splitDim, this );
			isPoint = new BooleanAttribute<>( layout.isPoint, this );
			orient = new IntArrayAttribute<>( layout.orient, this );
			axisSquDistance = new DoubleArrayAttribute<>( layout.axisSquareDistance, this );
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

		final int copystart;

		final int copylength;

		protected NodeData( final NodeDataPool pool )
		{
			super( pool );
			n = pool.layout.numDimensions;
			copystart = pool.layout.orient.getOffset();
			copylength = pool.layout.axisSquareDistance.getOffset() + pool.layout.axisSquareDistance.getSizeInBytes() - copystart;
		}

		@Override
		protected void setToUninitializedState()
		{}

		@Override
		public int compareTo( final NodeData o )
		{
			return Double.compare( pool.distances[ getInternalPoolIndex() ], pool.distances[ o.getInternalPoolIndex() ] );
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
			return pool.distances[ getInternalPoolIndex() ];
		}

		public void setSquDistance( final double value )
		{
			pool.distances[ getInternalPoolIndex() ] = value;
		}

		public NodeData init( final int nodeIndex, final int splitDim )
		{
			pool.isPoint.setQuiet( this, false );
			pool.nodeIndex.setQuiet( this, nodeIndex );
			pool.splitDim.setQuiet( this, splitDim );
			for ( int d = 0; d < n; ++d )
			{
				pool.orient.setQuiet( this, d, 0 );
				pool.axisSquDistance.setQuiet( this, d, 0 );
			}
			setSquDistance( 0 );
			return this;
		}

		public NodeData init( final int nodeIndex, final int splitDim, final NodeData parent )
		{
			pool.isPoint.setQuiet( this, false );
			pool.nodeIndex.setQuiet( this, nodeIndex );
			pool.splitDim.setQuiet( this, splitDim );
			access.copy( parent.access, copystart, copylength );
			return this;
		}

		public NodeData init( final NodeData other )
		{
			access.copy( other.access, 0, copystart + copylength );
			setSquDistance( other.getSquDistance() );
			return this;
		}
	}

	static class NodeDataQueue
	{
		private final NodeDataPool pool;

		private final RefArrayList< NodeData > heap;

		public NodeDataQueue( final NodeDataPool pool )
		{
			this( pool, Constants.DEFAULT_CAPACITY );
		}

		public NodeDataQueue( final NodeDataPool pool, final int initialCapacity )
		{
			this.pool = pool;
			heap = new RefArrayList<>( pool, initialCapacity );
		}

		public TIntList getIndexCollection()
		{
			return heap.getIndexCollection();
		}

		public boolean isEmpty()
		{
			return heap.isEmpty();
		}

		public int size()
		{
			return heap.size();
		}

		/**
		 * Inserts the specified element into this priority queue.
		 * 
		 * @param obj
		 *            the object to insert.
		 * @return {@code true} (as specified by {@link Queue#offer})
		 * @throws ClassCastException
		 *             if the specified element cannot be compared with elements
		 *             currently in this priority queue according to the
		 *             priority queue's ordering
		 * @throws NullPointerException
		 *             if the specified element is null
		 */
		public boolean offer( final NodeData obj )
		{
			if ( obj == null )
				throw new NullPointerException();
			heap.add( obj );
			siftUp( heap.size() - 1 );
			return true;
		}

		public NodeData peek( final NodeData obj )
		{
			return heap.isEmpty() ? null : heap.getQuick( 0, obj );
		}

		public NodeData poll( final NodeData obj )
		{
			switch ( heap.size() )
			{
			case 0:
				return null;
			case 1:
				return heap.remove( 0, obj );
			default:
				heap.getQuick( 0, obj );
				final TIntArrayList indices = heap.getIndexCollection();
				indices.set( 0, indices.removeAt( heap.size() - 1 ) );
				siftDown( 0 );
				return obj;
			}
		}

		public void reset()
		{
			heap.resetQuick();
		}

		void siftDown( int i )
		{
			final TIntArrayList indices = heap.getIndexCollection();

			final int parentPoolIndex = indices.getQuick( i );
			final double parentDistance = pool.distances[ parentPoolIndex ];
			final int size = heap.size();
			for ( int j = ( i << 1 ) + 1; j < size; i = j, j = ( i << 1 ) + 1 )
			{
				if ( j + 1 < size &&  pool.distances[ indices.getQuick( j + 1 ) ] < pool.distances[ indices.getQuick( j ) ] )
					++j;
				if ( parentDistance > pool.distances[ indices.getQuick( j ) ] )
					indices.set( i, indices.getQuick( j ) );
				else
					break;
			}
			indices.set( i, parentPoolIndex );
		}

		void siftUp( int i )
		{
			final TIntArrayList indices = heap.getIndexCollection();

			final int childPoolIndex = indices.getQuick( i );
			final double childDistance = pool.distances[ childPoolIndex ];
			while ( i > 0 )
			{
				final int pi = ( i - 1 ) >>> 1;
				final int parentPoolIndex = heap.getIndexCollection().getQuick( pi);
				if ( childDistance >= pool.distances[ parentPoolIndex ] )
					break;
				indices.set( i, parentPoolIndex );
				i = pi;
			}
			indices.set( i, childPoolIndex );
		}
	}
}
