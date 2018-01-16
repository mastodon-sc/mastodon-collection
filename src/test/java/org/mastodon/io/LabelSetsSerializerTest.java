package org.mastodon.io;

import gnu.trove.map.hash.TIntIntHashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mastodon.io.labels.LabelSetsSerializer;
import org.mastodon.io.properties.IntPropertyMapSerializer;
import org.mastodon.labels.LabelSet;
import org.mastodon.labels.LabelSets;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;
import org.mastodon.properties.IntPropertyMap;

import static org.junit.Assert.assertEquals;

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
		final Random random = new Random();
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
		new LabelSetsSerializer<>( labelsets, labelSerializer ).writePropertyMap( objectToFileIdMap, oos );
		oos.close();

		final ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( bs.toByteArray() ) );
		final LabelSets< TestObject, Integer > rlabelsets = new LabelSets<>( pool );
		new LabelSetsSerializer<>( rlabelsets, labelSerializer ).readPropertyMap( fileIdToObjectMap, ois );

//		pool.forEach( o -> {
//			System.out.println( labelsets.getLabels( o ) );
//			System.out.println( rlabelsets.getLabels( o ) );
//			System.out.println();
//		} );

		pool.forEach( o -> assertEquals( labelsets.getLabels( o ), rlabelsets.getLabels( o ) ) );
	}
}
