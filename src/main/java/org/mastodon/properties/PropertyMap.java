/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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

/**
 * A map from objects {@code O} to properties {@code T}.
 * <p>
 * Using this interface, the feature value (for a particular object) can be
 * read, set, and removed. Additionally, it can be checked whether the value is
 * set.
 * </p>
 * <p>
 * TODO: Consider whether we need more lightweight {@link PropertyMap} without
 * listeners.
 * </p>
 *
 * @param <O>
 *            type of object which the property is attached to.
 * @param <T>
 *            type of the property.
 *
 * @author Tobias Pietzsch
 */
public interface PropertyMap< O, T > extends Property< O >
{
	/**
	 * Set the {@code value} of this property for the specified ({@code key}).
	 *
	 * @param key
	 *            the object.
	 * @param value
	 *            the value to set in this map.
	 * @return the previous value associated with {@code key} (or {@code null}
	 *         if the property was not set before).
	 */
	public T set( O key, T value );

	/**
	 * Remove the mapping for the specified {@code key} (if it exists).
	 *
	 * @param key
	 *            the object.
	 * @return the previous value associated with {@code key} (or {@code null}
	 *         if the property was not set before).
	 */
	public T remove( O key );

	/**
	 * For internal use.
	 * <p>
	 * This is called by {@link PropertyMaps#objectCreated(Object)} when a new
	 * object was added. Potentially, we might use this to initialize properties
	 * in the future, but at the moment it does nothing.
	 * </p>
	 *
	 * @param key
	 *            the object just created.
	 */
	public default void objectCreated( final O key )
	{}

	/**
	 * For internal use.
	 * <p>
	 * This is called by {@link PropertyMaps#beforeDeleteObject(Object)} when a
	 * object is about to be deleted. This will remove the mapping for
	 * {@code key} if it exists. In contrast to {@link #remove(Object)} this
	 * does not emit any
	 * {@link BeforePropertyChangeListener#beforePropertyChange(Object)
	 * beforePropertyChange} or
	 * {@link PropertyChangeListener#propertyChanged(Object) propertyChanged}
	 * events.
	 * </p>
	 *
	 * @param key
	 *            the object to be deleted.
	 */
	public void beforeDeleteObject( final O key );

	/**
	 * For internal use.
	 * <p>
	 * This is called by {@link PropertyMaps#beforeClearPool()} when a pool is
	 * about to be cleared. This will remove all mappings. In contrast to
	 * {@link #clear()} this does not emit any
	 * {@link BeforePropertyChangeListener#beforePropertyChange(Object)
	 * beforePropertyChange} or
	 * {@link PropertyChangeListener#propertyChanged(Object) propertyChanged}
	 * events.
	 * </p>
	 */
	public void beforeClearPool();

	/**
	 * Remove all mappings.
	 */
	public void clear();

	/**
	 * Returns the value of this property for the specified ({@code key}). If
	 * {@code isSet(key) == false} then {@code get(key) == null}.
	 *
	 * @param key
	 *            the object.
	 * @return the value, may be {@code null}.
	 */
	public T get( O key );

	/**
	 * Returns the number of mappings in this {@link PropertyMap}.
	 *
	 * @return the number of mappings.
	 */
	public int size();
}
