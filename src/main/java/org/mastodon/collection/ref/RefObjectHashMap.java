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
package org.mastodon.collection.ref;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mastodon.RefPool;
import org.mastodon.collection.RefObjectMap;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Incomplete!
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class RefObjectHashMap< K, V > implements RefObjectMap< K, V >
{
	private final TIntObjectHashMap< V > indexmap;

	private final RefPool< K > pool;

	private final Class< K > keyType;

	private EntrySet entrySet;

	public RefObjectHashMap( final RefPool< K > pool )
	{
		indexmap = new TIntObjectHashMap<>();
		this.pool = pool;
		this.keyType = pool.getRefClass();
	}

	public RefObjectHashMap( final RefPool< K > pool, final int initialCapacity )
	{
		indexmap = new TIntObjectHashMap<>( initialCapacity );
		this.pool = pool;
		this.keyType = pool.getRefClass();
	}

	@Override
	public void clear()
	{
		indexmap.clear();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean containsKey( final Object key )
	{
		if ( keyType.isInstance( key ) )
			return indexmap.containsKey( pool.getId( ( K ) key ) );
		else
			return false;
	}

	@Override
	public boolean containsValue( final Object value )
	{
		return indexmap.containsValue( value );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public V get( final Object key )
	{
		if ( keyType.isInstance( key ) )
			return indexmap.get( pool.getId( ( K ) key ) );
		else
			return null;
	}

	@Override
	public boolean isEmpty()
	{
		return indexmap.isEmpty();
	}

	@Override
	public V put( final K key, final V value )
	{
		return indexmap.put( pool.getId( key ), value );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public V remove( final Object key )
	{
		if ( keyType.isInstance( key ) )
			return indexmap.remove( pool.getId( ( K ) key ) );
		else
			return null;
	}

	@Override
	public int size()
	{
		return indexmap.size();
	}

	@Override
	public Collection< V > values()
	{
		return indexmap.valueCollection();
	}

	@Override
	public RefSetImp< K > keySet()
	{
		return new RefSetImp<>( pool, indexmap.keySet() );
	}

	@Override
	public void putAll( final Map< ? extends K, ? extends V > m )
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public Set< Entry< K, V > > entrySet()
	{
		return ( entrySet == null ) ? ( entrySet = new EntrySet() ) : entrySet;
	}

	final class EntrySet extends AbstractSet< Entry< K, V > >
	{
		@Override
		public Iterator< Entry< K, V > > iterator()
		{
			final TIntObjectIterator< V > iter = indexmap.iterator();

			final Entry< K, V > entry = new Entry< K, V >()
			{
				final K ref = pool.createRef();

				@Override
				public K getKey()
				{
					return pool.getObject( iter.key(), ref );
				}

				@Override
				public V getValue()
				{
					return iter.value();
				}

				@Override
				public V setValue( final V value )
				{
					return iter.setValue( value );
				}
			};

			return new Iterator< Entry< K, V > >()
			{
				@Override
				public boolean hasNext()
				{
					return iter.hasNext();
				}

				@Override
				public Entry< K, V > next()
				{
					iter.advance();
					return entry;
				}
			};
		}

		@Override
		public int size()
		{
			return RefObjectHashMap.this.size();
		}
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;

		final RefObjectHashMap< ?, ? > that = ( RefObjectHashMap< ?, ? > ) o;

		if ( !indexmap.equals( that.indexmap ) )
			return false;
		if ( !pool.equals( that.pool ) )
			return false;
		return keyType.equals( that.keyType );
	}

	@Override
	public int hashCode()
	{
		int result = indexmap.hashCode();
		result = 31 * result + pool.hashCode();
		result = 31 * result + keyType.hashCode();
		return result;
	}
}
