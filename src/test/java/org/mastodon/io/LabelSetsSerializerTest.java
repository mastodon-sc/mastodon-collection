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
package org.mastodon.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.ref.RefSetImp;
import org.mastodon.io.labels.LabelSetsSerializer;
import org.mastodon.labels.LabelSets;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

import gnu.trove.map.hash.TIntIntHashMap;

public class LabelSetsSerializerTest
{
	private TestObjectPool pool;

	private LabelSets< TestObject, Integer > labelsets;

	private ObjectToFileIdMap< TestObject > objectToFileIdMap;

	private FileIdToObjectMap< TestObject > fileIdToObjectMap;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );
		labelsets = new LabelSets<>( pool );
		final TIntIntHashMap objectIdToFileId = new TIntIntHashMap( 10, 0.75f, -1, -1 );
		final TIntIntHashMap fileIdToObjectId = new TIntIntHashMap( 10, 0.75f, -1, -1 );
		final TestObject ref = pool.createRef();
		for ( int i = 0; i < 10; i++ )
		{
			final int id = 20 + i;
			final TestObject a = pool.create( ref ).init( id );
			final Set< Integer > labels = labelsets.getLabels( a );
			labels.add( i );
			labels.add( i + 2 );
			labels.remove( i - 4 );
			labels.remove( 2 * i );
			labels.add( i + 5 );
			labels.remove( 2 * i + 1 );
			objectIdToFileId.put( pool.getId( a ), i );
			fileIdToObjectId.put( i, pool.getId( a ) );
		}
		pool.releaseRef( ref );

		objectToFileIdMap = new ObjectToFileIdMap<>( objectIdToFileId, pool );
		fileIdToObjectMap = new FileIdToObjectMap<>( fileIdToObjectId, pool );
	}

	@Test
	public void test() throws IOException, ClassNotFoundException
	{
		final ByteArrayOutputStream bs = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream( bs );
		final LabelSetsSerializer.LabelSerializer< Integer > labelSerializer = new LabelSetsSerializer.LabelSerializer< Integer >()
		{
			@Override
			public void writeLabel( final Integer label, final ObjectOutputStream oos ) throws IOException
			{
				oos.writeInt( label );
			}

			@Override
			public Integer readLabel( final ObjectInputStream ois ) throws IOException
			{
				return ois.readInt();
			}
		};
		LabelSetsSerializer.writePropertyMap( labelsets, labelSerializer, objectToFileIdMap, oos );
		oos.close();

		final ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( bs.toByteArray() ) );
		final LabelSets< TestObject, Integer > rlabelsets = new LabelSets<>( pool );
		LabelSetsSerializer.readPropertyMap( rlabelsets, labelSerializer, fileIdToObjectMap, ois );

//		pool.forEach( o -> {
//			System.out.println( labelsets.getLabels( o ) );
//			System.out.println( rlabelsets.getLabels( o ) );
//			System.out.println();
//		} );

		pool.forEach( o -> assertEquals( labelsets.getLabels( o ), rlabelsets.getLabels( o ) ) );

		for ( int i = 0; i < 20; ++ i )
		{
			// TODO: add RefSetImp.equals() and hashcode()
			assertEquals( ( ( RefSetImp< TestObject > ) labelsets.getLabeledWith( i ) ).getIndexCollection(), ( ( RefSetImp< TestObject > ) rlabelsets.getLabeledWith( i ) ).getIndexCollection() );
		}
	}
}
