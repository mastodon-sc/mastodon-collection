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
package org.mastodon.collection.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

public class RefLinkedQueueTest
{
	private RefLinkedQueue< TestObject > queue;

	private ArrayList< TestObject > objects;

	@Before
	public void noSetup()
	{
		final TestObjectPool pool = new TestObjectPool( 10 );
		queue = new RefLinkedQueue< >( pool );
		final TestObject A = pool.create().init( 1 );
		final TestObject B = pool.create().init( 2 );
		final TestObject C = pool.create().init( 3 );
		final TestObject E = pool.create().init( 4 );
		final TestObject D = pool.create().init( 5 );
		final TestObject F = pool.create().init( 6 );
		final TestObject G = pool.create().init( 7 );
		objects = new ArrayList< >( 7 );
		objects.add( A );
		objects.add( B );
		objects.add( C );
		objects.add( D );
		objects.add( E );
		objects.add( F );
		objects.add( G );
	}

	@After
	public void noTearDown()
	{
		queue = null;
	}

	@Test
	public void offerTest()
	{
		for ( final TestObject o : objects )
		{
			queue.offer( o );
		}
		final TestObject ref = queue.createRef();
		assertEquals( queue.peek( ref ), objects.get( 0 ) );
	}

	@Test
	public void pollTest()
	{
		for ( final TestObject o : objects )
		{
			queue.offer( o );
		}
		final TestObject ref = queue.createRef();
		for ( final TestObject o : objects )
		{
			assertEquals( o, queue.poll( ref ) );
		}
		assertTrue( queue.isEmpty() );
	}

	@Test
	public void peekTest()
	{
		final TestObject ref = queue.createRef();
		assertNull( queue.peek( ref ) );
	}

	@Test
	public void emptyTest()
	{
		assertTrue( queue.isEmpty() );
	}
}
