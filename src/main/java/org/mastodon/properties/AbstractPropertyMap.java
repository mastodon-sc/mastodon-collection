package org.mastodon.properties;

import java.util.ArrayList;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.ref.RefPoolBackedRefCollection;

public abstract class AbstractPropertyMap< O, T > implements PropertyMap< O, T >
{
	private PropertyMaps< O > propertyMaps;

	private final ArrayList< BeforePropertyChangeListener< O > > beforeChangeListeners;

	private final ArrayList< PropertyChangeListener< O > > changeListeners;

	private boolean emitEvents;

	protected AbstractPropertyMap()
	{
		beforeChangeListeners = new ArrayList<>();
		changeListeners = new ArrayList<>();
		emitEvents = true;
	}

	@SuppressWarnings( "unchecked" )
	protected void tryRegisterPropertyMap( final RefPool< O > pool )
	{
		if ( pool instanceof HasPropertyMaps )
		{
			propertyMaps = ( ( HasPropertyMaps< O > ) pool ).getPropertyMaps();
			propertyMaps.addPropertyMap( this );
		}
		else
			System.err.println( "WARNING: Creating property map for a collection/pool that does not manage PropertyMaps!" );
	}

	protected void tryRegisterPropertyMap( final RefCollection< O > collection )
	{
		if ( collection instanceof RefPoolBackedRefCollection )
			tryRegisterPropertyMap( ( ( RefPoolBackedRefCollection< O > ) collection ).getRefPool() );
		else
			System.err.println( "WARNING: Creating property map for a collection/pool that does not manage PropertyMaps!" );
	}

	protected void tryUnregisterPropertyMap()
	{
		if ( propertyMaps != null )
			propertyMaps.removePropertyMap( this );
	}

	@Override
	public boolean addBeforePropertyChangeListener( final BeforePropertyChangeListener< O > listener )
	{
		if ( ! beforeChangeListeners.contains( listener ) )
		{
			beforeChangeListeners.add( listener );
			return true;
		}
		return false;
	}

	@Override
	public boolean removeBeforePropertyChangeListener( final BeforePropertyChangeListener< O > listener )
	{
		return beforeChangeListeners.remove( listener );
	}

	@Override
	public boolean addPropertyChangeListener( final PropertyChangeListener< O > listener )
	{
		if ( ! changeListeners.contains( listener ) )
		{
			changeListeners.add( listener );
			return true;
		}
		return false;
	}

	@Override
	public boolean removePropertyChangeListener( final PropertyChangeListener< O > listener )
	{
		return changeListeners.remove( listener );
	}

	@Override
	public void pauseListeners()
	{
		emitEvents = false;
	}

	@Override
	public void resumeListeners()
	{
		emitEvents = true;
	}

	protected void notifyBeforePropertyChange( final O object )
	{
		if ( emitEvents )
			for ( final BeforePropertyChangeListener< O > l : beforeChangeListeners )
				l.beforePropertyChange( this, object );
	}

	protected void notifyPropertyChanged( final O object )
	{
		if ( emitEvents )
			for ( final PropertyChangeListener< O > l : changeListeners )
				l.propertyChanged( this, object );
	}
}
