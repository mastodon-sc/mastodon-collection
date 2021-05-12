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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.mastodon.RefPool;
import org.mastodon.collection.ObjectRefMap;
import org.mastodon.collection.RefCollection;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * A data structure that maps objects to reference objects.
 * <p>
 * Based on a Trove Object -&gt; int hash-map.
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <K>
 *            the type of keys in the map.
 * @param <V>
 *            the type of values in the map. Must be pool objects.
 */
public class ObjectRefHashMap< K, V > implements ObjectRefMap< K, V >
{

	/**
	 * Int value used to declare that the requested value is not in the map.
	 * Negative, so that it cannot be an index in the pool.
	 */
	private static final int NO_ENTRY_VALUE = -1;

	private final TObjectIntHashMap< K > indexmap;

	private final RefPool< V > pool;

	private final Class< V > valueType;

	public ObjectRefHashMap( final RefPool< V > pool )
	{
		this.indexmap = new TObjectIntHashMap<>( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, NO_ENTRY_VALUE );
		this.pool = pool;
		this.valueType = pool.getRefClass();
	}

	public ObjectRefHashMap( final RefPool< V > pool, final int initialCapacity )
	{
		this.indexmap = new TObjectIntHashMap<>( initialCapacity, Constants.DEFAULT_LOAD_FACTOR, NO_ENTRY_VALUE );
		this.pool = pool;
		this.valueType = pool.getRefClass();
	}

	@Override
	public int size()
	{
		return indexmap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return indexmap.isEmpty();
	}

	@Override
	public boolean containsKey( final Object key )
	{
		return indexmap.containsKey( key );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean containsValue( final Object value )
	{
		if ( valueType.isInstance( value ) )
			return indexmap.containsValue( pool.getId( ( V ) value ) );
		else
			return false;
	}

	@Override
	public V get( final Object key )
	{
		return get( key, pool.createRef() );
	}

	@Override
	public V get( final Object key, final V ref )
	{
		final int index = indexmap.get( key );
		if ( index != NO_ENTRY_VALUE )
			return pool.getObject( index, ref );

		return null;
	}

	@Override
	public V put( final K key, final V value )
	{
		return put( key, value, pool.createRef() );
	}

	@Override
	public V put( final K key, final V value, final V ref )
	{
		final int index = indexmap.put( key, pool.getId( value ) );
		if ( index != NO_ENTRY_VALUE )
			return pool.getObject( index, ref );
		else
			return null;
	}

	@Override
	public V remove( final Object key )
	{
		return removeWithRef( key, pool.createRef() );
	}

	@Override
	public V removeWithRef( final Object key, final V ref )
	{
		final int remove = indexmap.remove( key );
		if ( remove != NO_ENTRY_VALUE )
			return pool.getObject( remove, ref );

		return null;
	}

	@Override
	public void putAll( final Map< ? extends K, ? extends V > m )
	{
		if ( m instanceof ObjectRefMap )
		{
			@SuppressWarnings( "unchecked" )
			final ObjectRefMap< K, V > rm = ( ObjectRefMap< K, V > ) m;
			final V ref = createValueRef();
			for ( final K key : rm.keySet() )
				indexmap.put( key, pool.getId( rm.get( key, ref ) ) );

			rm.releaseValueRef( ref );
		}
		else
		{
			for ( final K key : m.keySet() )
				indexmap.put( key, pool.getId( m.get( key ) ) );
		}
	}

	@Override
	public void clear()
	{
		indexmap.clear();
	}

	@Override
	public Set< K > keySet()
	{
		return indexmap.keySet();
	}

	@Override
	public RefCollection< V > values()
	{
		return new CollectionValuesView();
	}

	@Override
	public Set< java.util.Map.Entry< K, V > > entrySet()
	{
		return new EntrySetCollection();
	}

	@Override
	public void releaseValueRef( final V obj )
	{
		pool.releaseRef( obj );
	}

	@Override
	public V createValueRef()
	{
		return pool.createRef();
	}

	@Override
	public String toString() {
		final Iterator<Entry<K,V>> i = entrySet().iterator();
		if (! i.hasNext())
			return "{}";

		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			final Entry<K,V> e = i.next();
			final K key = e.getKey();
			final V value = e.getValue();
			sb.append(key   == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value);
			if (! i.hasNext())
				return sb.append('}').toString();
			sb.append(',').append(' ');
		}
	}

	/*
	 * INNER CLASS
	 */

	private class CollectionValuesView implements RefPoolBackedRefCollection< V >
	{

		@Override
		public boolean add( final V e )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll( final Collection< ? extends V > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear()
		{
			ObjectRefHashMap.this.clear();
		}

		@Override
		public boolean contains( final Object o )
		{
			return ObjectRefHashMap.this.containsValue( o );
		}

		@Override
		public boolean containsAll( final Collection< ? > collection )
		{
			final Iterator< ? > iter = collection.iterator();
			while ( iter.hasNext() )
			{
				if ( !ObjectRefHashMap.this.containsValue( iter.next() ) ) { return false; }
			}
			return true;
		}

		@Override
		public boolean isEmpty()
		{
			return 0 == size();
		}

		/**
		 * Unsafe iterator.
		 */
		@Override
		public Iterator< V > iterator()
		{
			final TIntIterator it = indexmap.valueCollection().iterator();
			final V ref = createValueRef();
			return new Iterator< V >()
			{
				@Override
				public boolean hasNext()
				{
					return it.hasNext();
				}

				@Override
				public V next()
				{
					final int index = it.next();
					return pool.getObject( index, ref );
				}

				@Override
				public void remove()
				{
					it.remove();
				}
			};
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public boolean remove( final Object value )
		{
			if ( valueType.isInstance( value ) )
			{
				return indexmap.valueCollection().remove(
						pool.getId( ( V ) value ) );
			}
			else
			{
				return false;
			}
		}

		@Override
		public boolean removeAll( final Collection< ? > collection )
		{
			boolean changed = false;
			for ( final Object value : collection )
			{
				changed = remove( value ) || changed;
			}
			return changed;
		}

		@Override
		public boolean retainAll( final Collection< ? > collection )
		{
			boolean changed = false;
			final Iterator< V > it = iterator();
			while ( it.hasNext() )
			{
				if ( !collection.contains( it.next() ) )
				{
					it.remove();
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public int size()
		{
			return indexmap.size();
		}

		@Override
		public Object[] toArray()
		{
			final int[] indices = indexmap.values();
			final Object[] obj = new Object[ indices.length ];
			for ( int i = 0; i < obj.length; i++ )
				obj[ i ] = pool.getObject( indices[ i ], createValueRef() );
			return obj;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public < T > T[] toArray( final T[] a )
		{
			if ( a.length < size() ) { return ( T[] ) toArray(); }

			final int[] indices = indexmap.values();
			for ( int i = 0; i < indices.length; i++ )
				a[ i ] = ( T ) pool.getObject( indices[ i ], createValueRef() );
			for ( int i = indices.length; i < a.length; i++ )
				a[ i ] = null;
			return a;
		}

		@Override
		public V createRef()
		{
			return pool.createRef();
		}

		@Override
		public void releaseRef( final V obj )
		{
			pool.releaseRef( obj );
		}

		@Override
		public RefPool< V > getRefPool()
		{
			return pool;
		}
	}

	private class EntrySetCollection implements Set< Entry< K, V > >
	{

		@Override
		public int size()
		{
			return indexmap.size();
		}

		@Override
		public boolean isEmpty()
		{
			return indexmap.isEmpty();
		}

		@Override
		public boolean contains( final Object o )
		{
			if ( null == o )
				return false;
			if ( o instanceof Entry )
			{
				@SuppressWarnings( "unchecked" )
				final Entry< K, V > e1 = ( java.util.Map.Entry< K, V > ) o;
				for ( final java.util.Map.Entry< K, V > e2 : this )
					if ( e1.equals( e2 ) )
						return true;
			}
			return false;
		}

		@Override
		public Iterator< java.util.Map.Entry< K, V > > iterator()
		{
			return new Iterator< Map.Entry< K, V > >()
			{

				private final Iterator< K > keys = indexmap.keySet().iterator();

				private final V ref = pool.createRef();

				private K current;

				private final java.util.Map.Entry< K, V > entry = new Entry< K, V >()
				{

					@Override
					public K getKey()
					{
						return current;
					}

					@Override
					public V getValue()
					{
						return ObjectRefHashMap.this.get( current, ref );
					}

					@Override
					public V setValue( final V value )
					{
						final int index = indexmap.put( current, pool.getId( value ) );
						if ( index != NO_ENTRY_VALUE ) { return pool.getObject( index, ref ); }
						return null;
					}
				};

				@Override
				public boolean hasNext()
				{
					return keys.hasNext();
				}

				@Override
				public java.util.Map.Entry< K, V > next()
				{
					current = keys.next();
					return entry;
				}

			};
		}

		@Override
		public Object[] toArray()
		{
			// Make a deep copy.
			final Object[] obj = new Object[ indexmap.size() ];
			int i = 0;
			for ( final Object key : indexmap.keys() )
			{
				final java.util.Map.Entry< K, V > entry = new Entry< K, V >()
				{
					private V val = pool.getObject( indexmap.get( key ), createValueRef() );

					@SuppressWarnings( "unchecked" )
					@Override
					public K getKey()
					{
						return ( K ) key;
					}

					@Override
					public V getValue()
					{
						return val;
					}

					@Override
					public V setValue( final V value )
					{
						final V tmp = val;
						val = value;
						return tmp;

					}
				};
				obj[ i++ ] = entry;
			}
			return obj;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public < T > T[] toArray( final T[] a )
		{
			return ( T[] ) toArray();
		}

		@Override
		public boolean add( final java.util.Map.Entry< K, V > e )
		{
			final int index = pool.getId( e.getValue() );
			final int oldIndex = indexmap.put( e.getKey(), index );
			return oldIndex != NO_ENTRY_VALUE;
		}

		@Override
		public boolean remove( final Object o )
		{
			if (!contains( o ))
				return false;

			@SuppressWarnings( "unchecked" )
			final Entry< K, V > e = ( java.util.Map.Entry< K, V > ) o;
			indexmap.remove( e.getKey() );
			return true;
		}

		@Override
		public boolean containsAll( final Collection< ? > c )
		{
			for ( final Object o : c )
				if ( !contains( o ) )
					return false;

			return true;
		}

		@Override
		public boolean addAll( final Collection< ? extends java.util.Map.Entry< K, V > > c )
		{
			boolean modified = false;
			for ( final Entry< K, V > entry : c )
			{
				if ( !contains( entry ) )
				{
					add( entry );
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean retainAll( final Collection< ? > c )
		{
			boolean modified = false;
			for ( final Object o : c )
			{
				if ( !contains( o ) )
				{
					remove( o );
					modified = true;
				}

			}
			return modified;
		}

		@Override
		public boolean removeAll( final Collection< ? > c )
		{
			boolean modified = false;
			for ( final Object o : c )
				modified = remove( o ) || modified;
			return modified;
		}

		@Override
		public void clear()
		{
			indexmap.clear();
		}
	}
}
