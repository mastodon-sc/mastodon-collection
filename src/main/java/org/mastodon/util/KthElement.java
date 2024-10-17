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
