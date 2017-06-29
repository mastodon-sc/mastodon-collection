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
