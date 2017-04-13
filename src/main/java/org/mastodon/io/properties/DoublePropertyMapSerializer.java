package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mastodon.collection.RefDoubleMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.DoublePropertyMap;

import gnu.trove.map.hash.TIntDoubleHashMap;

public class DoublePropertyMapSerializer< O > implements PropertyMapSerializer< O, DoublePropertyMap< O > >
{
	private final DoublePropertyMap< O > propertyMap;

	public DoublePropertyMapSerializer( final DoublePropertyMap< O > propertyMap )
	{
		this.propertyMap = propertyMap;
	}

	@Override
	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
					throws IOException
	{
		final TIntDoubleHashMap fmap = new TIntDoubleHashMap();
		final RefDoubleMap< O > pmap = propertyMap.getMap();
		pmap.forEachEntry( ( final O key, final double value ) -> {
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
		final TIntDoubleHashMap fmap = ( TIntDoubleHashMap ) ois.readObject();
		final RefDoubleMap< O > pmap = propertyMap.getMap();
		pmap.clear();
		final O ref = idmap.createRef();
		fmap.forEachEntry( ( final int key, final double value ) -> {
			pmap.put( idmap.getObject( key, ref ), value );
			return true;
		} );
		idmap.releaseRef( ref );
	}

	@Override
	public DoublePropertyMap< O > getPropertyMap()
	{
		return propertyMap;
	}
}
