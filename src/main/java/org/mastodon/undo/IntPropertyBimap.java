/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.undo;

import org.mastodon.RefPool;
import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.ref.IntRefHashMap;
import org.mastodon.collection.ref.RefIntHashMap;
import org.mastodon.properties.AbstractPropertyMap;

/**
 * Property map ({@code O} keys to {@code int} values) that also keeps a reverse
 * map ({@code int} keys to {@code O} values). This is used to implement
 * {@link UndoIdBimap}.
 *
 * @param <O>
 *            the object type
 *
 * @author Tobias Pietzsch
 */
public class IntPropertyBimap< O > extends AbstractPropertyMap< O, Integer >
{
	private final int noEntryValue;

	private final RefIntMap< O > map;

	private final IntRefMap< O > rmap;

	public IntPropertyBimap( final RefCollection< O > pool, final int noEntryValue )
	{
		this.noEntryValue = noEntryValue;
		map = RefMaps.createRefIntMap( pool, noEntryValue );
		rmap = RefMaps.createIntRefMap( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyBimap( final RefCollection< O > pool, final int noEntryValue, final int initialCapacity )
	{
		this.noEntryValue = noEntryValue;
		map = RefMaps.createRefIntMap( pool, noEntryValue, initialCapacity );
		rmap = RefMaps.createIntRefMap( pool, noEntryValue, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyBimap( final RefPool< O > pool, final int noEntryValue )
	{
		this.noEntryValue = noEntryValue;
		map = new RefIntHashMap<>( pool, noEntryValue );
		rmap = new IntRefHashMap<>( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyBimap( final RefPool< O > pool, final int noEntryValue, final int initialCapacity )
	{
		this.noEntryValue = noEntryValue;
		map = new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		rmap = new IntRefHashMap<>( pool, noEntryValue, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	/*
	 * methods used by UndoIdBimap
	 */

	public void set( final O key, final int value )
	{
//		notifyBeforePropertyChange( key );
		map.put( key, value );
		rmap.put( value, key );
//		notifyPropertyChanged( key );
	}

	public int getValue( final O key )
	{
		return map.get( key );
	}

	public O getKey( final int value, final O ref )
	{
		return rmap.get( value, ref );
	}

	/*
	 * methods below are to satisfy PropertyMap interface
	 */

	@Override
	public Integer set( final O key, final Integer value )
	{
//		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		rmap.put( value, key );
//		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	@Override
	public Integer remove( final O key )
	{
//		notifyBeforePropertyChange( key );
		final int old = map.remove( key );
		if ( old != noEntryValue )
			rmap.remove( old );
//		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	public int removeInt( final O key )
	{
		notifyBeforePropertyChange( key );
		final int old = map.remove( key );
		if ( old != noEntryValue )
			rmap.remove( old );
		notifyPropertyChanged( key );
		return old;
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

	public void release()
	{
		map.clear();
		rmap.clear();
		tryUnregisterPropertyMap();
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		final int value = map.remove( key );
		if ( value != noEntryValue )
			rmap.remove( value );
	}

	@Override
	public void beforeClearPool()
	{
		clear();
	}

	@Override
	public void clear()
	{
		map.clear();
		rmap.clear();
	}
}
