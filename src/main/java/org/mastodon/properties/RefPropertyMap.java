/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.function.Function;

import org.mastodon.RefPool;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.ref.RefRefArrayMap;
import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.mastodon.properties.undo.RefPropertyUndoRedoStack;

public class RefPropertyMap< O, T > extends AbstractPropertyMap< O, T >
{
	private final RefRefMap< O, T > map;

	private final Function< T, T > createValue;

	public RefPropertyMap( final RefPool< O > keyPool,
			final RefPool< T > valuePool,
			final Function< T, T > createValue )
	{
		map = new RefRefArrayMap<>( keyPool, valuePool );
		tryRegisterPropertyMap( keyPool );
		this.createValue = createValue;
	}

	@Override
	public T set( final O key, final T value )
	{
		notifyBeforePropertyChange( key );
		final T old = map.put( key, value );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public T remove( final O key )
	{
		notifyBeforePropertyChange( key );
		final T old = map.remove( key );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public T get( final O key )
	{
		return map.get( key );
	}

	public T get( final O key, final T ref )
	{
		return map.get( key, ref );
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

	public RefRefMap< O, T > getMap()
	{
		return map;
	}

	public T createValueRef()
	{
		return map.createValueRef();
	}

	public void releaseValueRef( T ref )
	{
		map.releaseValueRef( ref );
	}

	public T createValue( T ref )
	{
		return createValue.apply( ref );
	}

	public T createValue()
	{
		return createValue( createValueRef() );
	}

	public void release()
	{
		map.clear();
		tryUnregisterPropertyMap();
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		map.remove( key );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return new RefPropertyUndoRedoStack<>( this );
	}

	@Override
	public void beforeClearPool()
	{
		map.clear();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException( "TODO" );
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;

		final RefPropertyMap< ?, ? > that = ( RefPropertyMap< ?, ? > ) o;

		return map.equals( that.map );
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}
}
