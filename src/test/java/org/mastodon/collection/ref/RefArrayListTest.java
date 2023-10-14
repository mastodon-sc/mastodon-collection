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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

public class RefArrayListTest
{

	private RefArrayList< TestObject > list;

	private ArrayList< TestObject > objects;

	@Before
	public void setUp() throws Exception
	{
		final int nobj = 1000;
		final TestObjectPool pool = new TestObjectPool( nobj );
		list = new RefArrayList< >( pool );

		final Random rand = new Random();

		final int[] ids = new int[ nobj ];
		for ( int i = 0; i < ids.length; i++ )
		{
			ids[ i ] = i + 1;
		}
		for ( int i = ids.length; i > 1; i-- )
		{
			final int temp = ids[ i - 1 ];
			final int j = rand.nextInt( i );
			ids[ i - 1 ] = ids[ j ];
			ids[ j ] = temp;
		}

		objects = new ArrayList< >( nobj );
		for ( int i = 0; i < ids.length; i++ )
		{
			final TestObject o = pool.create().init( ids[ i ] );
			list.add( o );
			objects.add( o );
		}
	}

	@Test
	public void testSort()
	{
		final Comparator< TestObject > comparator = new Comparator< TestObject >()
		{
			@Override
			public int compare( final TestObject o1, final TestObject o2 )
			{
				return o1.getId() - o2.getId();
			}
		};
		list.sort( comparator );

		int previousID = Integer.MIN_VALUE;
		for ( final TestObject testObject : list )
		{
			assertTrue( "List around ID " + previousID + " is not sorted.", previousID < testObject.getId() );
			previousID = testObject.getId();
		}
	}

}
