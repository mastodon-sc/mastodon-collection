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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

/**
 * Test ObjectRefHashMap.
 *
 * @author Jean-Yves Tinevez
 */
public class ObjectRefHashMapTest
{
	protected TestObjectPool pool;
	protected TestObject v0;
	protected TestObject v1;
	protected TestObject v2;
	protected TestObject v3;
	protected TestObject v4;
	protected String k0;
	protected String k1;
	protected String k2;
	protected String k3;
	protected String k4;

	protected ObjectRefHashMap<String, TestObject> map;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );

		v0 = pool.create().init( 0 );
		v1 = pool.create().init( 1 );
		v2 = pool.create().init( 2 );
		v3 = pool.create().init( 3 );
		v4 = pool.create().init( 4 );

		k0 = "Not there";
		k1 = "A";
		k2 = "B";
		k3 = "C";
		k4 = "D";

		// fill map { k1 -> v0, k2 -> v1, k3 -> v2, k4 -> v3 }
		map = new ObjectRefHashMap<>( pool );
		final TestObject ref = map.createValueRef();
		map.put( k1, v0, ref );
		map.put( k2, v1, ref );
		map.put( k3, v2, ref );
		map.put( k4, v3, ref );
		map.releaseValueRef( ref );
	}

	@Test
	public void testClear()
	{
		assertFalse( "Map should not be empty now,", map.isEmpty() );
		map.clear();
		assertTrue( "Map should be empty now,", map.isEmpty() );
	}

	@Test
	public void testContainsKey()
	{
		assertFalse( "Map should not contain key k0.", map.containsKey( k0 ) );
		assertTrue( "Map should contain key k1.", map.containsKey( k1 ) );
	}

	@Test
	public void testContainsValue()
	{
		assertFalse( "Map should not contain value v4.", map.containsValue( v4 ) );
		assertTrue( "Map should contain a value v0.", map.containsValue( v0 ) );
	}

	@Test
	public void testGetObject()
	{
		assertEquals( "Unexpected mapping for k1 (expected k1 -> v0).", v0, map.get( k1 ) );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> v1).", v1, map.get( k2 ) );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> v3).", v3, map.get( k4 ) );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> v2).", v2, map.get( k3 ) );
		assertNull( "There should not be a mapping for key k0.", map.get( k0 ) );
	}

	@Test
	public void testGetObjectL()
	{
		final TestObject ref = map.createValueRef();
		map.get( k1, ref );
		assertEquals( "Unexpected mapping for key k1 (expected k1 -> v0).", v0, ref );
		map.get( k3, ref );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> v2)", v2, ref );
		map.get( k2, ref );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> v1)", v1, ref );
		map.get( k4, ref );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> v3)", v3, ref );

		assertEquals( "Unexpected mapping for k1 (expected k1 -> v0).", v0, map.get( k1, ref ) );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> v2).", v2, map.get( k3, ref ) );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> v1).", v1, map.get( k2, ref ) );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> v3).", v3, map.get( k4, ref ) );
		assertNull( "There should not be a mapping for key k0.", map.get( k0, ref ) );
	}

	@Test
	public void testKeySet()
	{
		final Set< String > keySet = map.keySet();
		final Set< String> set = new HashSet<>();
		set.add( k1 );
		set.add( k2 );
		set.add( k3 );
		set.add( k4 );
		// All but k0

		assertEquals( "Unexpected keys found in the key set.", keySet, set );
		for ( final String key : keySet )
		{
			assertTrue( "Unexpected key found in the key set.", set.remove( key ) );
		}
		assertTrue( "All the expected keys have not been fount in the key set.", set.isEmpty() );
	}

	@Test
	public void testPutKLL()
	{
		final TestObject ref = pool.createRef();

		// Add a new key
		final TestObject put = map.put( k0, v0, ref );
		assertNull( "There should not be any mapping prior to adding this key.", put );
		assertEquals( "Could not find the expected value for the new key.", v0, map.get( k0, ref ) );

		// Replace an existing key
		final TestObject put2 = map.put( k1, v4, ref );
		assertEquals( "Could not retrieve the expected value for the old key.", v0, put2 );
		assertEquals( "Could not find the expected value for the new key.", v4, map.get( k1, ref ) );
	}

	@Test
	public void testPutKL()
	{
		// Add a new key
		final TestObject put = map.put( k0, v0 );
		assertNull( "There should not be any mapping prior to adding this key.", put );
		assertEquals( "Could not find the expected value for the new key.", v0, map.get( k0 ) );

		// Replace an existing key
		final TestObject put2 = map.put( k1, v4 );
		assertEquals( "Could not retrieve the expected value for the old key.", v0, put2 );
		assertEquals( "Could not find the expected value for the new key.", v4, map.get( k1 ) );
	}

	@Test
	public void testPutAll()
	{
		final ObjectRefHashMap< String, TestObject > extraMap = new ObjectRefHashMap<>( pool );
		extraMap.put( k0, v0 );
		// Careful to add 1 mapping not already present in the map.
		extraMap.put( k1, v1 );
		// Change one mapping.

		final int initSize = map.size();
		map.putAll( extraMap );
		assertEquals( "Map after putAll does not have the expected size.", initSize + 1, map.size() );
		assertEquals( "New mapping is not right.", v0, map.get( k0 ) );
		assertEquals( "New mapping is not right.", v1, map.get( k1 ) );
	}

	@Test
	public void testRemoveObjectL()
	{
		final int size = map.size();
		final TestObject ref = pool.createRef();

		// Remove a non existing mapping
		final TestObject remove = map.removeWithRef( k0, ref );
		assertNull( "Removing a non-exiting mapping should return null.", remove );
		assertEquals( "Map size should not have changed.", size, map.size() );

		// Remove an existing mapping
		final TestObject remove2 = map.removeWithRef( k1, ref );
		assertEquals( "Did not retrieve the expected value upong key removal.", v0, remove2 );
		assertEquals( "Map size should have decreased by 1.", size - 1, map.size() );
	}

	@Test
	public void testRemoveObject()
	{
		final int size = map.size();

		// Remove a non existing mapping
		final TestObject remove = map.remove( k0 );
		assertNull( "Removing a non-exiting mapping should return null.", remove );
		assertEquals( "Map size should not have changed.", size, map.size() );

		// Remove an existing mapping
		final TestObject remove2 = map.remove( k1 );
		assertEquals( "Did not retrieve the expected value upong key removal.", v0, remove2 );
		assertEquals( "Map size should have decreased by 1.", size - 1, map.size() );
	}

	@Test
	public void testSize()
	{
		final int initSize = 4;
		assertEquals( "Map does not report the expected size.", initSize, map.size() );
		map.remove( k0 ); // absent
		map.remove( k1 ); // present
		map.remove( k2 ); // present
		assertEquals( "Map does not report the expected size after changes.", initSize - 2, map.size() );
	}

	@Test
	public void testCreateValueRef()
	{
		final TestObject ref = map.createValueRef();
		assertNotNull( "Created reference object is null.", ref );
	}
}
