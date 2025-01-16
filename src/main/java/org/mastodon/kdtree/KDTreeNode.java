/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;
import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.RefPool;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

import net.imglib2.RealLocalizable;

/**
 *
 * @author Tobias Pietzsch
 *
 * @param <O>
 *            type of objects stored in the tree.
 * @param <T>
 *            the {@link MappedElement} type of the created pool of nodes.
 */
public class KDTreeNode<
			O extends RealLocalizable,
			T extends MappedElement >
		extends PoolObject< KDTreeNode< O, T >, KDTree< O, T >, T >
		implements RealLocalizable
{
	private final int n;

	private final RefPool< O > objPool;

	private final int POS_INDEX_OFFSET;
	private final int LEFT_INDEX_OFFSET;
	private final int RIGHT_INDEX_OFFSET;
	private final int DATA_INDEX_OFFSET;
	private final int FLAGS_OFFSET;

	private final int sizeInDoubles;

	public KDTreeNode( final KDTree< O, T > kdtree, final int numDimensions )
	{
		super( kdtree );
		this.objPool = kdtree.getObjectPool();
		n = numDimensions;
		POS_INDEX_OFFSET = kdtree.layout.position.getOffset();
		LEFT_INDEX_OFFSET = kdtree.layout.leftIndex.getOffset();
		RIGHT_INDEX_OFFSET = kdtree.layout.rightIndex.getOffset();
		FLAGS_OFFSET = kdtree.layout.flags.getOffset();
		DATA_INDEX_OFFSET = kdtree.layout.dataIndex.getOffset();
		sizeInDoubles = ( kdtree.layout.getSizeInBytes() + DOUBLE_SIZE - 1 ) / DOUBLE_SIZE;
	}

	protected int getLeftIndex()
	{
		return access.getIndex( LEFT_INDEX_OFFSET ) / sizeInDoubles;
	}

	protected void setLeftIndex( final int index )
	{
		access.putIndex( index * sizeInDoubles, LEFT_INDEX_OFFSET );
	}

	protected int getRightIndex()
	{
		return access.getIndex( RIGHT_INDEX_OFFSET ) / sizeInDoubles;
	}

	protected void setRightIndex( final int index )
	{
		access.putIndex( index * sizeInDoubles, RIGHT_INDEX_OFFSET );
	}

	protected int getDataIndex()
	{
		return access.getIndex( DATA_INDEX_OFFSET );
	}

	protected void setDataIndex( final int index )
	{
		access.putIndex( index, DATA_INDEX_OFFSET );
	}

	protected int getFlags()
	{
		return access.getInt( FLAGS_OFFSET );
	}

	protected void setFlags( final int flags )
	{
		access.putInt( flags, FLAGS_OFFSET );
	}

	protected double getPosition( final int d )
	{
		return access.getDouble( POS_INDEX_OFFSET + d * DOUBLE_SIZE );
	}

	protected void setPosition( final double position, final int d )
	{
		access.putDouble( position, POS_INDEX_OFFSET + d * DOUBLE_SIZE );
	}

	@Override
	protected void setToUninitializedState()
	{}

	protected void init( final O o )
	{
		setDataIndex( objPool.getId( o ) );
		setFlags( 0 );
		for ( int d = 0; d < o.numDimensions(); ++d )
			setPosition( o.getDoublePosition( d ), d );
	}

	protected static int sizeInBytes( final int n )
	{
		return 4 * INDEX_SIZE + n * DOUBLE_SIZE;
	}

	/**
	 * Compute the squared distance from p to this node.
	 *
	 * @param p
	 *            coordinates to compute squared distance to. Must have at least
	 *            <code>numDimensions</code> elements. Only the first
	 *            <code>numDimensions</code> elements are used.
	 * @return the square distance.
	 */
	public final float squDistanceTo( final float[] p )
	{
		float sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double diff = getPosition( d ) - p[ d ];
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 *
	 * @param p
	 *            coordinates to compute squared distance to. Must have at least
	 *            <code>numDimensions</code> elements. Only the first
	 *            <code>numDimensions</code> elements are used.
	 * @return the square distance.
	 */
	public final double squDistanceTo( final double[] p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double diff = getPosition( d ) - p[ d ];
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 *
	 * @param p
	 *            coordinates to compute squared distance to. Must be at least
	 *            <code>numDimensions</code>-dimensional.
	 * @return the square distance.
	 */
	public final double squDistanceTo( final RealLocalizable p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double diff = getPosition( d ) - p.getDoublePosition( d );
			sum += diff * diff;
		}
		return sum;
	}

	public boolean isValid()
	{
		return getFlags() == 0;
	}

	public void setValid( final boolean valid )
	{
		setFlags( valid ? 0 : 1 );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) getPosition( d );
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = getPosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return ( float ) getPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getPosition( d );
	}
}
