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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;

import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.ObjPropertyMap;

public class StringPropertyMapSerializer< O > implements PropertyMapSerializer< O, ObjPropertyMap< O, String > >
{
	private final ObjPropertyMap< O, String > propertyMap;

	public StringPropertyMapSerializer( final ObjPropertyMap< O, String > propertyMap )
	{
		this.propertyMap = propertyMap;
	}

	@Override
	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
					throws IOException
	{
		final int numFeatures = propertyMap.size();
		final int[] ids = new int[ numFeatures ];
		final ByteArrayOutputStream bs = new ByteArrayOutputStream();
		final DataOutputStream ds = new DataOutputStream( bs );

		int i = 0;
		for ( final Entry< O, String > e : propertyMap.getMap().entrySet() )
		{
			ids[ i ] = idmap.getId( e.getKey() );
			ds.writeUTF( e.getValue() );
			++i;
		}

		oos.writeObject( ids );
		oos.writeObject( bs.toByteArray() );
	}

	@Override
	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException
	{
		final int[] ids = ( int[] ) ois.readObject();
		final byte[] bsbytes = ( byte[] ) ois.readObject();
		final DataInputStream ds = new DataInputStream( new ByteArrayInputStream( bsbytes ) );

		propertyMap.getMap().clear();
		final O ref = idmap.createRef();
		for ( final int id : ids )
		{
			final String name = ds.readUTF();
			propertyMap.getMap().put( idmap.getObject( id, ref ), name );
		}
		idmap.releaseRef( ref );
	}

	@Override
	public ObjPropertyMap< O, String > getPropertyMap()
	{
		return propertyMap;
	}
}
