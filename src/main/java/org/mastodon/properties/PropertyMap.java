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
	 */
	public void set( O key, T value );

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
	 * Set the {@link PropertyChangeListener} that is notified when a property
	 * is changed. Specifically,
	 * {@link PropertyChangeListener#beforePropertyChange(PropertyMap, Object)
	 * beforePropertyChange} is triggered as the first step of
	 * {@link #set(Object, Object)} and {@link #remove(Object)}.
	 * <p>
	 * TODO: How about create() on properties that set an initial value?
	 * </p>
	 *
	 * @param listener
	 */
	public void setPropertyChangeListener( PropertyChangeListener< O > listener );
}
