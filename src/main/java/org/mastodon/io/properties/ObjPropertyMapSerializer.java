package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;

import org.mastodon.collection.RefObjectMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.ObjPropertyMap;

import gnu.trove.map.hash.TIntObjectHashMap;

public class ObjPropertyMapSerializer< O, T > implements PropertyMapSerializer< O, ObjPropertyMap< O, T > >
{
	private final ObjPropertyMap< O, T > propertyMap;

	public ObjPropertyMapSerializer( final ObjPropertyMap< O, T > propertyMap )
	{
		this.propertyMap = propertyMap;
	}

	@Override
	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
					throws IOException
	{
		final TIntObjectHashMap< T > fmap = new TIntObjectHashMap< >();
		final RefObjectMap< O, T > pmap = propertyMap.getMap();
		for ( final Entry< O, T > e : pmap.entrySet() )
			fmap.put( idmap.getId( e.getKey() ), e.getValue() );
		oos.writeObject( fmap );
	}

	@Override
	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException
	{
		@SuppressWarnings( "unchecked" )
		final TIntObjectHashMap< T > fmap = ( TIntObjectHashMap< T > ) ois.readObject();
		final RefObjectMap< O, T > pmap = propertyMap.getMap();
		pmap.clear();
		final O ref = idmap.createRef();
		fmap.forEachEntry( ( final int key, final T value ) -> {
			pmap.put( idmap.getObject( key, ref ), value );
			return true;
		} );
		idmap.releaseRef( ref );
	}

	@Override
	public ObjPropertyMap< O, T > getPropertyMap()
	{
		return propertyMap;
	}
}
