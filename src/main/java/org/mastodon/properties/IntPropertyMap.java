/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.ref.RefIntHashMap;
import org.mastodon.properties.undo.IntPropertyUndoRedoStack;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

public class IntPropertyMap< O > extends AbstractPropertyMap< O, Integer >
{
	private final RefIntMap< O > map;

	private final int noEntryValue;

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue )
	{
		map = RefMaps.createRefIntMap( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = RefMaps.createRefIntMap( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue )
	{
		map = new RefIntHashMap<>( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public int set( final O key, final int value )
	{
		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public Integer set( final O key, final Integer value )
	{
		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	@Override
	public Integer remove( final O key )
	{
		notifyBeforePropertyChange( key );
		final int old = map.remove( key );
		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	public int removeInt( final O key )
	{
		notifyBeforePropertyChange( key );
		final int old = map.remove( key );
		notifyPropertyChanged( key );
		return old;
	}

	public int getInt( final O key )
	{
		return map.get( key );
	}

	@Override
	public Integer get( final O key )
	{
		return map.get( key );
	}

	@Override
	public boolean isSet( final O key )
	{
		return map.containsKey( key );
	}

	@Override
	public int size()
	{
		return map.size();
	}

	public RefIntMap< O > getMap()
	{
		return map;
	}

	public void release()
	{
		map.clear();
		tryUnregisterPropertyMap();
	}

	public int getNoEntryValue()
	{
		return noEntryValue;
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		map.remove( key );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return new IntPropertyUndoRedoStack<>( this );
	}

	@Override
	public void beforeClearPool()
	{
		map.clear();
	}

	@Override
	public void clear()
	{
		map.clear();
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;

		final IntPropertyMap< ? > that = ( IntPropertyMap< ? > ) o;

		return map.equals( that.map );
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}
}
