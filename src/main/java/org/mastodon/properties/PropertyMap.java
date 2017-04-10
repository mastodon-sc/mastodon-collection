package org.mastodon.properties;

/**
 * A map from objects {@code O} to properties {@code T}.
 * <p>
 * Using this interface, the feature value (for a particular object) can be
 * read, set, and removed. Additionally, it can be checked whether the value is
 * set.
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

	public default void create( final O key )
	{}

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
	 * Register a {@link BeforePropertyChangeListener} that will be notified before the
	 * value of this property is changed. Specifically,
	 * {@link BeforePropertyChangeListener#beforePropertyChange(PropertyMap, Object)
	 * beforePropertyChange} is triggered as the first step of
	 * {@link #set(Object, Object)} and {@link #remove(Object)}.
	 * <p>
	 * TODO: How about create() on properties that set an initial value?
	 * </p>
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
	 * <p>
	 * TODO: How about create() on properties that set an initial value?
	 * </p>
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
}
