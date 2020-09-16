package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.ref.RefPoolBackedRefCollection;

public abstract class AbstractPropertyMap< O, T > extends AbstractProperty< O > implements PropertyMap< O, T >
{
	private PropertyMaps< O > propertyMaps;

	protected AbstractPropertyMap()
	{
		super();
	}

	@SuppressWarnings( "unchecked" )
	protected void tryRegisterPropertyMap( final RefPool< O > pool )
	{
		if ( pool instanceof HasPropertyMaps )
		{
			propertyMaps = ( ( HasPropertyMaps< O > ) pool ).getPropertyMaps();
			propertyMaps.addPropertyMap( this );
		}
//		else
//			System.err.println( "WARNING: Creating property map for a collection/pool that does not manage PropertyMaps!" );
	}

	protected void tryRegisterPropertyMap( final RefCollection< O > collection )
	{
		if ( collection instanceof RefPoolBackedRefCollection )
			tryRegisterPropertyMap( ( ( RefPoolBackedRefCollection< O > ) collection ).getRefPool() );
//		else
//			System.err.println( "WARNING: Creating property map for a collection/pool that does not manage PropertyMaps!" );
	}

	protected void tryUnregisterPropertyMap()
	{
		if ( propertyMaps != null )
			propertyMaps.removePropertyMap( this );
	}
}
