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

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;

import org.mastodon.RefPool;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.ref.RefRefHashMap;
import org.mastodon.pool.DoubleMappedElement;
import org.mastodon.pool.DoubleMappedElementArray;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
/**
 * KDTree of {@link RealLocalizable} objects.
 *
 * @param <O>
 *            type of objects stored in the tree.
 * @param <T>
 *            the MappedElement type of the {@link KDTreeNode tree nodes}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class KDTree<
			O extends RealLocalizable,
			T extends MappedElement >
		extends Pool< KDTreeNode< O, T >, T >
		implements RealInterval
{
	private static final MemPool.Factory< DoubleMappedElement > defaultPoolFactory = SingleArrayMemPool.factory( DoubleMappedElementArray.factory );

	/**
	 * Builds a KDTree of the given {@code objects}. The KDTree is stored in a
	 * {@link SingleArrayMemPool} of {@link DoubleMappedElement}s.
	 *
	 * @param objects
	 *            objects to build tree from.
	 * @param objectPool
	 *            the pool that contains the {@code objects}.
	 * @return the tree.
	 * @param <O>
	 *            the type of objects stored in the tree.
	 */
	public static < O extends RealLocalizable >
			KDTree< O, DoubleMappedElement > kdtree( final Collection< O > objects, final RefPool< O > objectPool )
	{
		return kdtree( objects, objectPool, defaultPoolFactory );
	}

	/**
	 * Builds a KDTree of the given {@code objects}.
	 *
	 * @param objects
	 *            objects to build tree from.
	 * @param objectPool
	 *            the pool that contains the {@code objects}.
	 * @param poolFactory
	 *            The {@link org.mastodon.pool.MemPool.Factory} that should be
	 *            used to create storage for {@link KDTreeNode nodes}
	 * @return the tree.
	 * @param <O>
	 *            the type of objects stored in the tree.
	 * @param <T>
	 *            the {@link MappedElement} type of the created pool of nodes.
	 */
	public static < O extends RealLocalizable, T extends MappedElement >
			KDTree< O, T > kdtree( final Collection< O > objects, final RefPool< O > objectPool, final MemPool.Factory< T > poolFactory )
	{
		final int capacity = objects.size();
		final int numDimensions = getNumDimensions( objects, objectPool );
		final KDTreeNodeLayout layout = new KDTreeNodeLayout( numDimensions );
		final KDTree< O, T > kdtree = new KDTree<>( capacity, layout, poolFactory, objectPool );
		kdtree.build( objects );
		return kdtree;
	}

	/**
	 * Creates a mapping from the objects stored in the specified tree and its
	 * nodes.
	 *
	 * @param kdtree
	 *            the tree to create the map from.
	 * @return a new map that links objects to tree nodes.
	 * @param <O>
	 *            the type of objects stored in the tree.
	 * @param <T>
	 *            the {@link MappedElement} type of the created pool of nodes.
	 */
	public static < O extends RealLocalizable, T extends MappedElement >
			RefRefMap< O, KDTreeNode< O, T > > createRefToKDTreeNodeMap( final KDTree< O, T > kdtree )
	{
		final RefPool< O > objPool = kdtree.getObjectPool();
		final O ref = objPool.createRef();
		final KDTreeNode< O, T > n = kdtree.createRef();
		final RefRefMap< O, KDTreeNode< O, T > > map = new RefRefHashMap<>( objPool, kdtree );
		for ( final KDTreeNode< O, T > node : kdtree )
		{
			final O obj = objPool.getObject( node.getDataIndex(), ref );
			map.put( obj, node, n );
		}
		objPool.releaseRef( ref );
		kdtree.releaseRef( n );
		return map;
	}

	static class KDTreeNodeLayout extends PoolObjectLayout
	{
		final DoubleArrayField position;
		final IndexField leftIndex;
		final IndexField rightIndex;
		final IndexField dataIndex;
		final IntField flags;

		KDTreeNodeLayout( final int n )
		{
			position = doubleArrayField( n );
			if ( java.nio.ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN )
			{
				rightIndex = indexField();
				leftIndex = indexField();
			}
			else
			{
				leftIndex = indexField();
				rightIndex = indexField();
			}
			dataIndex = indexField();
			flags = intField();
		}
	}

	final KDTreeNodeLayout layout;

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private KDTree(
			final int initialCapacity,
			final KDTreeNodeLayout layout,
			final MemPool.Factory< T > memPoolFactory,
			final RefPool< O > objectPool )
	{
		super( initialCapacity, layout, ( Class ) KDTreeNode.class, memPoolFactory, MemPool.FreeElementPolicy.UNCHECKED );
		this.layout = layout;
		this.n = layout.position.numElements();
		this.objectPool = objectPool;
		min = new double[ n ];
		max = new double[ n ];
		Arrays.fill( min, Double.POSITIVE_INFINITY );
		Arrays.fill( max, Double.NEGATIVE_INFINITY );
	}

	private static < O extends RealLocalizable > int getNumDimensions( final Collection< O > objects, final RefPool< O > objectPool )
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

	private final RefPool< O > objectPool;

	/**
	 * the number of dimensions.
	 */
	private final int n;

	/**
	 * minimum of each dimension.
	 */
	private final double[] min;

	/**
	 * maximum of each dimension.
	 */
	private final double[] max;

	int rootIndex;

	private void build( final Collection< O > objects )
	{
		final KDTreeNode< O, T > n1 = createRef();
		final KDTreeNode< O, T > n2 = createRef();
		final KDTreeNode< O, T > n3 = createRef();
		for ( final O obj : objects )
		{
			create( n1 ).init( obj );
			for ( int d = 0; d < n; ++d )
			{
				final double x = obj.getDoublePosition( d );
				if ( x < min[ d ] )
					min[ d ] = x;
				if ( x > max[ d ] )
					max[ d ] = x;
			}
		}
		final int max = objects.size() - 1;
		final int r = makeNode( 0, max, 0, n1, n2, n3 );
		releaseRef( n1 );
		releaseRef( n2 );
		releaseRef( n3 );
		rootIndex = r;
	}

	@Override
	protected KDTreeNode< O, T > createEmptyRef()
	{
		return new KDTreeNode<>( this, n );
	}

	double[] getDoubles()
	{
		if ( this.getMemPool() instanceof SingleArrayMemPool )
		{
			final SingleArrayMemPool< ?, ? > mempool = ( SingleArrayMemPool< ?, ? > ) this.getMemPool();
			if ( mempool.getDataArray() instanceof DoubleMappedElementArray )
			{
				final DoubleMappedElementArray doublearray = ( DoubleMappedElementArray ) mempool.getDataArray();
				return doublearray.getCurrentDataArray();
			}
		}
		return null;
	}

	RefPool< O > getObjectPool()
	{
		return objectPool;
	}

	/**
	 * Construct the tree by recursively adding nodes. The sublist of
	 * {@link KDTreeNode elements} between indices i and j (inclusive) is split
	 * at the median element with respect to coordinates in the given dimension
	 * d. The median becomes the new node which is returned. The left and right
	 * partitions of the sublist are processed recursively and form the left and
	 * right subtrees of the node.
	 *
	 * @param i
	 *            start index of sublist to process
	 * @param j
	 *            end index of sublist to process
	 * @param d
	 *            dimension along which to split the sublist
	 * @param n1
	 *            temporary {@link KDTreeNode} reference.
	 * @param n2
	 *            temporary {@link KDTreeNode} reference.
	 * @param n3
	 *            temporary {@link KDTreeNode} reference.
	 * @return index of the constructed node containing the subtree of the given
	 *         sublist of positions.
	 */
	private int makeNode( final int i, final int j, final int d, final KDTreeNode< O, T > n1, final KDTreeNode< O, T > n2, final KDTreeNode< O, T > n3 )
	{
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			kthElement( i, j, k, d, n1, n2, n3 );

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			final int left = makeNode( i, k - 1, dChild, n1, n2, n3 );
			final int right = makeNode( k + 1, j, dChild, n1, n2, n3 );

			getObject( k, n1 );
			n1.setLeftIndex( left );
			n1.setRightIndex( right );
			return k;
		}
		else if ( j == i )
		{
			// no left/right children
			getObject( i, n1 );
			n1.setLeftIndex( -1 );
			n1.setRightIndex( -1 );
			return i;
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Partition a sublist of KDTreeNodes such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal. Elements are compared by
	 * their coordinate in the specified dimension.s
	 *
	 * Note, that is is assumed that the {@link KDTreeNode}s are stored with
	 * consecutive indices in the pool.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param k
	 *            index for k-th smallest value. i &lt;= k &lt;= j.
	 * @param compare_d
	 *            dimension by which to compare.
	 * @param pivot
	 *            temporary {@link KDTreeNode} reference.
	 * @param ti
	 *            temporary {@link KDTreeNode} reference.
	 * @param tj
	 *            temporary {@link KDTreeNode} reference.
	 */
	private void kthElement( int i, int j, final int k, final int compare_d, final KDTreeNode< O, T > pivot, final KDTreeNode< O, T > ti, final KDTreeNode< O, T > tj )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, compare_d, pivot, ti, tj );
			if ( pivotpos > k )
			{
				// partition lower half
				j = pivotpos - 1;
			}
			else if ( pivotpos < k )
			{
				// partition upper half
				i = pivotpos + 1;
			}
			else
				break;
		}
	}

	/**
	 * Partition a sublist of KDTreeNodes by their coordinate in the specified
	 * dimension.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * Note, that is is assumed that the {@link KDTreeNode}s are stored with
	 * consecutive indices in the pool.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param compare_d
	 *            dimension by which to order the sublist
	 * @param pivot
	 *            temporary {@link KDTreeNode} reference.
	 * @param ti
	 *            temporary {@link KDTreeNode} reference.
	 * @param tj
	 *            temporary {@link KDTreeNode} reference.
	 * @return index of pivot element
	 */
	private int partitionSubList( int i, int j, final int compare_d, final KDTreeNode< O, T > pivot, final KDTreeNode< O, T > ti, final KDTreeNode< O, T > tj )
	{
		final int pivotIndex = j;
		getObject( j--, pivot );
		final double pivotPosition = pivot.getPosition( compare_d );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				getObject( i, ti );
				if ( ti.getPosition( compare_d ) >= pivotPosition )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				getObject( j, tj );
				if ( tj.getPosition( compare_d ) < pivotPosition )
				{
					// swap [j] with [i]
					getMemPool().swap( i++, j-- );
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		// check whether the element at iLastIndex is <
		if ( i != pivotIndex )
		{
			getMemPool().swap( i, pivotIndex );
		}
		return i;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public double realMin( final int d )
	{
		return min[ d ];
	}

	@Override
	public void realMin( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = min[ d ];
	}

	@Override
	public void realMin( final RealPositionable m )
	{
		m.setPosition( min );
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		m.setPosition( max );
	}
}
