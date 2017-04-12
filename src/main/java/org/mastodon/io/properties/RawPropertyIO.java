package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.properties.PropertyMaps;

public class RawPropertyIO
{
	/**
	 * Read a collection of {@link PropertyMaps} from an object input stream.
	 *
	 * @param idmap
	 *            the file id-to-object map.
	 * @param serializers
	 *            the feature collection to read. Will be filled with the
	 *            feature maps read from the input stream.
	 * @param ois
	 *            the object input stream to read from.
	 * @param <O>
	 *            the type of object to which features are attached.
	 * @throws IOException
	 *             if a serializer cannot be found for the specified feature, or
	 *             the class of serialized object cannot be found, or for usual
	 *             I/O errors.
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
			{
				System.out.println( "loading property: " + key );
				serializers.getPropertyMap( key ).readPropertyMap( idmap, ois );
			}
		}
		catch ( final ClassNotFoundException e )
		{
			throw new IOException( e );
		}
	}
}
