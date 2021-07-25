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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefSet;
import org.mastodon.pool.TestObject;

public class RefRefHashMapSamePoolTest extends RefRefHashMapSamePoolAbstractTest
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
		assertFalse( "Map should not contain k0 as a key.", map.containsKey( k0 ) );
		assertTrue( "Map should contain k1 as a key.", map.containsKey( k1 ) );
	}

	@Test
	public void testContainsValue()
	{
		assertFalse( "Map should not contain k1 as a value.", map.containsValue( k1 ) );
		assertTrue( "Map should contain k0 as a value.", map.containsValue( k0 ) );
	}

	@Test
	public void testGetObject()
	{
		assertEquals( "Unexpected mapping for k1 (expected k1 -> k2).", k2, map.get( k1 ) );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> k4).", k4, map.get( k3 ) );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> k3).", k3, map.get( k2 ) );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> k0).", k0, map.get( k4 ) );
		assertNull( "There should not be a mapping for key k0.", map.get( k0 ) );
	}

	@Test
	public void testGetObjectL()
	{
		final TestObject ref = map.createValueRef();
		map.get( k1, ref );
		assertEquals( "Unexpected mapping for key k1 (expected k1 -> k2).", k2, ref );
		map.get( k3, ref );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> k4)", k4, ref );
		map.get( k2, ref );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> k3)", k3, ref );
		map.get( k4, ref );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> k0)", k0, ref );
		assertNull( "There should not be a mapping for key k0.", map.get( k0, ref ) );

		assertEquals( "Unexpected mapping for k1 (expected k1 -> k2).", k2, map.get( k1 ) );
		assertEquals( "Unexpected mapping for k3 (expected k3 -> k4).", k4, map.get( k3 ) );
		assertEquals( "Unexpected mapping for k2 (expected k2 -> k3).", k3, map.get( k2 ) );
		assertEquals( "Unexpected mapping for k4 (expected k4 -> k0).", k0, map.get( k4 ) );
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
		final TestObject ref = pool.createRef();

		// Add a new key
		final TestObject put = map.put( k0, k0, ref );
		assertNull( "There should not be any mapping prior to adding this key.", put );
		assertEquals( "Could not find the expected value for the new key.", k0, map.get( k0, ref ) );

		// Replace an existing key
		final TestObject put2 = map.put( k1, k4, ref );
		assertEquals( "Could not retrieve the expected value for the old key.", k2, put2 );
		assertEquals( "Could not find the expected value for the new key.", k4, map.get( k1, ref ) );
	}

	@Test
	public void testPutKL()
	{
		// Add a new key
		final TestObject put = map.put( k0, k1 );
		assertNull( "There should not be any mapping prior to adding this key.", put );
		assertEquals( "Could not find the expected value for the new key.", k1, map.get( k0 ) );

		// Replace an existing key
		final TestObject put2 = map.put( k1, k0 );
		assertEquals( "Could not retrieve the expected value for the old key.", k2, put2 );
		assertEquals( "Could not find the expected value for the new key.", k0, map.get( k1 ) );
	}

	@Test
	public void testPutAll()
	{
		final RefRefHashMap< TestObject, TestObject > extraMap = new RefRefHashMap<>( pool, pool );
		extraMap.put( k0, k1 );
		// Careful to add 1 mapping not already present in the map.
		extraMap.put( k1, k0 );
		// Change one mapping.

		final int initSize = map.size();
		map.putAll( extraMap );
		assertEquals( "Map after putAll does not have the expected size.", initSize + 1, map.size() );
		assertEquals( "New mapping is not right.", k1, map.get( k0 ) );
		assertEquals( "New mapping is not right.", k0, map.get( k1 ) );
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
		assertEquals( "Did not retrieve the expected value upong key removal.", k2, remove2 );
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
		assertEquals( "Did not retrieve the expected value upong key removal.", k2, remove2 );
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
