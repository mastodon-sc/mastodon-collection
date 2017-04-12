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
