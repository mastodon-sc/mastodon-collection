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
package org.mastodon.util;

import java.util.function.IntToDoubleFunction;

/**
 * Class for partially sorting a list. The class is used in the KDTree.
 */
public class KthElement
{
	private KthElement()
	{
		// prevent instantiation
	}

	/**
	 * Partially sort a sublist such that the element that would be at postition
	 * {@code k} in a sorted list is at position {@code k}, elements before the k-th
	 * are smaller or equal and elements after the k-th are larger or equal.
	 *
	 * @param i          index of first element of the sublist
	 * @param j          index of last element of the sublist
	 * @param k          index for k-th smallest value. i &lt;= k &lt;= j.
	 * @param rankMethod method that returns i-th rank of the i-th value in the list
	 * @param swapMethod method that swaps entry i and j in the list
	 */
	public static void kthElement( int i, int j, final int k, final IntToDoubleFunction rankMethod, final Swap swapMethod )
	{
		while ( i < j )
		{
			final int pivotpos = partitionSubList( i, j, rankMethod, swapMethod );
			if ( k < pivotpos )
			{
				// partition lower half
				j = pivotpos - 1;
			}
			else //if ( k >= pivotpos )
			{
				// partition upper half
				i = pivotpos;
			}
		}
	}

	/**
	 * Partition a sublist.
	 *
	 * The method does not swap entries for a correctly sorted list.
	 *
	 * @param left     index of first element of the sublist
	 * @param right    index of last element of the sublist
	 * @param rankMethod method that returns i-th rank of the i-th value in the list
	 * @param swapMethod method that swaps entry i and j in the list
	 * @return the index of the first element of the right partition
	 */
	static int partitionSubList( final int left, final int right, final IntToDoubleFunction rankMethod, final Swap swapMethod )
	{
		final double pivot = rankMethod.applyAsDouble( ( left + right ) / 2 );
		int i = left;
		int j = right;

		while ( true )
		{
			double ivalue = rankMethod.applyAsDouble( i );
			while ( ivalue < pivot )
			{
				++i;
				ivalue = rankMethod.applyAsDouble( i );
			}

			double jvalue = rankMethod.applyAsDouble( j );
			while ( pivot < jvalue )
			{
				--j;
				jvalue = rankMethod.applyAsDouble( j );
			}

			if ( i <= j )
			{
				if ( ivalue > jvalue ) // this avoids unnecessary swaps in case of ivalue = jvalue = pivot
					swapMethod.swap( i, j );
				++i;
				--j;
			}

			if ( i > j )
				return i;
		}
	}

	public interface Swap
	{
		void swap( int i, int j );
	}
}
