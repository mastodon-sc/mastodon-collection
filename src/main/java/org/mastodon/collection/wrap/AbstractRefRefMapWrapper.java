package org.mastodon.collection.wrap;

import java.util.Map;
import java.util.Set;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;

/**
 * Wraps a standard {@link Map} as a {@link RefRefMap}.
 */
public abstract class AbstractRefRefMapWrapper< K, L, M extends Map< K, L > > implements RefRefMap< K, L >
{
	protected final M map;

	public AbstractRefRefMapWrapper( final M map )
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
	public Set< Entry< K, L > > entrySet()
	{
		return map.entrySet();
	}

	@Override
	public L get( final Object key )
	{
		return map.get( key );
	}

	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	@Override
	public L put( final K key, final L value )
	{
		return map.put( key, value );
	}

	@Override
	public void putAll( final Map< ? extends K, ? extends L > m )
	{
		map.putAll( m );
	}

	@Override
	public L remove( final Object key )
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
	public RefCollection< L > values()
	{
		return new RefCollectionWrapper<>( map.values() );
	}

	@Override
	public L createValueRef()
	{
		return null;
	}

	@Override
	public void releaseValueRef( final L obj )
	{}

	@Override
	public L put( final K key, final L value, final L ref )
	{
		return map.put( key, value );
	}

	@Override
	public L removeWithRef( final Object key, final L ref )
	{
		return map.remove( key );
	}

	@Override
	public L get( final Object key, final L ref )
	{
		return map.get( key );
	}
}
