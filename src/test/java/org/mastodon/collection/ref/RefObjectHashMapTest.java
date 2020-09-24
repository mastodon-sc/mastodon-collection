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
package org.mastodon.collection.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

public class RefObjectHashMapTest
{

	private RefObjectHashMap< TestObject, String > map;

	private TestObject A;

	private TestObject B;

	private TestObjectPool pool;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );

		A = pool.createRef();
		A = pool.create( A ).init( 0 );

		B = pool.createRef();
		B = pool.create( B ).init( 0 );

		TestObject C = pool.createRef();
		C = pool.create( C ).init( 0 );

		TestObject D = pool.createRef();
		D = pool.create( D ).init( 0 );

		TestObject E = pool.createRef();
		E = pool.create( E ).init( 0 );

		map = new RefObjectHashMap<>( pool );
		map.put( A, "a" );
		map.put( B, "b" );
		map.put( C, "c" );
		map.put( D, "d" );
		map.put( E, "e" );
	}

	@Test
	public void testClear()
	{
		map.clear();
		assertTrue( "Map should be empty after clear(), is not.", map.isEmpty() );
	}

	@Test
	public void testContainsKey()
	{
		assertTrue( "Map should contain the key 'A', does not.", map.containsKey( A ) );
		assertTrue( "Map should contain the key 'B', does not.", map.containsKey( B ) );
		TestObject ref = pool.createRef();
		ref = pool.create( ref ).init( 10 );
		assertFalse( "Map should not contain the key 'ref', but does.", map.containsKey( ref ) );
	}

	@Test
	public void testContainsValue()
	{
		assertTrue( "Map should contain the value 'a', does not.", map.containsValue( "a" ) );
		assertTrue( "Map should contain the value 'b', does not.", map.containsValue( "b" ) );
		assertFalse( "Map should not contain a random value, but does.", map.containsValue( "yodelodoudi" ) );
	}

	@Test
	public void testGet()
	{
		assertTrue( "Could not retrieve the desired value for key 'A'.", map.get( A ).equals( "a" ) );
		assertTrue( "Could not retrieve the desired value for key 'B'.", map.get( B ).equals( "b" ) );
	}

	@Test
	public void testIsEmpty()
	{
		assertTrue( "A new map should be empty, but is not.", new RefObjectHashMap< TestObject, String >( pool ).isEmpty() );
	}

	@Test
	public void testPut()
	{
		TestObject Z = pool.createRef();
		Z = pool.create( Z ).init( 11 );
		map.put( Z, "z" );
		assertTrue( "Could not retrieve the desired value for key 'Z'.", map.get( Z ).equals( "z" ) );
	}

	@Test
	public void testRemove()
	{
		final String remove = map.remove( A );
		assertTrue( "The value returned by remove() is not what was expected.", remove.equals( "a" ) );
		assertFalse( "The mapping for key 'A' should not be present anymore, but is.", map.containsKey( A ) );
	}

	@Test
	public void testSize()
	{
		assertEquals( "Map does not have the right size.", 5, map.size() );
	}

	@Test
	public void testValues()
	{
		final Collection< String > values = map.values();
		assertEquals( "Value collection does not have the expected size.", 5, values.size() );
		assertTrue( "Value collection does not contain the expected value 'a'.", values.contains( "a" ) );
		assertTrue( "Value collection does not contain the expected value 'b'.", values.contains( "b" ) );
		final boolean remove = values.remove( "a" );
		assertTrue( "Value 'a' should have been sucessfully removed from value collection, but was not.", remove );
		assertFalse( "After removal of 'a' from value collection, map should not contain mapping for 'a', but does.", map.containsValue( "a" ) );
		values.clear();
		assertTrue( "After clearing the value collection, the map should be empty, but was not.", map.isEmpty() );
	}

	@Test
	public void testKeySet()
	{
		final RefSetImp< TestObject > keySet = map.keySet();
		assertEquals( "Key set does not have the expected size.", 5, keySet.size() );
		assertTrue( "Key set does not contain the expected key 'A'.", keySet.contains( A ) );
		assertTrue( "Key set does not contain the expected key 'B'.", keySet.contains( B ) );
		final boolean remove = keySet.remove( A );
		assertTrue( "Key 'A' should have been sucessfully removed from key set, but was not.", remove );
		assertFalse( "After removal of 'A' from key set, map should not contain mapping for 'A', but does.", map.containsKey( A ) );
		keySet.clear();
		assertTrue( "After clearing the key set, the map should be empty, but was not.", map.isEmpty() );
	}
}
