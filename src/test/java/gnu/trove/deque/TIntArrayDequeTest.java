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
package gnu.trove.deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gnu.trove.iterator.TIntIterator;

public class TIntArrayDequeTest
{
	@Test
	public void addFirstTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addFirst( 10 );
		deque.addFirst( 11 );
		assertEquals( deque.iterator().next(), 11 );
	}

	@Test
	public void addLastTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 10 );
		deque.addLast( 11 );
		assertEquals( deque.iterator().next(), 10 );
	}

	@Test
	public void pollFirstTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addFirst( 10 );
		deque.addFirst( 11 );
		assertEquals( deque.pollFirst(), 11 );
	}

	@Test
	public void pollLastTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 10 );
		deque.addLast( 11 );
		assertEquals( deque.pollLast(), 11 );
	}

	@Test
	public void iteratorTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 10 );
		deque.addLast( 11 );
		final TIntIterator iter = deque.iterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 10 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 11 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void dscendingIteratorTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 10 );
		deque.addLast( 11 );
		final TIntIterator iter = deque.descendingIterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 11 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 10 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void containsTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 10 );
		deque.addLast( 11 );
		deque.addLast( 29 );
		deque.addLast( 51 );
		assertTrue( deque.contains( 10 ) );
		assertTrue( deque.contains( 11 ) );
		assertTrue( deque.contains( 29 ) );
		assertTrue( deque.contains( 51 ) );
		assertFalse( deque.contains( 100 ) );
	}

	@Test
	public void removeTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 1 );
		deque.addLast( 2 );
		deque.addLast( 3 );
		deque.addLast( 2 );
		deque.addLast( 4 );
		deque.remove( 2 );
		final TIntIterator iter = deque.iterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 1 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 3 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 2 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 4 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void remove2Test()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addFirst( 4 );
		deque.addFirst( 2 );
		deque.addFirst( 3 );
		deque.addFirst( 2 );
		deque.addFirst( 1 );
		deque.remove( 2 );
		final TIntIterator iter = deque.iterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 1 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 3 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 2 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 4 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void removeLastOccurrenceTest()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addLast( 1 );
		deque.addLast( 2 );
		deque.addLast( 3 );
		deque.addLast( 2 );
		deque.addLast( 4 );
		deque.removeLastOccurrence( 2 );
		final TIntIterator iter = deque.iterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 1 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 2 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 3 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 4 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void removeLastOccurrence2Test()
	{
		final TIntArrayDeque deque = new TIntArrayDeque( 10, -1 );
		deque.addFirst( 4 );
		deque.addFirst( 2 );
		deque.addFirst( 3 );
		deque.addFirst( 2 );
		deque.addFirst( 1 );
		deque.removeLastOccurrence( 2 );
		final TIntIterator iter = deque.iterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 1 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 2 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 3 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next(), 4 );
		assertFalse( iter.hasNext() );
	}
}
