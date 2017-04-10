package org.mastodon.properties;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mastodon.RefPool;

/**
 * Maintains a list of {@link PropertyMap}s, typically for a {@link RefPool}.
 * The purpose is to broadcast {@link #create(Object)} and
 * {@link #remove(Object)} events to all {@link PropertyMap}s such that they can
 * update accordingly.
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
	 * Forward to {@link PropertyMap#remove(Object)} of all registered property
	 * maps. Also cleans up maps that have been garbage collected.
	 *
	 * @param key
	 */
	public void remove( final O key )
	{
		if ( maps.isEmpty() )
			return;

		boolean cleanUp = false;
		for ( final WeakReference< PropertyMap< O, ? > > ref : maps )
		{
			final PropertyMap< O, ? > map = ref.get();
			if ( map != null )
				map.remove( key );
			else
				cleanUp = true;
		}
		if ( cleanUp )
			maps.removeIf( r -> null == r.get() );
	}

	/**
	 * Forward to {@link PropertyMap#create(Object)} of all registered property
	 * maps. Also cleans up maps that have been garbage collected.
	 *
	 * @param key
	 */
	public void create( final O key )
	{
		boolean cleanUp = false;
		for ( final WeakReference< PropertyMap< O, ? > > ref : maps )
		{
			final PropertyMap< O, ? > map = ref.get();
			if ( map != null )
				map.create( key );
			else
				cleanUp = true;
		}
		if ( cleanUp )
			maps.removeIf( r -> null == r.get() );
	}
}
