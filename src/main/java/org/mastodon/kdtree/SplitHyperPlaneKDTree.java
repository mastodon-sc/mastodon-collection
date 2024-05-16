/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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

import gnu.trove.list.array.TIntArrayList;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.kdtree.HyperPlane;

/**
 * Partition nodes in a {@link KDTree} into disjoint sets of nodes that are
 * above and below a given hyperplane, respectively.
 *
 * <p>
 * Construct with the {@link KDTree}. Call {@link #split(HyperPlane)} to
 * partition with respect to a {@link HyperPlane}. Then call
 * {@link #getAboveValues()} and {@link #getBelowValues()} to get the sets of
 * node values above and below the hyperplane, respectively.
 *
 * <p>
 * The algorithm is described in <a
 * href="http://fly.mpi-cbg.de/~pietzsch/polytope.pdf">this note</a>.
 *
 * @param <O>
 *            type of objects stored in the tree.
 * @param <T>
 *            the MappedElement type of the {@link KDTreeNode tree nodes}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class SplitHyperPlaneKDTree< O extends RealLocalizable, T extends MappedElement >
{
	private final KDTree< O, T > tree;

	private final int n;

	private final double[] normal;

	private double m;

	private final double[] xmin;

	private final double[] xmax;

	private final TIntArrayList aboveNodes;

	private final TIntArrayList aboveSubtrees;

	private final TIntArrayList belowNodes;

	private final TIntArrayList belowSubtrees;

	private final KDTreeNode< O, T > current;

	private final FastDoubleSearch fastDoubleSearch;

	public SplitHyperPlaneKDTree( final KDTree< O, T > tree )
	{
		n = tree.numDimensions();
		xmin = new double[ n ];
		xmax = new double[ n ];
		normal = new double[ n ];
		this.tree = tree;
		aboveNodes = new TIntArrayList();
		aboveSubtrees = new TIntArrayList();
		belowNodes = new TIntArrayList();
		belowSubtrees = new TIntArrayList();
		current = tree.createRef();
		fastDoubleSearch = ( tree.getDoubles() != null ) ? new FastDoubleSearch() : null;
	}

	public int numDimensions()
	{
		return n;
	}

	public void split( final HyperPlane plane )
	{
		if ( tree.size() <= 0 )
			return;

		initNewSearch();
		System.arraycopy( plane.getNormal(), 0, normal, 0, n );
		m = plane.getDistance();
		if ( fastDoubleSearch != null )
			fastDoubleSearch.split();
		else
			split( tree.rootIndex, 0 );
	}

	public void split( final double[] plane )
	{
		if ( tree.size() <= 0 )
			return;

		initNewSearch();
		System.arraycopy( plane, 0, normal, 0, n );
		m = plane[ n ];
		if ( fastDoubleSearch != null )
			fastDoubleSearch.split();
		else
			split( tree.rootIndex, 0 );
	}

	private void initNewSearch()
	{
		aboveNodes.clear();
		aboveSubtrees.clear();
		belowNodes.clear();
		belowSubtrees.clear();
		tree.realMin( xmin );
		tree.realMax( xmax );
	}

	public Iterable< O > getAboveValues()
	{
		return new KDTreeValueIterable<>( aboveNodes, aboveSubtrees, tree, fastDoubleSearch != null );
	}

	public Iterable< O > getBelowValues()
	{
		return new KDTreeValueIterable<>( belowNodes, belowSubtrees, tree, fastDoubleSearch != null );
	}

	private boolean allAbove()
	{
		double dot = 0;
		for ( int d = 0; d < n; ++d )
			dot += normal[ d ] * ( normal[ d ] >= 0 ? xmin[ d ] : xmax[ d ] );
		return dot >= m;
	}

	private boolean allBelow()
	{
		double dot = 0;
		for ( int d = 0; d < n; ++d )
			dot += normal[ d ] * ( normal[ d ] < 0 ? xmin[ d ] : xmax[ d ] );
		return dot < m;
	}

	private void splitSubtree( final int currentNodeIndex, final int parentsd, final boolean p, final boolean q )
	{
		if ( p && q && allAbove() )
			aboveSubtrees.add( currentNodeIndex );
		else if ( !p && !q && allBelow() )
			belowSubtrees.add( currentNodeIndex );
		else
			split( currentNodeIndex, parentsd + 1 == n ? 0 : parentsd + 1 );
	}

	private void split( final int currentNodeIndex, final int sd )
	{
		// consider the current node
		tree.getObject( currentNodeIndex, current );
		final double sc = current.getDoublePosition( sd );
		final int left = current.getLeftIndex();
		final int right = current.getRightIndex();

		double dot = 0;
		for ( int d = 0; d < n; ++d )
			dot += current.getDoublePosition( d ) * normal[ d ];
		final boolean p = dot >= m;

		// current
		if ( p )
			aboveNodes.add( currentNodeIndex );
		else
			belowNodes.add( currentNodeIndex );

		// left
		if ( left >= 0 )
		{
			final double max = xmax[ sd ];
			xmax[ sd ] = sc;
			splitSubtree( left, sd, p, normal[ sd ] < 0 );
			xmax[ sd ] = max;
		}

		// right
		if ( right >= 0 )
		{
			final double min = xmin[ sd ];
			xmin[ sd ] = sc;
			splitSubtree( right, sd, p, normal[ sd ] >= 0 );
			xmin[ sd ] = min;
		}
	}

	private final class FastDoubleSearch
	{
		private final int nodeSizeInDoubles;

		private final double[] doubles;

		private final int doublesRootIndex;

		private FastDoubleSearch()
		{
			nodeSizeInDoubles = n + 2;
			doubles = tree.getDoubles();
			doublesRootIndex = tree.rootIndex * nodeSizeInDoubles;
		}

		private void split()
		{
			split( doublesRootIndex, 0 );
		}

		private void splitSubtree( final int currentIndex, final int parentsd, final boolean p, final boolean q )
		{
			if ( p && q && allAbove() )
				aboveSubtrees.add( currentIndex );
			else if ( !p && !q && allBelow() )
				belowSubtrees.add( currentIndex );
			else
				split( currentIndex, parentsd + 1 == n ? 0 : parentsd + 1 );
		}

		private void split( final int currentIndex, final int sd )
		{
			// consider the current node
			final double sc = doubles[ currentIndex + sd ];
			final long leftright = Double.doubleToRawLongBits( doubles[ currentIndex + n ] );
			final int left = ( int ) ( leftright >> 32 );
			final int right = ( int ) leftright;

			double dot = 0;
			for ( int d = 0; d < n; ++d )
				dot += doubles[ currentIndex + d ] * normal[ d ];
			final boolean p = dot >= m;

			// current
			if ( p )
				aboveNodes.add( currentIndex );
			else
				belowNodes.add( currentIndex );

			// left
			if ( left >= 0 )
			{
				final double max = xmax[ sd ];
				xmax[ sd ] = sc;
				splitSubtree( left, sd, p, normal[ sd ] < 0 );
				xmax[ sd ] = max;
			}

			// right
			if ( right >= 0 )
			{
				final double min = xmin[ sd ];
				xmin[ sd ] = sc;
				splitSubtree( right, sd, p, normal[ sd ] >= 0 );
				xmin[ sd ] = min;
			}
		}
	}
}
