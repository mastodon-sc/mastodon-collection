/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
import org.mastodon.pool.OtherTestObject;
import org.mastodon.pool.OtherTestObjectPool;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

/**
 * Set up a TestObjectPool with objects k0 .. k4.
 * <p>
 * Set up a OtherTestObjectPool with objects v0 .. v4.
 * <p>
 * Create RefRefHashMap { k1 -> v0,  k2 -> v1,  k3 -> v2,  k4 -> v3 }.
 * <p>
 * This leaves objects k0 and v4 which are not present in the map.
 *
 *
 * @author Tobias Pietzsch
 * @author Jean-Yves Tinevez
 */
public class RefRefHashMapAbstractTest
{
	protected TestObjectPool pool;

	protected TestObject k0;

	protected TestObject k1;

	protected TestObject k2;

	protected TestObject k3;

	protected TestObject k4;

	protected OtherTestObjectPool otherPool;

	protected OtherTestObject v0;

	protected OtherTestObject v1;

	protected OtherTestObject v2;

	protected OtherTestObject v3;

	protected OtherTestObject v4;

	protected RefRefHashMap< TestObject, OtherTestObject > map;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );
		otherPool = new OtherTestObjectPool( 10 );

		k0 = pool.create().init( 0 );
		k1 = pool.create().init( 1 );
		k2 = pool.create().init( 2 );
		k3 = pool.create().init( 3 );
		k4 = pool.create().init( 4 );

		v0 = otherPool.create().init( 100 );
		v1 = otherPool.create().init( 101 );
		v2 = otherPool.create().init( 102 );
		v3 = otherPool.create().init( 103 );
		v4 = otherPool.create().init( 104 );

		// fill map { k1 -> v0,  k2 -> v1,  k3 -> v2,  k4 -> v3 }
		map = new RefRefHashMap<>( pool, otherPool );
		map.put( k1, v0 );
		map.put( k2, v1 );
		map.put( k3, v2 );
		map.put( k4, v3 );
	}
}
