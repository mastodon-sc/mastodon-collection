package org.mastodon.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.io.properties.ObjPropertyMapSerializer;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;
import org.mastodon.properties.ObjPropertyMap;

import gnu.trove.map.hash.TIntIntHashMap;

public class ObjPropertyMapSerializerTest
{
	private TestObjectPool pool;

	private ObjPropertyMap< TestObject, Double > map;

	private ObjectToFileIdMap< TestObject > objectToFileIdMap;

	private FileIdToObjectMap< TestObject > fileIdToObjectMap;

	@Before
	public void setUp() throws Exception
	{
		pool = new TestObjectPool( 10 );
		map = new ObjPropertyMap<>( pool );
		final TIntIntHashMap objectIdToFileId = new TIntIntHashMap( 10, 0.75f, -1, -1 );
		final TIntIntHashMap fileIdToObjectId = new TIntIntHashMap( 10, 0.75f, -1, -1 );
		final TestObject ref = pool.createRef();
		final Random random = new Random();
		for ( int i = 0; i < 10; i++ )
		{
			final int id = 20 + i;
			final TestObject a = pool.create( ref ).init( id );
			map.set( a, random.nextDouble() );
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
		new ObjPropertyMapSerializer<>( map ).writePropertyMap( objectToFileIdMap, oos );
		oos.close();

		final ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( bs.toByteArray() ) );
		final ObjPropertyMap< TestObject, Double > rmap = new ObjPropertyMap<>( pool );
		new ObjPropertyMapSerializer<>( rmap ).readPropertyMap( fileIdToObjectMap, ois );

		assertEquals( map, rmap );
	}
}
