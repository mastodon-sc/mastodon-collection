/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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
package gnu.trove.iterator;

import org.junit.Assert;
import org.junit.Test;

import gnu.trove.iterator.TIntAlternatingIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt;
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class TIntAlternatingIteratorTest
{

	@Test
	public void test1()
	{
		compare(
				new int[] { 0, 2, 4, 6, 8, 10 },
				                 3,
				new int[] {       4,
				               2,    6,
				            0,          8, 10 } );
	}

	@Test
	public void test2()
	{
		compare(
				new int[] { 0, 2, 4, 6, 8, 10 },
				                  4,
				new int[] {       4,
				               2,    6,
				            0,          8, 10 } );
	}

	@Test
	public void test3()
	{
		compare(
				new int[] { 0, 2, 4, 6, 8, 10 },
				                        8,
				new int[] {             8,
				                     6,    10,
				                  4,
				               2,
				            0 } );
	}

	@Test
	public void test4()
	{
		compare(
				new int[] { 0, 2, 4, 6, 8, 10 },
				            0,
				new int[] { 0, 2, 4, 6, 8, 10 } );
	}

	@Test
	public void test5()
	{
		compare(
				new int[] { 0, 2, 4, 6, 8, 10 },
				                           10,
				new int[] {                10,
				                        8,
				                     6,
				                  4,
				               2,
				            0 } );
	}

	void compare( final int[] list, final int startValue, final int[] expectedIterationOrder )
	{
		compare( new TIntArrayList( list ), startValue, new TIntArrayList( expectedIterationOrder ) );
	}

	void compare( final TIntList list, final int startValue, final TIntList expectedIterationOrder )
	{
		final TIntList actual = new TIntArrayList();
		final TIntAlternatingIterator it = new TIntAlternatingIterator( list, startValue );
		while ( it.hasNext() )
			actual.add( it.next() );
		Assert.assertArrayEquals( expectedIterationOrder.toArray(), actual.toArray() );
	}

	public static void main( final String[] args )
	{
		final TIntArrayList list = new TIntArrayList( 11 );
		for ( int i = 0; i < 11; i+=2 )
		{
			list.add( i );
		}

		System.out.println( "From 3:" );
		TIntAlternatingIterator it = new TIntAlternatingIterator( list, 3 );
		while ( it.hasNext() )
			System.out.println( "  " + it.next() );

		System.out.println( "From 4:" );
		it = new TIntAlternatingIterator( list, 4 );
		while ( it.hasNext() )
			System.out.println( "  " + it.next() );

		System.out.println( "From 8:" );
		it = new TIntAlternatingIterator( list, 8 );
		while ( it.hasNext() )
			System.out.println( "  " + it.next() );

		System.out.println( "From 0:" );
		it = new TIntAlternatingIterator( list, 0 );
		while ( it.hasNext() )
			System.out.println( "  " + it.next() );

		System.out.println( "From 10:" );
		it = new TIntAlternatingIterator( list, 10 );
		while ( it.hasNext() )
			System.out.println( "  " + it.next() );

		System.out.println( "From 5:" );
		it = new TIntAlternatingIterator( list, 5 );
		while ( it.hasNext() )
			System.out.println( "  " + it.next() );

	}

}
