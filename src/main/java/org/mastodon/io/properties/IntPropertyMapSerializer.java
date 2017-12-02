package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

import org.mastodon.collection.RefIntMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.IntPropertyMap;

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
		final RefIntMap< O > pmap = propertyMap.getMap();

		// NUMBER OF ENTRIES
		oos.writeInt( pmap.size() );

		// ENTRIES
		try
		{
			pmap.forEachEntry( ( final O key, final int value ) -> {
				try
				{
					oos.writeInt( idmap.getId( key ) );
					oos.writeInt( value );
				}
				catch ( final IOException e )
				{
					throw new UncheckedIOException( e );
				}
				return true;
			} );
		}
		catch ( final UncheckedIOException e )
		{
			throw e.getCause();
		}
	}

	@Override
	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException
	{
		final RefIntMap< O > pmap = propertyMap.getMap();
		pmap.clear();

		// NUMBER OF ENTRIES
		final int size = ois.readInt();

		// ENTRIES
		final O ref = idmap.createRef();
		for ( int i = 0; i < size; i++ )
		{
			final int key = ois.readInt();
			final int value = ois.readInt();
			pmap.put( idmap.getObject( key, ref ), value );
		}
		idmap.releaseRef( ref );
	}

	@Override
	public IntPropertyMap< O > getPropertyMap()
	{
		return propertyMap;
	}
}
