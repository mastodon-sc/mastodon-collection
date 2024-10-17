package org.mastodon.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

import org.junit.Test;

/**
 * Tests for {@link KthElement}.
 */
public class KthElementTest
{
	@Test
	public void test()
	{
		testKthElement( 1, new int[] { 3, 2, 1 } );
		testKthElement( 4, new int[] { 4, 3, 2, 1, 5 } );
		testKthElement( 0, new int[] { 0, 3, 2, 1, 5 } );
	}

	private static void testKthElement( final int k, final int[] originalList )
	{
		final int[] sortedList = originalList.clone();
		KthElement.kthElement( 0, sortedList.length - 1, k, rankMethod( sortedList ), swapMethod( sortedList ) );
		assertCorrectPartialSorting( k, originalList, sortedList );
	}

	private static void assertCorrectPartialSorting( final int k, final int[] original, final int[] partiallySorted )
	{
		assertArrayEquals( sortedArray( original ), sortedArray( partiallySorted ) );
		for ( int i = 0; i < k; i++ )
			assertTrue( partiallySorted[ i ] <= partiallySorted[ k ] );
		for ( int i = k + 1; i < partiallySorted.length; i++ )
			assertTrue( partiallySorted[ i ] >= partiallySorted[ k ] );
	}

	@Test
	public void testRandomized()
	{
		repeatedlyTestKthElement( 1000, 10, 5 );
		repeatedlyTestKthElement( 1000, 10, 0 );
		repeatedlyTestKthElement( 1000, 10, 9 );
		repeatedlyTestKthElement( 1000, 2, 1 );
		repeatedlyTestKthElement( 1000, 2, 0 );
		repeatedlyTestKthElement( 1000, 1, 0 );
		repeatedlyTestKthElement( 10, 1000, 10 );
	}

	private static void repeatedlyTestKthElement( final int repeats, final int numberOfValues, final int k )
	{
		for ( int seed = 0; seed < repeats; seed++ )
			testKthElement( k, randomInts( seed, numberOfValues ) );
	}

	@Test
	public void testPartitionSubList_forConstantList()
	{
		final int[] values = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		assertEquals( 5, KthElement.partitionSubList( 0, 9, rankMethod( values ), swapMethod( values ) ) );
	}

	@Test
	public void testNoSwapForConstantList()
	{
		final int[] values = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		final KthElement.Swap swap = ( i, j ) -> {
			throw new AssertionError( "Swap should not be called on a constant list." );
		};
		KthElement.kthElement( 0, 9, 5, rankMethod( values ), swap );
	}

	@Test
	public void testNoSwapForSortedList()
	{
		final int[] values = new int[] { 1, 2, 3, 5, 5, 5, 8, 8, 8, 9 };
		final KthElement.Swap swap = ( i, j ) -> {
			throw new AssertionError( "Swap should not be called on sorted list." );
		};
		KthElement.kthElement( 0, 9, 5, rankMethod( values ), swap );
	}

	private static int[] sortedArray( final int[] array )
	{
		final int[] sorted = array.clone();
		Arrays.sort( sorted );
		return sorted;
	}

	private static int[] randomInts( final int seed, final int count )
	{
		return new Random( seed ).ints( count, 0, count ).toArray();
	}

	private static IntToDoubleFunction rankMethod( final int[] values )
	{
		return i -> values[ i ];
	}

	private static KthElement.Swap swapMethod( final int[] values )
	{
		return ( i, j ) -> {
			final int temp = values[ i ];
			values[ i ] = values[ j ];
			values[ j ] = temp;
		};
	}
}
