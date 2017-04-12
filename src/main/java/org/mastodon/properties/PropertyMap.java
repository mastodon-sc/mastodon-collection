package org.mastodon.properties;

import org.mastodon.properties.undo.PropertyUndoRedoStack;

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
 *            type of object which the property should be attached to.
 * @param <T>
 *            type of the property.
 *
 * @author Tobias Pietzsch
 */
public interface PropertyMap< O, T >
{
	/**
	 * Set the {@code value} of this property for the specified ({@code key}).
	 *
	 * @param key
	 * @param value
	 * @return the previous value associated with {@code key} (or {@code null}
	 *         if the property was not set before).
	 */
	public T set( O key, T value );

	/**
	 * Remove the mapping for the specified {@code key} (if it exists).
	 *
	 * @param key
	 */
	public void remove( O key );

	/**
	 * For internal use.
	 * <p>
	 * This is called by {@link PropertyMaps#objectCreated(Object)} when a new
	 * object was added. Potentially, we might use this to initialize properties
	 * in the future, but at the moment it does nothing.
	 * </p>
	 *
	 * @param key
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
	 * {@link BeforePropertyChangeListener#beforePropertyChange(PropertyMap, Object)
	 * beforePropertyChange} or
	 * {@link PropertyChangeListener#propertyChanged(PropertyMap, Object)
	 * propertyChanged} events.
	 * </p>
	 *
	 * @param key
	 */
	public void beforeDeleteObject( final O key );

	/**
	 * Returns the value of this property for the specified ({@code key}).
	 *
	 * @param key
	 * @return the value, may be {@code null}.
	 */
	public T get( O key );

	/**
	 * Returns whether this feature value is set for the specified ({@code key})
	 * or not. If {@code false}, the value returned by {@link #get(Object)} is
	 * {@code null}.
	 *
	 * @param key
	 * @return whether a property value is set for the specified data item.
	 */
	public boolean isSet( O key );

	/**
	 * Returns the number of mappings in this {@link PropertyMap}.
	 *
	 * @return the number of mappings.
	 */
	public int size();

	/**
	 * Register a {@link BeforePropertyChangeListener} that will be notified before the
	 * value of this property is changed. Specifically,
	 * {@link BeforePropertyChangeListener#beforePropertyChange(PropertyMap, Object)
	 * beforePropertyChange} is triggered as the first step of
	 * {@link #set(Object, Object)} and {@link #remove(Object)}.
	 *
	 * @param listener
	 *            the listener to register.
	 * @return {@code true} if the listener was successfully registered.
	 *         {@code false} if it was already registered.
	 */
	public boolean addBeforePropertyChangeListener( final BeforePropertyChangeListener< O > listener );

	/**
	 * Removes the specified {@link BeforePropertyChangeListener} from the set of
	 * listeners.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was present in the listeners of this
	 *         model and was successfully removed.
	 */
	public boolean removeBeforePropertyChangeListener( final BeforePropertyChangeListener< O > listener );

	/**
	 * Register a {@link PropertyChangeListener} that will be notified when the
	 * value of this property was changed. Specifically,
	 * {@link PropertyChangeListener#propertyChanged(PropertyMap, Object)
	 * propertyChanged} is triggered as the last step of
	 * {@link #set(Object, Object)} and {@link #remove(Object)}.
	 *
	 * @param listener
	 *            the listener to register.
	 * @return {@code true} if the listener was successfully registered.
	 *         {@code false} if it was already registered.
	 */
	public boolean addPropertyChangeListener( final PropertyChangeListener< O > listener );

	/**
	 * Removes the specified {@link PropertyChangeListener} from the set of
	 * listeners.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was present in the listeners of this
	 *         model and was successfully removed.
	 */
	public boolean removePropertyChangeListener( final PropertyChangeListener< O > listener );


	/**
	 * Pause sending events to {@link BeforePropertyChangeListener}s and
	 * {@link PropertyChangeListener}s.
	 */
	public void pauseListeners();

	/**
	 * Resume sending events to {@link BeforePropertyChangeListener}s and
	 * {@link PropertyChangeListener}s.
	 */
	public void resumeListeners();

	/**
	 * Optional.
	 */
	public default PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		throw new UnsupportedOperationException();
	}
}
