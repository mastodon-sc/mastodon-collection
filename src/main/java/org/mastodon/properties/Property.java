/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.scijava.listeners.Listeners;

/**
 * A property of objects of type {@code O} which listeners can subscribe to to
 * be notified about changes.
 *
 * @param <O>
 *            type of object which the property is attached to.
 *
 * @author Tobias Pietzsch
 */
public interface Property< O >
{
	/**
	 * Returns whether (the value of) this property is set for the specified
	 * object ({@code key}) or not.
	 *
	 * @param key
	 *            the object
	 * @return whether a property is set for the specified object.
	 */
	boolean isSet( O key );

	/**
	 * {@code BeforePropertyChangeListener}s can be added/removed here.
	 * <p>
	 * A {@link BeforePropertyChangeListener} will be notified
	 * before the value of this property is changed. Specifically,
	 * {@link BeforePropertyChangeListener#beforePropertyChange(Object)
	 * beforePropertyChange} is triggered as the first step of
	 * {@link PropertyMap#set(Object, Object)} and
	 * {@link PropertyMap#remove(Object)}.
	 *
	 * @return list of {@code BeforePropertyChangeListener}s
	 */
	Listeners< BeforePropertyChangeListener< O > > beforePropertyChangeListeners();

	/**
	 * {@code PropertyChangeListener}s can be added/removed here.
	 * <p>
	 * A {@link PropertyChangeListener} will be notified when the
	 * value of this property was changed. Specifically,
	 * {@link PropertyChangeListener#propertyChanged(Object) propertyChanged} is
	 * triggered as the last step of {@link PropertyMap#set(Object, Object)} and
	 * {@link PropertyMap#remove(Object)}.
	 *
	 * @return list of {@code PropertyChangeListener}s
	 */
	Listeners< PropertyChangeListener< O > > propertyChangeListeners();

	/**
	 * Pause sending events to {@link BeforePropertyChangeListener}s and
	 * {@link PropertyChangeListener}s.
	 */
	void pauseListeners();

	/**
	 * Resume sending events to {@link BeforePropertyChangeListener}s and
	 * {@link PropertyChangeListener}s.
	 */
	void resumeListeners();

	/**
	 * Unsupported.
	 *
	 * @return nothing. Thrown an exception.
	 */
	default PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		throw new UnsupportedOperationException();
	}
}
