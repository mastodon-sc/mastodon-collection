package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mastodon.collection.RefIntMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.IntPropertyMap;

import gnu.trove.map.hash.TIntIntHashMap;

public class IntPropertyMapSerializer< O > implements PropertyMapSerializer< O, IntPropertyMap< O > >
{
	private final IntPropertyMap< O > propertyMap;

	public IntPropertyMapSerializer( final IntPropertyMap< O > propertyMap )
	{
		this.propertyMap = propertyMap;
	}

	@Override
	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
					throws IOException
	{
		final TIntIntHashMap fmap = new TIntIntHashMap();
		final RefIntMap< O > pmap = propertyMap.getMap();
		pmap.forEachEntry( ( final O key, final int value ) -> {
			fmap.put( idmap.getId( key ), value );
			return true;
		} );
		oos.writeObject( fmap );
	}

	@Override
	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException
	{
		final TIntIntHashMap fmap = ( TIntIntHashMap ) ois.readObject();
		final RefIntMap< O > pmap = propertyMap.getMap();
		pmap.clear();
		final O ref = idmap.createRef();
		fmap.forEachEntry( ( final int key, final int value ) -> {
			pmap.put( idmap.getObject( key, ref ), value );
			return true;
		} );
		idmap.releaseRef( ref );
	}

	@Override
	public IntPropertyMap< O > getPropertyMap()
	{
		return propertyMap;
	}
}
