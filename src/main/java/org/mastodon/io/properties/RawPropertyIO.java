/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
	 * @param <O>
	 *            the type of objects for which the maps are defined.
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
	 * @param <O>
	 *            the type of objects for which the maps are defined.
	 * @throws IOException
	 *             if there is a problem writing to the output stream.
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
