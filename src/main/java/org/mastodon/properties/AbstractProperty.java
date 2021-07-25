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
package org.mastodon.properties;

import org.scijava.listeners.Listeners;

public abstract class AbstractProperty< O > implements Property< O >
{
	private Listeners.List< BeforePropertyChangeListener< O > > beforeChangeListeners;

	private Listeners.List< PropertyChangeListener< O > > changeListeners;

	private boolean emitEvents;

	protected AbstractProperty()
	{
		beforeChangeListeners = new Listeners.List<>();
		changeListeners = new Listeners.List<>();
		emitEvents = true;
	}

	@Override
	public Listeners< BeforePropertyChangeListener< O > > beforePropertyChangeListeners()
	{
		return beforeChangeListeners;
	}

	@Override
	public Listeners< PropertyChangeListener< O > > propertyChangeListeners()
	{
		return changeListeners;
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
			for ( final BeforePropertyChangeListener< O > l : beforeChangeListeners.list )
				l.beforePropertyChange( object );
	}

	protected void notifyPropertyChanged( final O object )
	{
		if ( emitEvents )
			for ( final PropertyChangeListener< O > l : changeListeners.list )
				l.propertyChanged( object );
	}
}
