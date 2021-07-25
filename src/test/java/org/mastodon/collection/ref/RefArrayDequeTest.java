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
package org.mastodon.collection.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

public class RefArrayDequeTest
{
	@Test
	public void addFirstTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final TestObject _ref = pool.createRef();
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );

		deque.addFirst( pool.create( _ref ).init( 10 ) );
		deque.addFirst( pool.create( _ref ).init( 11 ) );
		assertEquals( deque.iterator().next().getId(), 11 );
	}

	@Test
	public void addLastTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final TestObject _ref = pool.createRef();
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );

		deque.addLast( pool.create( _ref ).init( 10 ) );
		deque.addLast( pool.create( _ref ).init( 11 ) );
		assertEquals( deque.iterator().next().getId(), 10 );
	}

	@Test
	public void pollFirstTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final TestObject _ref = pool.createRef();
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );

		deque.addFirst( pool.create( _ref ).init( 10 ) );
		deque.addFirst( pool.create( _ref ).init( 11 ) );
		assertEquals( deque.pollFirst( _ref ).getId(), 11 );
	}

	@Test
	public void pollLastTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final TestObject _ref = pool.createRef();
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );

		deque.addLast( pool.create( _ref ).init( 10 ) );
		deque.addLast( pool.create( _ref ).init( 11 ) );
		assertEquals( deque.pollLast( _ref ).getId(), 11 );
	}

	@Test
	public void iteratorTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final TestObject _ref = pool.createRef();
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );

		deque.addLast( pool.create( _ref ).init( 10 ) );
		deque.addLast( pool.create( _ref ).init( 11 ) );
		final Iterator< TestObject > iter = deque.iterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next().getId(), 10 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next().getId(), 11 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void descendingIteratorTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final TestObject _ref = pool.createRef();
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );

		deque.addLast( pool.create( _ref ).init( 10 ) );
		deque.addLast( pool.create( _ref ).init( 11 ) );
		final Iterator< TestObject > iter = deque.descendingIterator();
		assertTrue( iter.hasNext() );
		assertEquals( iter.next().getId(), 11 );
		assertTrue( iter.hasNext() );
		assertEquals( iter.next().getId(), 10 );
		assertFalse( iter.hasNext() );
	}

	@Test
	public void containsTest()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		final RefArrayDeque< TestObject > deque = new RefArrayDeque<>( pool );
		final TestObject o10 = pool.create().init( 10 );
		final TestObject o11 = pool.create().init( 11 );
		final TestObject o29 = pool.create().init( 29 );
		final TestObject o51 = pool.create().init( 51 );
		final TestObject o100 = pool.create().init( 100 );
		deque.addLast( o10 );
		deque.addLast( o11 );
		deque.addLast( o29 );
		deque.addLast( o51 );
		assertTrue( deque.contains( o10 ) );
		assertTrue( deque.contains( o11 ) );
		assertTrue( deque.contains( o29 ) );
		assertTrue( deque.contains( o51 ) );
		assertFalse( deque.contains( o100 ) );
	}

}
