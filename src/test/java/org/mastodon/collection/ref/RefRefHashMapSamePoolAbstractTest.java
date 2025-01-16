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
package org.mastodon.collection.ref;

import org.junit.Before;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

/**
 * Set up a TestObjectPool with objects k0 .. k4.
 * <p>
 * Create RefRefHashMap { k1 -> k2,  k2 -> k3,  k3 -> k4,  k4 -> k0 }.
 * <p>
 * This leaves objects k0 not occurring as key, and k1 not accurring as value.
 *
 *
 * @author Tobias Pietzsch
 * @author Jean-Yves Tinevez
 */
public abstract class RefRefHashMapSamePoolAbstractTest
{
	protected TestObjectPool pool;

	protected TestObject k0;

	protected TestObject k1;

	protected TestObject k2;

	protected TestObject k3;

	protected TestObject k4;

	protected RefRefHashMap< TestObject, TestObject > map;

	@Before
	public void setUp()
	{
		pool = new TestObjectPool( 10 );

		k0 = pool.create().init( 0 );
		k1 = pool.create().init( 1 );
		k2 = pool.create().init( 2 );
		k3 = pool.create().init( 3 );
		k4 = pool.create().init( 4 );

		map = new RefRefHashMap<>( pool, pool );
		map.put( k1, k2 );
		map.put( k2, k3 );
		map.put( k3, k4 );
		map.put( k4, k0 );
		/*
		 * 4 mappings. k0 not occurring as key, k1 not occurring as value.
		 */
	}
}
