package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.PropertyMaps;

public class RawPropertyIO
{
	/**
	 * Read a collection of {@link PropertyMaps} from an object input stream.
	 *
	 * @param idmap
	 *            the file id-to-object map.
	 * @param serializers
	 *            collection of serializers to read property maps. This must
	 *            contain at least the keys and serializers for the property
	 *            maps listed in the input stream.
	 * @param ois
	 *            the object input stream to read from.
	 * @throws IOException
	 *             if a serializer cannot be found for a key occurring in the
	 *             input stream, or the class of a serialized object cannot be
	 *             found, or for usual I/O errors.
	 */
	public static < O > void readPropertyMaps(
			final FileIdToObjectMap< O > idmap,
			final PropertyMapSerializers< O > serializers,
			final ObjectInputStream ois )
					throws IOException
	{
		try
		{
			final String[] keys = ( String[] ) ois.readObject();
			for ( final String key : keys )
				serializers.getPropertyMap( key ).readPropertyMap( idmap, ois );
		}
		catch ( final ClassNotFoundException e )
		{
			throw new IOException( e );
		}
	}

	/**
	 * Write a collection of {@link PropertyMaps} to an object output stream.
	 * <p>
	 * First, writes a {@code String[]} array with the property map keys (as
	 * assigned in {@link PropertyMapSerializers}). Then calls each
	 * {@link PropertyMapSerializer#writePropertyMap(ObjectToFileIdMap, ObjectOutputStream)}
	 * (in the order of keys).
	 * </p>
	 *
	 * @param idmap
	 *            the object-to-file id map.
	 * @param serializers
	 *            collection of serializers to write property maps. The
	 *            contained keys and property maps are written to the output
	 *            stream.
	 * @param oos
	 *            the output stream for serializing.
	 * @throws IOException
	 */
	public static < O > void writePropertyMaps(
			final ObjectToFileIdMap< O > idmap,
			final PropertyMapSerializers< O > serializers,
			final ObjectOutputStream oos )
					throws IOException
	{
		final String[] keys = serializers.getKeys().toArray( new String[ 0 ] );
		oos.writeObject( keys );
		for ( final String key : keys )
			serializers.getPropertyMap( key ).writePropertyMap( idmap, oos );
	}
}
