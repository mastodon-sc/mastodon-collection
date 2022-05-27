/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.pool.MappedElement;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * Implementation of {@link NearestNeighborSearch} search for kd-trees.
 * 
 * @param <O>
 *            the type of points.
 * @param <T>
 *            the type of mapping for the points.
 *
 * @author Tobias Pietzsch
 */
public final class NearestValidNeighborSearchOnKDTree< O extends RealLocalizable, T extends MappedElement >
	implements NearestNeighborSearch< O >, Sampler< O >
{
	private final KDTree< O, T > tree;

	private final int n;

	private final double[] pos;

	private final KDTreeNode< O, T > node;

	private int bestPointNodeIndex;

	private double bestSquDistance;

	private final O obj;

	private final FastDoubleSearch fastDoubleSearch;

	public NearestValidNeighborSearchOnKDTree( final KDTree< O, T > tree )
	{
		n = tree.numDimensions();
		pos = new double[ n ];
		bestPointNodeIndex = -1;
		bestSquDistance = Double.POSITIVE_INFINITY;
		this.tree = tree;
		this.node = tree.createRef();
		this.obj = tree.getObjectPool().createRef();
		this.fastDoubleSearch = ( tree.getDoubles() != null ) ? new FastDoubleSearch( tree ) : null;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public void search( final RealLocalizable p )
	{
		if ( tree.size() <= 0 )
			return;

		if ( fastDoubleSearch != null )
		{
			fastDoubleSearch.search( p );
			bestPointNodeIndex = fastDoubleSearch.getBestPointNodeIndex();
			bestSquDistance = fastDoubleSearch.getBestSquDistance();
		}
		else
		{
			p.localize( pos );
			bestPointNodeIndex = -1;
			bestSquDistance = Double.POSITIVE_INFINITY;
			searchNode( tree.rootIndex, 0 );
		}
	}

	private void searchNode( final int currentNodeIndex, final int d )
	{
		// consider the current node
		tree.getObject( currentNodeIndex, node );
		final double distance = node.squDistanceTo( pos );
		if ( distance < bestSquDistance && node.isValid() )
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

	@Override
	public Sampler< O > getSampler()
	{
		return this;
	}

	@Override
	public RealLocalizable getPosition()
	{
		if ( bestPointNodeIndex == -1 )
			return null;

		tree.getObject( bestPointNodeIndex, node );
		return node;
	}

	@Override
	public double getSquareDistance()
	{
		return bestSquDistance;
	}

	@Override
	public double getDistance()
	{
		return Math.sqrt( bestSquDistance );
	}

	@Override
	public O get()
	{
		if ( bestPointNodeIndex == -1 )
			return null;

		tree.getObject( bestPointNodeIndex, node );
		return tree.getObjectPool().getObject( node.getDataIndex(), obj );
	}

	@Override
	public NearestValidNeighborSearchOnKDTree< O, T > copy()
	{
		final NearestValidNeighborSearchOnKDTree< O, T > copy = new NearestValidNeighborSearchOnKDTree<>( tree );
		System.arraycopy( pos, 0, copy.pos, 0, pos.length );
		copy.bestPointNodeIndex = bestPointNodeIndex;
		copy.bestSquDistance = bestSquDistance;
		return copy;
	}

	private static final class FastDoubleSearch
	{
		private final int n;

		private final int nodeSizeInDoubles;

		private final double[] pos;

		private int bestIndex;

		private double bestSquDistance;

		private final double[] doubles;

		private final int doublesRootIndex;

		private final double[] axisDiffs;

		private final int[] awayChildNodeIndices;

		private final int[] ds;

		FastDoubleSearch( final KDTree< ?, ? > tree )
		{
			n = tree.numDimensions();
			nodeSizeInDoubles = n + 2;
			final int depth = ( tree.size() <= 0 ) ? 0 :
				( int ) ( Math.log( tree.size() ) / Math.log( 2 ) ) + 2;
			pos = new double[ n ];
			bestIndex = -1;
			bestSquDistance = Double.POSITIVE_INFINITY;
			doubles = tree.getDoubles();
			doublesRootIndex = tree.rootIndex * nodeSizeInDoubles;
			axisDiffs = new double[ depth ];
			awayChildNodeIndices = new int[ depth ];
			ds = new int[ depth ];
			for ( int i = 0; i < depth; ++i )
				ds[ i ] = i % n;
		}

		void search( final RealLocalizable p )
		{
			p.localize( pos );
			int currentIndex = doublesRootIndex;
			int depth = 0;
			double bestSquDistanceL = Double.POSITIVE_INFINITY;
			int bestIndexL = -1;
			while ( true )
			{
				final double distance = squDistance( currentIndex );
				if ( distance < bestSquDistanceL )
				{
					final int flags = ( int ) ( Double.doubleToRawLongBits( doubles[ currentIndex + n + 1 ] ) >> 32 );
					if ( flags == 0 ) // if node is valid
					{
						bestSquDistanceL = distance;
						bestIndexL = currentIndex;
					}
				}

				final int d = ds[ depth ];
				final double axisDiff = pos[ d ] - doubles[ currentIndex + d ];
				final boolean leftIsNearBranch = axisDiff < 0;

				final long leftright = Double.doubleToRawLongBits( doubles[ currentIndex + n ] );
				final int left = ( int ) ( leftright >> 32 );
				final int right = ( int ) leftright;

				// search the near branch
				final int nearChildNodeIndex = leftIsNearBranch ? left : right;
				final int awayChildNodeIndex = leftIsNearBranch ? right : left;
				++depth;
				awayChildNodeIndices[ depth ] = awayChildNodeIndex;
				axisDiffs[ depth ] = axisDiff * axisDiff;
				if ( nearChildNodeIndex < 0 )
				{
					while ( awayChildNodeIndices[ depth ] < 0 || axisDiffs[ depth ] > bestSquDistanceL )
						if ( --depth == 0 )
						{
							bestSquDistance = bestSquDistanceL;
							bestIndex = bestIndexL;
							return;
						}
					currentIndex = awayChildNodeIndices[ depth ];
					awayChildNodeIndices[ depth ] = -1;
				}
				else
					currentIndex = nearChildNodeIndex;
			}
		}

		int getBestPointNodeIndex()
		{
			return bestIndex == -1 ? -1 : bestIndex / nodeSizeInDoubles;
		}

		double getBestSquDistance()
		{
			return bestSquDistance;
		}

		private double squDistance( final int index )
		{
			double sum = 0;
			for ( int d = 0; d < n; ++d )
			{
				final double diff = ( pos[ d ] - doubles[ index + d ] );
				sum += diff * diff;
			}
			return sum;
		}
	}
}
