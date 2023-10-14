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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;

public class IntRefHashMapTest
{

	private TestObjectPool pool;

	private IntRefHashMap< TestObject > map;

	private HashMap< Integer, Integer > truthMap;

	private int[] storedIds;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );
		truthMap = new HashMap<>( 10 );
		final TestObject ref = pool.createRef();
		for ( int i = 0; i < 10; i++ )
		{
			final int id = 20 + i;
			final TestObject a = pool.create( ref ).init( id );
			truthMap.put( Integer.valueOf( a.getId() ), Integer.valueOf( a.getInternalPoolIndex() ) );
		}

		map = new IntRefHashMap<>( pool, -1 );
		storedIds = new int[] { 22, 23, 26, 28 };
		for ( final int id : storedIds )
		{
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref );
			map.put( id, ref );
		}
		pool.releaseRef( ref );
	}

	@Test
	public void testCreateRef()
	{
		map.createRef();
	}

	@Test
	public void testClear()
	{
		map.clear();
		assertTrue( "Map should be empty after clear().", map.isEmpty() );
		assertEquals( "Map should be of 0-size after clear().", 0, map.size() );
		final TestObject ref = map.createRef();
		for ( int i = 0; i < 10; i++ )
		{
			final TestObject vertex = map.get( i, ref );
			assertNull( "There should not be a mapping for key " + i + " after clear().", vertex );
		}
		map.releaseRef( ref );
	}

	@Test
	public void testGetInt()
	{
		final TestObject ref = map.createRef();
		for ( final int id : storedIds )
		{

			final TestObject vactual = map.get( id );
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref );
			assertEquals( "Unexpected mapping for key " + id, ref, vactual );
		}
		map.releaseRef( ref );
	}

	@Test
	public void testGetIntV()
	{
		final TestObject ref1 = map.createRef();
		final TestObject ref2 = map.createRef();
		for ( final int id : storedIds )
		{

			final TestObject vactual = map.get( id, ref1 );
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref2 );
			assertEquals( "Unexpected mapping for key " + id, ref2, vactual );
		}
		map.releaseRef( ref1 );
		map.releaseRef( ref2 );
	}

	@Test
	public void testIsEmpty()
	{
		assertTrue( "Full map should not be empty.", !map.isEmpty() );
		for ( final int id : storedIds )
		{
			map.remove( id );
		}
		assertTrue( "Emptied map should be empty.", map.isEmpty() );
		assertTrue( "New map should be empty.", new IntRefArrayMap<>( pool ).isEmpty() );
	}

	@Test
	public void testPutIntV()
	{
		final int key = 25;
		assertTrue( "Map should not yet contain a mapping for key " + key, !map.containsKey( key ) );

		final Integer poolIndex = truthMap.get( key );
		final TestObject ref = map.createRef();
		pool.getObject( poolIndex, ref );
		final TestObject put = map.put( key, ref );
		map.releaseRef( ref );
		assertNull( "There should not be a previous mapping for key " + key, put );
		assertTrue( "Map should now contain a mapping for key " + key, map.containsKey( key ) );
	}

	@Test
	public void testPutIntVV()
	{
		final int key = 25;
		assertTrue( "Map should not yet contain a mapping for key " + key, !map.containsKey( key ) );

		final Integer poolIndex = truthMap.get( key );
		final TestObject ref1 = map.createRef();
		final TestObject ref2 = map.createRef();
		pool.getObject( poolIndex, ref1 );
		final TestObject put = map.put( key, ref1, ref2 );
		map.releaseRef( ref1 );
		map.releaseRef( ref2 );
		assertNull( "There should not be a previous mapping for key " + key, put );
		assertTrue( "Map should now contain a mapping for key " + key, map.containsKey( key ) );
	}

	@Test
	public void testRemoveInt()
	{
		final int key = 26;
		final int size = map.size();
		assertTrue( "Map should contain a mapping for key " + key, map.containsKey( key ) );
		final TestObject removed = map.remove( key );
		assertNotNull( "Object removed by existing mapping should not be null.", removed );
		assertTrue( "Map should not contain a mapping for removed key " + key, !map.containsKey( key ) );
		assertEquals( "Map size should have shrunk by 1 after removal.", size - 1, map.size() );

		final TestObject removed2 = map.remove( key );
		assertNull( "Object removed by non-existing mapping should be null.", removed2 );
		assertEquals( "Map size should not have shrunk by 1 after removal of non-existing mapping.", size - 1, map.size() );
	}

	@Test
	public void testRemoveIntV()
	{
		final int key = 26;
		final int size = map.size();
		assertTrue( "Map should contain a mapping for key " + key, map.containsKey( key ) );
		final TestObject ref = map.createRef();
		final TestObject removed = map.remove( key, ref );
		assertNotNull( "Object removed by existing mapping should not be null.", removed );
		assertTrue( "Map should not contain a mapping for removed key " + key, !map.containsKey( key ) );
		assertEquals( "Map size should have shrunk by 1 after removal.", size - 1, map.size() );

		final TestObject removed2 = map.remove( key, ref );
		assertNull( "Object removed by non-existing mapping should be null.", removed2 );
		assertEquals( "Map size should not have shrunk by 1 after removal of non-existing mapping.", size - 1, map.size() );
		map.releaseRef( ref );
	}

	@Test
	public void testSize()
	{
		assertEquals( "Unexpected map size.", storedIds.length, map.size() );
		final int[] toAdd = new int[] { 24, 25 };
		final TestObject ref1 = map.createRef();
		final TestObject ref2 = map.createRef();
		for ( final int add : toAdd )
		{
			final int poolIndex = truthMap.get( add );
			pool.getObject( poolIndex, ref1 );
			map.put( add, ref1, ref2 );
		}
		map.releaseRef( ref1 );
		map.releaseRef( ref2 );
		assertEquals( "Unexpected map size after addition.", storedIds.length + toAdd.length, map.size() );
		assertEquals( "Unexpected new map size.", 0, new IntRefArrayMap<>( pool ).size() );
	}

	@Test
	public void testGetNoEntryKey()
	{
		final int noEntryKey = map.getNoEntryKey();
		assertTrue( "The no entry key should be negative.", noEntryKey < 0 );
	}

	@Test
	public void testContainsKey()
	{
		for ( final int key : storedIds )
		{
			assertTrue( "The map should contain a mapping for key " + key, map.containsKey( key ) );
		}
		for ( final Integer key : truthMap.keySet() )
		{
			if ( Arrays.binarySearch( storedIds, key ) < 0 )
			{
				assertFalse( "The map should not contain a mapping for key " + key, map.containsKey( key ) );
			}

		}
	}

	@Test
	public void testContainsValue()
	{
		final TestObject ref = pool.createRef();
		for ( final int id : storedIds )
		{
			final Integer poolIndex = truthMap.get( id );
			pool.getObject( poolIndex, ref );
			assertTrue( "Map should contain the value " + ref, map.containsValue( ref ) );
		}
		Arrays.sort( storedIds );
		for ( final Integer id : truthMap.keySet() )
		{
			if ( Arrays.binarySearch( storedIds, id ) < 0 )
			{
				final Integer poolIndex = truthMap.get( id );
				pool.getObject( poolIndex, ref );
				assertFalse( "Map should not contain the value " + ref, map.containsValue( ref ) );
			}
		}
		pool.releaseRef( ref );
	}

	@Test
	public void testPutIfAbsent()
	{
		final int index = 100;
		final TestObject ref1 = pool.createRef();
		final TestObject ref2 = pool.createRef();
		final TestObject vertex = pool.create( ref1 ).init( index );
		final TestObject absent = map.putIfAbsent( index, vertex );
		assertNull( "There was not a mapping for index " + index + " before; returned object should be null.", absent );
		assertEquals( "Unexpected mapping for new key " + index, vertex, map.get( index, ref2 ) );

		final int existingMapping = storedIds[ 0 ];
		final TestObject absent2 = map.putIfAbsent( existingMapping, vertex );
		assertNotNull( "There was a mapping for index " + existingMapping + " before; returned object should not be null.", absent2 );

		final Integer poolIndex = truthMap.get( existingMapping );
		pool.getObject( poolIndex, ref1 );
		assertEquals( "Returned object by putIfAbsent is unexpected.", ref1, absent2 );

		pool.releaseRef( ref1 );
		pool.releaseRef( ref2 );
	}

	@Test
	public void testPutIfAbsentIntVV()
	{
		final int index = 100;
		final TestObject ref1 = pool.createRef();
		final TestObject ref2 = pool.createRef();
		final TestObject vertex = pool.create( ref1 ).init( index );
		final TestObject absent = map.putIfAbsent( index, vertex, ref2 );
		assertNull( "There was not a mapping for index " + index + " before; returned object should be null.", absent );
		assertEquals( "Unexpected mapping for new key " + index, vertex, map.get( index, ref2 ) );

		final int existingMapping = storedIds[ 0 ];
		final TestObject ref3 = pool.createRef();
		final TestObject absent2 = map.putIfAbsent( existingMapping, vertex, ref3 );
		assertNotNull( "There was a mapping for index " + existingMapping + " before; returned object should not be null.", absent2 );

		final Integer poolIndex = truthMap.get( existingMapping );
		pool.getObject( poolIndex, ref1 );
		assertEquals( "Returned object by putIfAbsent is unexpected.", ref1, absent2 );

		pool.releaseRef( ref1 );
		pool.releaseRef( ref2 );
		pool.releaseRef( ref3 );
	}

	@Test
	public void testPutAllMapOfQextendsIntegerQextendsV()
	{
		final Map< Integer, TestObject > m = new HashMap<>();
		final int[] newIds = new int[] { 101, 102 };
		for ( final int id : newIds )
		{
			m.put( id, pool.create( pool.createRef() ).init( id ) );
		}

		final int size = map.size();
		map.putAll( m );
		assertEquals( "Map does not have the expected size after putAll.", size + m.size(), map.size() );
		for ( final int key : m.keySet() )
		{
			final TestObject v = m.get( key );
			assertTrue( "Map should now contain a mapping for key " + key, map.containsKey( key ) );
			assertTrue( "Map should now contain a mapping for value " + v, map.containsValue( v ) );
			assertEquals( "New mapping is different than in the source map.", m.get( key ), v );
		}
	}

	@Test
	public void testPutAllTIntObjectMapOfQextendsV()
	{
		final IntRefHashMap< TestObject > m = new IntRefHashMap<>( pool, -1 );
		final int[] newIds = new int[] { 101, 102 };
		final TestObject ref = pool.createRef();
		for ( final int id : newIds )
		{
			m.put( id, pool.create( ref ).init( id ) );
		}

		final int size = map.size();
		map.putAll( m );
		assertEquals( "Map does not have the expected size after putAll.", size + m.size(), map.size() );
		for ( final int key : m.keys() )
		{
			final TestObject v = m.get( key );
			assertTrue( "Map should now contain a mapping for key " + key, map.containsKey( key ) );
			assertTrue( "Map should now contain a mapping for value " + v, map.containsValue( v ) );
			assertEquals( "New mapping is different than in the source map.", m.get( key ), v );
		}

		pool.releaseRef( ref );
	}

	@Test
	public void testKeys()
	{
		final int[] keys = map.keys();
		assertEquals( "Key array does not have the expected length.", map.size(), keys.length );
		// We know they are in the right order.
		Arrays.sort( storedIds );
		for ( int i = 0; i < keys.length; i++ )
		{
			final int s = Arrays.binarySearch( storedIds, keys[ i ] );
			assertTrue( "Unexpected key returned by keys(): " + keys[ i ], s >= 0 );
		}
	}

	@Test
	public void testKeysIntArray()
	{
		final int[] arr = new int[ 100 ];
		final int[] keys = map.keys( arr );
		assertEquals( "Returned array and passed array are not the same instance.", arr, keys );
		// They should since arr is larger than the map size.

		// We know they are in the right order.
		Arrays.sort( storedIds );
		for ( int i = 0; i < storedIds.length; i++ )
		{
			final int s = Arrays.binarySearch( storedIds, keys[ i ] );
			assertTrue( "Unexpected key returned by keys(): " + keys[ i ], s >= 0 );
		}
		// No contract on remaining elements, so no test.
		//		for ( int i = storedIds.length; i < keys.length; i++ )
		//		{
		//			assertEquals( "Unexpected key returned by keys().", map.getNoEntryKey(), keys[ i ] );
		//		}
	}

	@Test
	public void testValues()
	{
		final Object[] values = map.values();
		assertEquals( "values() array is not of the expected length.", map.size(), values.length );
		for ( final Object obj : values )
		{
			assertTrue( "Object returned by values() is not of the expected class.", obj instanceof TestObject );
			assertTrue( "Object returned by values() should be in the map.", map.containsValue( obj ) );
		}
	}

	@Test
	public void testValuesVArray()
	{
		final TestObject[] arr = new TestObject[ 100 ];
		final TestObject[] values = map.values( arr );
		assertEquals( "Returned array and passed array are not the same instance.", arr.hashCode(), values.hashCode() );
		for ( int i = 0; i < map.size(); i++ )
		{
			final TestObject v = values[ i ];
			assertTrue( "Object returned by values() should be in the map.", map.containsValue( v ) );
		}
		for ( int i = map.size(); i < values.length; i++ )
		{
			assertNull( "Remaining elements should be null.", values[ i ] );
		}
	}

	@Test
	public void testIterator()
	{
		// Test iterate in the right order.
		final TIntObjectIterator< TestObject > it = map.iterator();
		final TestObject ref = pool.createRef();
		Arrays.sort( storedIds );
		while ( it.hasNext() )
		{
			it.advance();
			final int k = it.key();
			final int i = Arrays.binarySearch( storedIds, k );
			assertTrue( "Iterator returns unexpected key: " + k, i >= 0 );

			final int key = storedIds[ i ];
			final int poolIndex = truthMap.get( key );
			pool.getObject( poolIndex, ref );

			assertEquals( "Iterator returns unexpected value.", ref, it.value() );
		}

		// Test iterator removal.
		// Remove the 3rd whatsoever value.
		final int size = map.size();
		final TIntObjectIterator< TestObject > it2 = map.iterator();
		it2.advance();
		it2.advance();
		it2.advance();
		final TestObject val = it2.value();
		it2.remove();
		assertEquals( "Map does not have the expected size after removal by keyset iterator.", size - 1, map.size() );
		assertFalse( "Map should not contain a mapping for key " + val + " after removal by keyset iterator.", map.containsValue( val ) );

		// Remove all.
		final TIntObjectIterator< TestObject > it3 = map.iterator();
		while ( it3.hasNext() )
		{
			it3.advance();
			it3.remove();
		}
		assertTrue( "Map should be empty after removing all content with keyset iterator.", map.isEmpty() );
	}

	@Test
	public void testForEachKey()
	{
		final AtomicInteger ai = new AtomicInteger( 0 );
		final TIntProcedure proc = new TIntProcedure()
		{
			@Override
			public boolean execute( final int value )
			{
				ai.incrementAndGet();
				assertTrue( "Iterated key is not contained in the map.", map.containsKey( value ) );
				return true;
			}
		};
		final boolean ok = map.forEachKey( proc );
		assertTrue( "ForEach procedure should have terminated ok.", ok );
		assertEquals( "All the values have not been iterated through.", map.size(), ai.get() );
	}

	@Test
	public void testForEachValue()
	{
		final AtomicInteger ai = new AtomicInteger( 0 );
		final TObjectProcedure< TestObject > proc = new TObjectProcedure< TestObject >()
		{
			@Override
			public boolean execute( final TestObject value )
			{
				ai.incrementAndGet();
				assertTrue( "Iterated value is not contained in the map.", map.containsValue( value ) );
				return true;
			}
		};
		final boolean ok = map.forEachValue( proc );
		assertTrue( "ForEach procedure should have terminated ok.", ok );
		assertEquals( "All the values have not been iterated through.", map.size(), ai.get() );
	}

	@Test
	public void testForEachEntry()
	{
		final AtomicInteger ai = new AtomicInteger( 0 );
		final TIntObjectProcedure< TestObject > proc = new TIntObjectProcedure< TestObject >()
		{
			@Override
			public boolean execute( final int key, final TestObject value )
			{
				ai.incrementAndGet();
				assertTrue( "Iterated key is not contained in the map.", map.containsKey( key ) );
				assertTrue( "Iterated value is not contained in the map.", map.containsValue( value ) );
				return true;
			}
		};
		final boolean ok = map.forEachEntry( proc );
		assertTrue( "ForEach procedure should have terminated ok.", ok );
		assertEquals( "All the values have not been iterated through.", map.size(), ai.get() );
	}

	@Test
	public void testTransformValues()
	{
		final TestObject ref = pool.createRef();
		final TestObject vertex = pool.create( ref ).init( 100 );
		final TObjectFunction< TestObject, TestObject > function = new TObjectFunction< TestObject, TestObject >()
		{
			@Override
			public TestObject execute( final TestObject value )
			{
				return vertex;
			}
		};
		map.transformValues( function );

		for ( final TestObject value : map.valueCollection() )
		{
			assertEquals( "Unexpected value after change.", vertex, value );
		}
	}

	@Test
	public void testRetainEntries()
	{
		final TIntObjectProcedure< TestObject > proc = new TIntObjectProcedure< TestObject >()
		{

			@Override
			public boolean execute( final int a, final TestObject b )
			{
				return b.getId() == storedIds[ 0 ];
			}
		};
		final boolean changed = map.retainEntries( proc );
		assertTrue( "RetainEntries should have changed the map.", changed );
		assertEquals( "There should be only 1 mapping left.", 1, map.size() );
		final TIntObjectIterator< TestObject > it = map.iterator();
		it.advance();
		final TestObject value = it.value();
		assertEquals( "Remaining value is not the right one.", storedIds[ 0 ], value.getId() );
	}
}
