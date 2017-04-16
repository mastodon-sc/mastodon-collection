package org.mastodon.properties;

import java.util.ArrayList;

public abstract class AbstractProperty< O > implements Property< O >
{
	private final ArrayList< BeforePropertyChangeListener< O > > beforeChangeListeners;

	private final ArrayList< PropertyChangeListener< O > > changeListeners;

	private boolean emitEvents;

	protected AbstractProperty()
	{
		beforeChangeListeners = new ArrayList<>();
		changeListeners = new ArrayList<>();
		emitEvents = true;
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
				l.beforePropertyChange( object );
	}

	protected void notifyPropertyChanged( final O object )
	{
		if ( emitEvents )
			for ( final PropertyChangeListener< O > l : changeListeners )
				l.propertyChanged( object );
	}
}
