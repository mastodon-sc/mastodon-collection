package org.mastodon.properties;

/**
 * A listener that is notified before a property changes (This happens when
 * {@link PropertyMap#set(Object, Object)} or {@link PropertyMap#remove(Object)}
 * is called.)
 */
public interface BeforePropertyChangeListener< O >
{
	public void beforePropertyChange( O object );
}
