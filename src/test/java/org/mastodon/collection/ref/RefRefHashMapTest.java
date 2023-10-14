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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefSet;
import org.mastodon.pool.OtherTestObject;
import org.mastodon.pool.TestObject;

/**
 * Test map from vertices to edges, both belong to the same graph.
 *
 * @author Jean-Yves Tinevez - 2015
 */
public class RefRefHashMapTest extends RefRefHashMapAbstractTest
{
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
		final OtherTestObject ref = map.createValueRef();
		map.get( k1, ref );
		assertEquals( "Unexpected mapping for key k1 (expected k1 -> v0).", v0, ref );
		map.get( k3, ref );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> v2)", v2, ref );
		map.get( k2, ref );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> v1)", v1, ref );
		map.get( k4, ref );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> v3)", v3, ref );
		assertNull( "There should not be a mapping for key k0.", map.get( k0, ref ) );

		assertEquals( "Unexpected mapping for k1 (expected k1 -> v0).", v0, map.get( k1 ) );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> v2).", v2, map.get( k3 ) );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> v1).", v1, map.get( k2 ) );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> v3).", v3, map.get( k4 ) );
	}

	@Test
	public void testKeySet()
	{
		final Set< TestObject > keySet = map.keySet();
		assertTrue( "Set returned should be a " + RefSetImp.class.getSimpleName(), keySet instanceof RefSetImp );
		final RefSet< TestObject > set = RefCollections.createRefSet( pool.asRefCollection() );
		set.add( k1 );
		set.add( k2 );
		set.add( k3 );
		set.add( k4 );
		// All but k0

		for ( final TestObject key : keySet )
		{
			assertTrue( "Unexpected key found in the key set.", set.remove( key ) );
		}
		assertTrue( "All the expected keys have not been fount in the key set.", set.isEmpty() );
	}

	@Test
	public void testPutKLL()
	{
		final OtherTestObject ref = otherPool.createRef();

		// Add a new key
		final OtherTestObject put = map.put( k0, v0, ref );
		assertNull( "There should not be any mapping prior to adding this key.", put );
		assertEquals( "Could not find the expected value for the new key.", v0, map.get( k0, ref ) );

		// Replace an existing key
		final OtherTestObject put2 = map.put( k1, v4, ref );
		assertEquals( "Could not retrieve the expected value for the old key.", v0, put2 );
		assertEquals( "Could not find the expected value for the new key.", v4, map.get( k1, ref ) );
	}

	@Test
	public void testPutKL()
	{
		// Add a new key
		final OtherTestObject put = map.put( k0, v0 );
		assertNull( "There should not be any mapping prior to adding this key.", put );
		assertEquals( "Could not find the expected value for the new key.", v0, map.get( k0 ) );

		// Replace an existing key
		final OtherTestObject put2 = map.put( k1, v4 );
		assertEquals( "Could not retrieve the expected value for the old key.", v0, put2 );
		assertEquals( "Could not find the expected value for the new key.", v4, map.get( k1 ) );
	}

	@Test
	public void testPutAll()
	{
		final RefRefHashMap< TestObject, OtherTestObject > extraMap = new RefRefHashMap<>( pool, otherPool );
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
		final OtherTestObject ref = otherPool.createRef();

		// Remove a non existing mapping
		final OtherTestObject remove = map.removeWithRef( k0, ref );
		assertNull( "Removing a non-exiting mapping should return null.", remove );
		assertEquals( "Map size should not have changed.", size, map.size() );

		// Remove an existing mapping
		final OtherTestObject remove2 = map.removeWithRef( k1, ref );
		assertEquals( "Did not retrieve the expected value upong key removal.", v0, remove2 );
		assertEquals( "Map size should have decreased by 1.", size - 1, map.size() );
	}

	@Test
	public void testRemoveObject()
	{
		final int size = map.size();

		// Remove a non existing mapping
		final OtherTestObject remove = map.remove( k0 );
		assertNull( "Removing a non-exiting mapping should return null.", remove );
		assertEquals( "Map size should not have changed.", size, map.size() );

		// Remove an existing mapping
		final OtherTestObject remove2 = map.remove( k1 );
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
		final OtherTestObject ref = map.createValueRef();
		assertNotNull( "Created reference object is null.", ref );
	}

	@Test
	public void testForEach()
	{
		Map<Integer, Integer> result = new HashMap<>();
		// Use forEach to copy content from RefRefHashMap to java collections HashMap.
		map.forEach( (key, value) -> result.put( key.getId(), value.getId() ) );
		// Make sure the content is the same.
		assertEquals( 4, result.size() );
		assertEquals( v0.getId(), (int) result.get( k1.getId() ) );
		assertEquals( v1.getId(), (int) result.get( k2.getId() ) );
		assertEquals( v2.getId(), (int) result.get( k3.getId() ) );
		assertEquals( v3.getId(), (int) result.get( k4.getId() ) );
	}
}
