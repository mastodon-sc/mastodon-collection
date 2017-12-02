package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;

import org.mastodon.collection.RefObjectMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.ObjPropertyMap;

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
		final RefObjectMap< O, T > pmap = propertyMap.getMap();

		// NUMBER OF ENTRIES
		oos.writeInt( pmap.size() );

		// ENTRIES
		for ( final Entry< O, T > e : pmap.entrySet() )
		{
			oos.writeInt( idmap.getId( e.getKey() ) );
			oos.writeObject( e.getValue() );
		}
	}

	@Override
	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException
	{
		final RefObjectMap< O, T > pmap = propertyMap.getMap();
		pmap.clear();

		// NUMBER OF ENTRIES
		final int size = ois.readInt();

		// ENTRIES
		final O ref = idmap.createRef();
		for ( int i = 0; i < size; i++ )
		{
			final int key = ois.readInt();
			@SuppressWarnings( "unchecked" )
			final T value = ( T ) ois.readObject();
			pmap.put( idmap.getObject( key, ref ), value );
		}
		idmap.releaseRef( ref );
	}

	@Override
	public ObjPropertyMap< O, T > getPropertyMap()
	{
		return propertyMap;
	}
}
