/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.collection.wrap;

import java.util.Map;
import java.util.Set;

import org.mastodon.collection.ObjectRefMap;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;

/**
 * Wraps a standard {@link Map} as a {@link RefRefMap}.
 */
public abstract class RefRefMapWrapper< K, V, M extends Map< K, V > > implements RefRefMap< K, V >
{
	protected final M map;

	protected RefRefMapWrapper( final M map )
	{
		this.map = map;
	}

	@Override
	public boolean containsKey( final Object key )
	{
		return map.containsKey( key );
	}

	@Override
	public boolean containsValue( final Object value )
	{
		return map.containsValue( value );
	}

	@Override
	public Set< Entry< K, V > > entrySet()
	{
		return map.entrySet();
	}

	@Override
	public V get( final Object key )
	{
		return map.get( key );
	}

	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	@Override
	public V put( final K key, final V value )
	{
		return map.put( key, value );
	}

	@Override
	public void putAll( final Map< ? extends K, ? extends V > m )
	{
		map.putAll( m );
	}

	@Override
	public V remove( final Object key )
	{
		return map.remove( key );
	}

	@Override
	public int size()
	{
		return map.size();
	}

	@Override
	public void clear()
	{
		map.clear();
	}

	@Override
	public RefSet< K > keySet()
	{
		return new RefSetWrapper<>( map.keySet() );
	}

	@Override
	public RefCollection< V > values()
	{
		return new RefCollectionWrapper<>( map.values() );
	}

	@Override
	public V createValueRef()
	{
		return null;
	}

	@Override
	public void releaseValueRef( final V obj )
	{}

	@Override
	public V put( final K key, final V value, final V ref )
	{
		return map.put( key, value );
	}

	@Override
	public V removeWithRef( final Object key, final V ref )
	{
		return map.remove( key );
	}

	@Override
	public V get( final Object key, final V ref )
	{
		return map.get( key );
	}

	/**
	 * Wraps a standard {@link Map} as a {@link RefRefMap}.
	 */
	public static class FromMap< K, V > extends RefRefMapWrapper< K, V, Map< K, V > >
	{
		public FromMap( final Map< K, V > map )
		{
			super( map );
		}
	}

	/**
	 * Wraps a {@link ObjectRefMap} as a {@link RefRefMap}.
	 */
	public static class FromObjectRefMap< K, V > extends RefRefMapWrapper< K, V, ObjectRefMap< K, V > >
	{
		public FromObjectRefMap( final ObjectRefMap< K, V > map )
		{
			super( map );
		}

		@Override
		public RefCollection< V > values()
		{
			return map.values();
		}

		@Override
		public V createValueRef()
		{
			return map.createValueRef();
		}

		@Override
		public void releaseValueRef( final V obj )
		{
			map.releaseValueRef( obj );
		}

		@Override
		public V put( final K key, final V value, final V ref )
		{
			return map.put( key, value, ref );
		}

		@Override
		public V removeWithRef( final Object key, final V ref )
		{
			return map.removeWithRef( key, ref );
		}

		@Override
		public V get( final Object key, final V ref )
		{
			return map.get( key, ref );
		}
	}

	/**
	 * Wraps a {@link RefObjectMap} as a {@link RefRefMap}.
	 */
	public static class FromRefObjectMap< K, V > extends RefRefMapWrapper< K, V, RefObjectMap< K, V > >
	{
		public FromRefObjectMap( final RefObjectMap< K, V > map )
		{
			super( map );
		}

		@Override
		public RefSet< K > keySet()
		{
			return map.keySet();
		}
	}
}
