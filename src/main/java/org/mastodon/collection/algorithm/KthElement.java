/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.collection.algorithm;

import java.util.Comparator;

import org.mastodon.collection.RefList;

public class KthElement< O >
{
	private final RefList< O > list;

	/**
	 * temporary {@code O} reference.
	 */
	private final O pivotRef;

	/**
	 * temporary {@code O} reference.
	 */
	private final O tiRef;

	/**
	 * temporary {@code O} reference.
	 */
	private final O tjRef;

	public KthElement( final RefList< O > list )
	{
		this.list = list;
		pivotRef = list.createRef();
		tiRef = list.createRef();
		tjRef = list.createRef();
	}

	public void kthElement( final int k, final Comparator< O > comparator )
	{
		kthElement( 0, list.size() - 1, k, comparator );
	}

	/**
	 * Partition a sublist of {@code RefList<O>} such that the k-th smallest
	 * value is at position {@code k}, elements before the k-th are smaller or
	 * equal and elements after the k-th are larger or equal. Elements are
	 * compared by the specified {@code comparator}.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param k
	 *            index for k-th smallest value. i &lt;= k &lt;= j.
	 * @param comparator
	 *            element order
	 */
	public void kthElement( int i, int j, final int k, final Comparator< O > comparator )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, comparator );
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
	 * Partition a sublist of {@code RefList<O>} by the specified
	 * {@code comparator}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param comparator
	 *            element order
	 * @return index of pivot element
	 */
	private int partitionSubList( int i, int j, final Comparator< O > comparator )
	{
		final int pivotIndex = j;
		final O pivot = list.get( j--, pivotRef );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final O ti = list.get( i, tiRef );
				if ( comparator.compare( ti, pivot ) >= 0 )
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
				final O tj = list.get( j, tjRef );
				if ( comparator.compare( tj, pivot ) < 0 )
				{
					// swap [j] with [i]
					list.swap( i++, j-- );
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
			list.swap( i, pivotIndex );
		}
		return i;
	}
}
