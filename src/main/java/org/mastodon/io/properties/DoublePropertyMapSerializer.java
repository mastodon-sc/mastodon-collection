package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

import org.mastodon.collection.RefDoubleMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.DoublePropertyMap;

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
		final RefDoubleMap< O > pmap = propertyMap.getMap();

		// NUMBER OF ENTRIES
		oos.writeInt( pmap.size() );

		// ENTRIES
		try
		{
			pmap.forEachEntry( ( final O key, final double value ) -> {
				try
				{
					oos.writeInt( idmap.getId( key ) );
					oos.writeDouble( value );
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
		final RefDoubleMap< O > pmap = propertyMap.getMap();
		pmap.clear();

		// NUMBER OF ENTRIES
		final int size = ois.readInt();

		// ENTRIES
		final O ref = idmap.createRef();
		for ( int i = 0; i < size; i++ )
		{
			final int key = ois.readInt();
			final double value = ois.readDouble();
			pmap.put( idmap.getObject( key, ref ), value );
		}
		idmap.releaseRef( ref );
	}

	@Override
	public DoublePropertyMap< O > getPropertyMap()
	{
		return propertyMap;
	}
}
