package org.mastodon.io.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.mastodon.properties.PropertyMap;

/**
 * De/serialize a {@link PropertyMap} of type {@code M}.
 *
 * @param <M>
 *            the property map type
 * @param <O>
 *            type of object which the property is attached to.
 *
 * @author Tobias Pietzsch
 */
public class PropertyMapSerializers< O >
{
	private final HashMap< String, PropertyMapSerializer< O, ? > > serializers = new HashMap<>();

	private final ArrayList< String > keys = new ArrayList<>();

	public static final class DuplicateKeyException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public DuplicateKeyException()
		{
			super();
		}

		public DuplicateKeyException( final String message )
		{
			super( message );
		}
	}

	public void put( final String key, final PropertyMapSerializer< O, ? > serializer )
	{
		if ( serializers.containsKey( key ) )
			throw new DuplicateKeyException( String.format( "property key \"%s\" already exists", key ) );
		serializers.put( key, serializer );
		keys.add( key );
	}

	public PropertyMapSerializer< O, ? > getPropertyMap( final String key )
	{
		return serializers.get( key );
	}

	public List< String > getKeys()
	{
		return Collections.unmodifiableList( keys );
	}
}
