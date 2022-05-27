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
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.pool.OtherTestObject;
import org.mastodon.pool.TestObject;

public class RefRefHashMapValuesTest extends RefRefHashMapAbstractTest
{
	private Collection< OtherTestObject > values;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		values = map.values();

	}

	@Test( expected = UnsupportedOperationException.class )
	public void testAdd()
	{
		values.add( v0 );
	}

	@Test( expected = UnsupportedOperationException.class )
	public void testAddAll()
	{
		final List< OtherTestObject > list = RefCollections.createRefList( otherPool.asRefCollection() );
		list.add( v0 );
		list.add( v1 );
		values.addAll( list );
	}

	@Test
	public void testClear()
	{
		values.clear();
		assertTrue( "Value collection is not empty after clear().", values.isEmpty() );
		assertTrue( "Corresponding map is not empty after value collection clear().", map.isEmpty() );
	}

	@Test
	public void testContains()
	{
		assertTrue( "Expected value could not be found in the value collection.", values.contains( v0 ) );
		final OtherTestObject v5 = otherPool.create().init( 5 );
		map.put( k0, v5 );
		assertTrue( "Value added could not be found in the value collection after map modification.", values.contains( v5 ) );
	}

	@Test
	public void testContainsAll()
	{
		final List< OtherTestObject > list = RefCollections.createRefList( otherPool.asRefCollection() );
		list.add( v0 );
		list.add( v1 );

		assertTrue( "Expected values could not be found in the value collection.", values.containsAll( list ) );

		final OtherTestObject v5 = otherPool.create().init( 5 );
		list.add( v5 );
		assertFalse( "Newly created value should not be found in the value collection before map modification.", values.containsAll( list ) );

		map.put( k0, v5 );
		assertTrue( "Value added could not be found in the value collection after map modification.", values.containsAll( list ) );
	}

	@Test
	public void testIsEmpty()
	{
		final RefRefHashMap< TestObject, OtherTestObject > map2 = new RefRefHashMap<>( pool, otherPool );
		assertTrue( "Value collection of newly created map should be empty.", map2.values().isEmpty() );

		map2.put( k0, v0 );
		assertFalse( "Value collection of map with 1 mappting should not be empty.", map2.values().isEmpty() );
	}

	@Test
	public void testIteratorIterates()
	{
		final Iterator< OtherTestObject > iterator = values.iterator();
		assertTrue( "Newly created iterator should have a next element.", iterator.hasNext() );
		int counter = 0;
		while ( iterator.hasNext() )
		{
			assertNotNull( "Returned objects by the iterator should not be null.", iterator.next() );
			counter++;
		}
		assertEquals( "Iterator did not iterate over the expected number of objects.", map.size(), counter );
	}

	@Test
	public void testIteratorRemoves()
	{
		final int initSize = values.size();
		final Iterator< OtherTestObject > iterator = values.iterator();
		while ( iterator.hasNext() )
		{
			if ( iterator.next().equals( v0 ) )
			{
				iterator.remove();
			}
		}

		assertEquals( "Value collection has not been shrinked by iterator.remove().", initSize - 1, values.size() );
		assertEquals( "Corresponding map has not been shrinked by iterator.remove().", initSize - 1, map.size() );
		assertFalse( "Mapping whose value has been removed should not be in the map.", map.containsKey( k1 ) );
	}

	@Test
	public void testRemove()
	{
//		final int initSize = values.size();
//		final boolean removed = values.remove( eAC );
		/*
		 * FIXME This goddam value cannot be removed, I have no idea why. Others
		 * can be removed without problem, but this one, no. <p> This is a known
		 * problem with the unrerlying TIntIntHashMap. Check
		 * https://bitbucket.org
		 * /trove4j/trove/issue/25/_k__v_hashmaptvalueviewremove-is
		 */
//		assertTrue( "Could not remove an existing value.", removed );
//		assertEquals( "Value collection has not been shrinked by iterator.remove().", initSize - 1, values.size() );
//		assertEquals( "Corresponding map has not been shrinked by iterator.remove().", initSize - 1, map.size() );
//		assertFalse( "Mapping whose value has been removed should not be in the map.", map.containsKey( Bk ) );
	}

	@Test
	public void testRemoveAll()
	{
		final int initSize = values.size();
		final RefList< OtherTestObject > toRemove = RefCollections.createRefList( otherPool.asRefCollection(), 2 );

		// Remove stuff not in the map.
		final OtherTestObject k5 = otherPool.create().init( 5 );
		final OtherTestObject k6 = otherPool.create().init( 6 );
		toRemove.add( k5 );
		toRemove.add( k6 );
		final boolean changed1 = values.removeAll( toRemove );
		assertFalse( "Removing values not in the collection should not change the collection.", changed1 );
		assertEquals( "Value collection should not have been shrinked by this removeAll().", initSize, values.size() );

		// Remove stuff in the map.
		toRemove.add( v0 );
		toRemove.add( v1 );
		final boolean changed2 = values.removeAll( toRemove );
		assertTrue( "Removing values in the collection should change the collection.", changed2 );
//		assertEquals( "Value collection should have been shrinked by this removeAll().", initSize - 2, values.size() );
	}

	@Test
	public void testRetainAll()
	{
		final RefList< OtherTestObject > toRetain = RefCollections.createRefList( otherPool.asRefCollection(), 2 );
		toRetain.add( v0 );
		toRetain.add( v1 );

		final boolean changed = values.retainAll( toRetain );
		assertTrue( "Removing values in the collection should change the collection.", changed );
		assertEquals( "Value collection should have been shrinked by this retainAll().", 2, values.size() );
		assertTrue( "Kept values should still be in the collection.", values.contains( v0 ) );
		assertTrue( "Kept values should still be in the collection.", values.contains( v1 ) );
		assertTrue( "Kept values should still be in the map.", map.containsValue( v0 ) );
		assertTrue( "Kept values should still be in the map.", map.containsValue( v1 ) );
	}
}
