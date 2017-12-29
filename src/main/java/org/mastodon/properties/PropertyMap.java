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
	 * @param value
	 * @return the previous value associated with {@code key} (or {@code null}
	 *         if the property was not set before).
	 */
	public T set( O key, T value );

	/**
	 * Remove the mapping for the specified {@code key} (if it exists).
	 *
	 * @param key
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
	 * {@link PropertyChangeListener#propertyChanged(Object)
	 * propertyChanged} events.
	 * </p>
	 *
	 * @param key
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
