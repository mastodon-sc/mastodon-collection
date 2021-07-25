package org.mastodon.properties;

/**
 * A listener that is notified when a property was changed. (This happens when
 * {@link PropertyMap#set(Object, Object)} or {@link PropertyMap#remove(Object)}
 * is called.)
 * 
 * @param <O>
 *            the type of objects in which the property is defined.
 */
public interface PropertyChangeListener< O >
{
	public void propertyChanged( O object );
}
