/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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
