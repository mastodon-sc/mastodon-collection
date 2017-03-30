package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.ref.RefPoolBackedRefCollection;

public abstract class AbstractPropertyMap< O, T > implements PropertyMap< O, T >
{
	@Override
	public void setPropertyChangeListener( final PropertyChangeListener< O > listener )
	{
		this.propertyChangeListener = listener;
	}

	private PropertyChangeListener< O > propertyChangeListener;

	protected void notifyBeforePropertyChange( final O obj )
	{
		if ( propertyChangeListener != null )
			propertyChangeListener.beforePropertyChange( this, obj );
	}

	protected void tryRegisterPropertyMaps( final RefCollection< O > collection )
	{
		if ( collection instanceof RefPoolBackedRefCollection )
			tryRegisterPropertyMaps( ( ( RefPoolBackedRefCollection< O > ) collection ).getRefPool() );
		else
			System.err.println( "WARNING: Creating property map for a collection/pool that does not manage PropertyMaps!" );
	}

	@SuppressWarnings( "unchecked" )
	protected void tryRegisterPropertyMaps( final RefPool< O > pool )
	{
		if ( pool instanceof HasPropertyMaps )
			( ( HasPropertyMaps< O > ) pool ).getPropertyMaps().addPropertyMap( this );
		else
			System.err.println( "WARNING: Creating property map for a collection/pool that does not manage PropertyMaps!" );
	}
}
