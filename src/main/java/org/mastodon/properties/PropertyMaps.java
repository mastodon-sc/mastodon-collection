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
	 *            the map to remove.
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
	 *            the object whose create deletion is to be notified.
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
	 *            the object whose create addition is to be notified.
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
		forEachPropertyMap( PropertyMap::beforeClearPool );
	}

	/**
	 * Forward to {@link PropertyMap#pauseListeners()} of all registered
	 * property maps. Also cleans up maps that have been garbage collected.
	 */
	public void pauseListeners()
	{
		forEachPropertyMap( PropertyMap::pauseListeners );
	}

	/**
	 * Forward to {@link PropertyMap#resumeListeners()} of all registered
	 * property maps. Also cleans up maps that have been garbage collected.
	 */
	public void resumeListeners()
	{
		forEachPropertyMap( PropertyMap::resumeListeners );
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
