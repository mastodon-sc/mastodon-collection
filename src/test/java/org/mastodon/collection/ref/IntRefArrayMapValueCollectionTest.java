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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

public class IntRefArrayMapValueCollectionTest
{

	private TestObjectPool pool;

	private IntRefArrayMap< TestObject > map;

	private HashMap< Integer, Integer > truthMap;

	private int[] storedIds;

	private Collection< TestObject > valueCollection;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );
		truthMap = new HashMap<>( 10 );
		final TestObject ref = pool.createRef();
		for ( int i = 0; i < 10; i++ )
		{
			final TestObject a = pool.create( ref ).init( i );
			truthMap.put( Integer.valueOf( a.getId() ), Integer.valueOf( a.getInternalPoolIndex() ) );
		}

		map = new IntRefArrayMap<>( pool );
		storedIds = new int[] { 2, 3, 6, 8 };
		for ( final int id : storedIds )
		{
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref );
			map.put( id, ref );
		}
		pool.releaseRef( ref );
		valueCollection = map.valueCollection();
	}

	@Test( expected = UnsupportedOperationException.class )
	public void testAdd()
	{
		final TestObject v = pool.create( pool.createRef() ).init( 100 );
		valueCollection.add( v );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void testAddAll()
	{
		final RefSetImp< TestObject > set = new RefSetImp<>( pool );
		final TestObject ref = pool.createRef();
		for ( int i = 0; i < 5; i++ )
		{
			set.add( pool.create( ref ).init( 100 + i ) );
		}
		pool.releaseRef( ref );
		valueCollection.addAll( set );
	}

	@Test
	public void testClear()
	{
		valueCollection.clear();
		assertTrue( "Map should be empty after valueCollection.clear().", map.isEmpty() );
	}

	@Test
	public void testContains()
	{
		final TestObject ref = pool.createRef();
		for ( final int id : storedIds )
		{
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref );
			assertTrue( "ValueCollection should contain the object " + ref, valueCollection.contains( ref ) );
		}
		for ( final int id : new int[] { 0, 1, 4, 5, 7, 9 } )
		{
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref );
			assertFalse( "ValueCollection should not contain the object " + ref, valueCollection.contains( ref ) );
		}
	}

	@Test
	public void testContainsAll()
	{
		final int[] allIn = storedIds;
		testContainsAllFromKeys( allIn, true );
		final int[] allOut = new int[] { 0, 1, 4, 5, 7, 9 };
		testContainsAllFromKeys( allOut, false );
		final int[] partIn = new int[] { 2, 3, 4, 5, 7, 9 };
		testContainsAllFromKeys( partIn, false );
	}

	private void testContainsAllFromKeys( final int[] keys, final boolean expected )
	{
		final boolean containsAll = valueCollection.containsAll( createListFromKeys( keys ) );
		if ( expected )
		{
			assertTrue( "Tested collection should be contained in the valueCondition.", containsAll );
		}
		else
		{
			assertFalse( "Tested collection should not be contained in the valueCondition.", containsAll );
		}
	}

	@Test
	public void testIsEmpty()
	{
		assertFalse( "ValueCollection should not be empty.", valueCollection.isEmpty() );
		valueCollection.clear();
		assertTrue( "ValueCollection should be empty after clear().", valueCollection.isEmpty() );
		assertTrue( "ValueCollection from new map should be empty.", new IntRefArrayMap<>( pool ).valueCollection().isEmpty() );
	}

	@Test
	public void testIterator()
	{
		// Test iterate in the right order.
		final Iterator< TestObject > it = valueCollection.iterator();
		final TestObject ref = pool.createRef();
		int index = 0;
		while ( it.hasNext() )
		{
			final TestObject actual = it.next();

			final int key = storedIds[ index++ ];
			final int poolIndex = truthMap.get( key );
			pool.getObject( poolIndex, ref );

			assertEquals( "Iterator returns unexpected value.", ref, actual );
		}

		// Test iterator removal.
		// Remove the 6.
		final int size = map.size();
		final Iterator< TestObject > it2 = valueCollection.iterator();
		it2.next(); // 2
		it2.next(); // 3
		final TestObject val = it2.next(); // 6
		it2.remove();
		assertEquals( "Map does not have the expected size after removal by keyset iterator.", size - 1, map.size() );
		assertFalse( "Map should not contain a mapping for key " + val + " after removal by keyset iterator.", map.containsValue( val ) );

		// Remove all.
		final Iterator< TestObject > it3 = valueCollection.iterator();
		while ( it3.hasNext() )
		{
			it3.next();
			it3.remove();
		}
		assertTrue( "Map should be empty after removing all content with keyset iterator.", map.isEmpty() );
	}

	@Test
	public void testRemoveAll()
	{
		final int[] allOut = new int[] { 0, 1, 4, 5, 7, 9 };
		boolean changed = removeAllFromKeys( allOut );
		assertFalse( "Removing non-present values should not have changed the valueCollection.", changed );

		final int[] partIn = new int[] { 2, 3, 4, 5, 7, 9 };
		changed = removeAllFromKeys( partIn );
		assertTrue( "Removing some present values should have changed the valueCollection.", changed );
		assertEquals( "Map does not have the expected size after valueCollection.removeAll.", 2, map.size() );

		final int[] allIn = storedIds;
		changed = removeAllFromKeys( allIn );
		assertTrue( "Removing all present values should have changed the valueCollection.", changed );
		assertTrue( "Map should be empty after removing all values.", map.isEmpty() );
	}

	private boolean removeAllFromKeys( final int[] keys )
	{
		return valueCollection.removeAll( createListFromKeys( keys ) );
	}

	@Test
	public void testRetainAll()
	{
		final int[] allIn = storedIds;
		boolean changed = retainAllFromKeys( allIn );
		assertFalse( "Retaining all present values should have not changed the valueCollection.", changed );

		final int[] partIn = new int[] { 2, 3, 4, 5, 7, 9 };
		changed = retainAllFromKeys( partIn );
		assertTrue( "Retaining only some present values should have changed the valueCollection.", changed );
		assertEquals( "Map does not have the expected size after valueCollection.retainAll.", 2, map.size() );

		final int[] allOut = new int[] { 0, 1, 4, 5, 7, 9 };
		changed = retainAllFromKeys( allOut );
		assertTrue( "Retaining only non-present values should have changed the valueCollection.", changed );
		assertTrue( "The map should be empty after retraining none of its values.", map.isEmpty() );
	}

	private boolean retainAllFromKeys( final int[] keys )
	{
		return valueCollection.retainAll( createListFromKeys( keys ) );
	}

	@Test
	public void testSize()
	{
		assertEquals( "ValueCollection and the map do not have the same size.", map.size(), valueCollection.size() );
		final TestObject ref = pool.createRef();
		for ( int i = 0; i < 5; i++ )
		{
			map.put( 100 + i, ref );
		}
		assertEquals( "ValueCollection and the map do not have the same size.", map.size(), valueCollection.size() );
		map.clear();
		assertEquals( "ValueCollection and the map do not have the same size.", map.size(), valueCollection.size() );
	}

	@Test
	public void testToArray()
	{
		final Object[] array = valueCollection.toArray();
		assertEquals( "Created array does not have the expected length.", valueCollection.size(), array.length );
		int index = 0;
		final RefArrayList< TestObject > set = createListFromKeys( storedIds );
		for ( final TestObject expected : set )
		{
			assertEquals( "Unexpected object in the array returned by toArray().", expected, array[ index++ ] );
		}
	}

	@Test
	public void testToArrayArray()
	{
		final TestObject[] array = valueCollection.toArray( new TestObject[ 100 ] );
		int index = 0;
		final RefArrayList< TestObject > set = createListFromKeys( storedIds );
		for ( final TestObject expected : set )
		{
			assertEquals( "Unexpected object in the array returned by toArray(T[]).", expected, array[ index++ ] );
		}
		for ( int j = index; j < array.length; j++ )
		{
			assertNull( "Remaining array slots should be null.", array[ j ] );
		}
	}

	private RefArrayList< TestObject > createListFromKeys( final int[] keys )
	{
		final RefArrayList< TestObject > set = new RefArrayList<>( pool );
		final TestObject ref = pool.createRef();
		for ( final int key : keys )
		{
			final int poolIndex = truthMap.get( key );
			pool.getObject( poolIndex, ref );
			set.add( ref );
		}
		return set;
	}
}
