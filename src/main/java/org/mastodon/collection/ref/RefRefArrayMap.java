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
package org.mastodon.collection.ref;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;

/**
 * A {@link RefRefMap} implementation backed by a {@link TIntArrayList}.
 *
 * @param <V>
 *            value type.
 *
 * @author Tobias Pietzsch
 */
public class RefRefArrayMap< K, V > implements RefRefMap< K, V >
{
	/**
	 * Int value used to declare that the requested key is not in the map.
	 * Negative, so that it cannot be an index in the pool.
	 */
	private static final int NO_ENTRY_KEY = -1;

	private final TIntArrayList indexmap;

	private final RefPool< K > keyPool;

	private final RefPool< V > valuePool;

	private final Class< K > keyType;

	private final Class< V > valueType;

	private int size;

	/*
	 * CONSTRUCTORS
	 */

	public RefRefArrayMap( final RefPool< K > keyPool, final RefPool< V > valuePool, final int initialCapacity, final float loadFactor )
	{
		indexmap = new TIntArrayList( initialCapacity, NO_ENTRY_KEY );
		this.keyPool = keyPool;
		this.valuePool = valuePool;
		keyType = keyPool.getRefClass();
		valueType = valuePool.getRefClass();
		size = 0;
	}

	public RefRefArrayMap( final RefPool< K > keyPool, final RefPool< V > valuePool, final int initialCapacity )
	{
		this( keyPool, valuePool, initialCapacity, 0.5f );
	}

	public RefRefArrayMap( final RefPool< K > keyPool, final RefPool< V > valuePool )
	{
		this( keyPool, valuePool, 10 );
	}

	/*
	 * METHODS
	 */

	@Override
	public void clear()
	{
		indexmap.clear();
		size = 0;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean containsKey( final Object key )
	{
		if ( keyType.isInstance( key ) )
		{
			final int keyId = keyPool.getId( ( K ) key );
			return indexmap.size() > keyId && indexmap.get( keyId ) >= 0;
		}
		else
			return false;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean containsValue( final Object value )
	{
		if ( valueType.isInstance( value ) )
		{
			final int valueId = valuePool.getId( ( V ) value );
			return indexmap.contains( valueId );
		}
		else
			return false;
	}

	@Override
	public Set< Entry< K, V > > entrySet()
	{
		// TODO implement
		throw new UnsupportedOperationException();
	}

	@Override
	public V get( final Object key )
	{
		return get( key, valuePool.createRef() );
	}

	@Override
	public V get( final Object key, final V ref )
	{
		if ( keyType.isInstance( key ) )
		{
			@SuppressWarnings( "unchecked" )
			final int keyId = keyPool.getId( ( K ) key );
			final int index = indexmap.get( keyId );
			if ( index >= 0 )
				return valuePool.getObject( index, ref );
		}
		return null;
	}

	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

	@Override
	public RefSet< K > keySet()
	{
		return new KeySetView();
	}

	private V putIndex( final int key, final int objInternalPoolIndex, final V replacedObj )
	{
		while ( key >= indexmap.size() )
			indexmap.add( NO_ENTRY_KEY );

		if ( objInternalPoolIndex < 0 )
			--size;

		final int old = indexmap.set( key, objInternalPoolIndex );
		if ( old >= 0 )
		{
			return valuePool.getObject( old, replacedObj );
		}
		else
		{
			++size;
			return null;
		}
	}

	@Override
	public V put( final K key, final V value, final V ref )
	{
		final int keyId = keyPool.getId( ( K ) key );
		final int valueId = valuePool.getId( value );
		return putIndex( keyId, valueId, ref );
	}

	@Override
	public V put( final K key, final V value )
	{
		return put( key, value, valuePool.createRef() );
	}

	@Override
	public void putAll( final Map< ? extends K, ? extends V > m )
	{
		if ( m instanceof RefRefMap )
		{
			@SuppressWarnings( "unchecked" )
			final RefRefMap< K, V > rm = ( RefRefMap< K, V > ) m;
			final V ref = createValueRef();
			for ( final K key : rm.keySet() )
			{
				indexmap.set( keyPool.getId( key ), valuePool.getId( rm.get( key, ref ) ) );
			}
			rm.releaseValueRef( ref );
		}
		else
		{
			for ( final K key : m.keySet() )
			{
				indexmap.set( keyPool.getId( key ), valuePool.getId( m.get( key ) ) );
			}
		}
	}

	@Override
	public V removeWithRef( final Object key, final V ref )
	{
		if ( keyType.isInstance( key ) )
		{
			@SuppressWarnings( "unchecked" )
			final int keyId = keyPool.getId( ( K ) key );
			return putIndex( keyId, NO_ENTRY_KEY, ref );
		}
		return null;
	}

	@Override
	public V remove( final Object key )
	{
		return removeWithRef( key, valuePool.createRef() );
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public RefCollection< V > values()
	{
		// TODO implement
		throw new UnsupportedOperationException();
	}

	@Override
	public V createValueRef()
	{
		return valuePool.createRef();
	}

	@Override
	public void releaseValueRef( final V obj )
	{
		valuePool.releaseRef( obj );
	}

	// TODO revise after implementing entrySet()
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		final V ref = createValueRef();
		final Iterator< K > it = keySet().iterator();
		sb.append( "{ " );
		while ( it.hasNext() )
		{
			final K key = it.next();
			final V val = get( key, ref );
			sb.append( key );
			sb.append( '=' ).append( '"' );
			sb.append( val );
			sb.append( '"' );
			if ( it.hasNext() )
			{
				sb.append( ',' ).append( ' ' );
			}
		}
		sb.append( " }" );
		return sb.toString();
	}

	/*
	 * INNER CLASSES
	 */

	private final class KeySetView implements RefSet< K >
	{
		@Override
		public int size()
		{
			return size;
		}

		@Override
		public boolean isEmpty()
		{
			return size == 0;
		}

		@Override
		public boolean contains( final Object key )
		{
			return containsKey( key );
		}


		@Override
		public Iterator< K > iterator()
		{
			final K ref = keyPool.createRef();
			return new Iterator< K >()
			{

				/** Index of element to be returned by subsequent call to next. */
				private int cursor = 0;

				/**
				 * Index of element returned by most recent call to next or
				 * previous. Reset to -1 if this element is deleted by a call to
				 * remove.
				 */
				int lastRet = -1;

				/** {@inheritDoc} */
				@Override
				public boolean hasNext()
				{
					return cursor < indexmap.size() ;
				}

				/** {@inheritDoc} */
				@Override
				public K next()
				{
					try
					{
						while ( indexmap.get( cursor ) < 0 )
						{
							cursor++;
						}
						final int next = cursor;
						lastRet = cursor++;
						// Advance to next now.
						while ( cursor < indexmap.size() && indexmap.get( cursor ) < 0 )
						{
							cursor++;
						}
						if ( cursor >= indexmap.size() )
							cursor = Integer.MAX_VALUE;
						return keyPool.getObject( next, ref );
					}
					catch ( final IndexOutOfBoundsException e )
					{
						throw new NoSuchElementException();
					}
				}

				/** {@inheritDoc} */
				@Override
				public void remove()
				{
					if ( lastRet == -1 )
						throw new IllegalStateException();

					try
					{
						indexmap.set( lastRet, NO_ENTRY_KEY );
						if ( lastRet < cursor )
							cursor--;
						lastRet = -1;
					}
					catch ( final IndexOutOfBoundsException e )
					{
						throw new ConcurrentModificationException();
					}
				}
			};
		}

		@Override
		public K createRef()
		{
			return keyPool.createRef();
		}

		@Override
		public void releaseRef( final K obj )
		{
			keyPool.releaseRef( obj );
		}

		@Override
		public boolean add( final K key )
		{
			throw new UnsupportedOperationException( "add is not supported for keyset view." );
		}

		@Override
		public boolean addAll( final Collection< ? extends K > c )
		{
			throw new UnsupportedOperationException( "addAll is not supported for keyset view." );
		}

		@Override
		public boolean removeAll( final Collection< ? > c )
		{
			boolean changed = false;
			for ( Object o : c )
			{
				changed |= remove( o );
			}
			return changed;
		}

		@Override
		public boolean retainAll( final Collection< ? > c )
		{
			// TODO implement
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove( final Object key )
		{
			final V ref = valuePool.createRef();
			boolean removed = removeWithRef( key, ref ) != null;
			valuePool.releaseRef( ref );
			return removed;
		}

		@Override
		public boolean containsAll( final Collection< ? > c )
		{
			for ( Object o : c )
			{
				if ( !contains( o ) )
					return false;
			}
			return true;
		}

		@Override
		public Object[] toArray()
		{
			// TODO implement
			throw new UnsupportedOperationException();
		}

		@Override
		public < T > T[] toArray( final T[] a )
		{
			// TODO implement
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear()
		{
			RefRefArrayMap.this.clear();
		}
	}
}
