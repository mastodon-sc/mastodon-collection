package org.mastodon.properties;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.mastodon.RefPool;

/**
 * Maintains a list of {@link PropertyMap}s, typically for a {@link RefPool}.
 * The purpose is to broadcast {@link #objectCreated(Object)} and
 * {@link #beforeDeleteObject(Object)} events to all {@link PropertyMap}s such
 * that they can update accordingly.
 * <p>
 * The list only maintains {@link WeakReference}s to {@link PropertyMap}s. This
 * makes it easier to create temporary maps without worrying about book-keeping.
 * Nevertheless, it is possible to explicitly remove maps.
 * </p>
 *
 * @param <O>
 *            object type (key type for all {@link PropertyMap}s)
 *
 * @author Tobias Pietzsch
 */
public class PropertyMaps< O >
{
	private final List< WeakReference< PropertyMap< O, ? > > > maps = new CopyOnWriteArrayList<>();

	/**
	 * Add a {@link PropertyMap}. The specified property map will be notified
	 * about addition and removal of objects.
	 *
	 * @param map
	 *            the property map to add.
	 */
	public void addPropertyMap( final PropertyMap< O, ? > map )
	{
		maps.add( new WeakReference< PropertyMap< O, ? > >( map ) );
	}

	/**
	 * Remove a {@link PropertyMap}. The specified property map will no longer
	 * be notified about addition and removal of objects.
	 *
	 * @param map
	 */
	public void removePropertyMap( final PropertyMap< O, ? > map )
	{
		maps.removeIf( r -> map.equals( r.get() ) );
	}

	/**
	 * Forward to {@link PropertyMap#beforeDeleteObject(Object)} of all
	 * registered property maps. Also cleans up maps that have been garbage
	 * collected.
	 *
	 * @param key
	 */
	public void beforeDeleteObject( final O key )
	{
		forEachPropertyMap( m -> m.beforeDeleteObject( key ) );
	}

	/**
	 * Forward to {@link PropertyMap#objectCreated(Object)} of all registered
	 * property maps. Also cleans up maps that have been garbage collected.
	 *
	 * @param key
	 */
	public void objectCreated( final O key )
	{
		forEachPropertyMap( m -> m.objectCreated( key ) );
	}

	/**
	 * Forward to {@link PropertyMap#beforeClearPool()} of all registered
	 * property maps. Also cleans up maps that have been garbage collected.
	 */
	public void beforeClearPool()
	{
		forEachPropertyMap( m -> m.beforeClearPool() );
	}

	private void forEachPropertyMap( final Consumer< PropertyMap< O, ? > > consumer )
	{
		boolean cleanUp = false;
		for ( final WeakReference< PropertyMap< O, ? > > ref : maps )
		{
			final PropertyMap< O, ? > map = ref.get();
			if ( map != null )
				consumer.accept( map );
			else
				cleanUp = true;
		}
		if ( cleanUp )
			maps.removeIf( r -> null == r.get() );
	}
}
