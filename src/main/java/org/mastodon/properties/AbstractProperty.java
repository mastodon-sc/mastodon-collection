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
