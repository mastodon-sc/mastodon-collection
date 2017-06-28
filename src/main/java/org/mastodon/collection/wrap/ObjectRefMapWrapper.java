package org.mastodon.collection.wrap;

import java.util.Map;
import java.util.Set;

import org.mastodon.collection.ObjectRefMap;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;

/**
 * Wraps a {@link ObjectRefMap} as a {@link RefRefMap}.
 */
public class ObjectRefMapWrapper< K, L > implements RefRefMap< K, L >
{
	private final ObjectRefMap< K, L > map;

	public ObjectRefMapWrapper( final ObjectRefMap< K, L > map )
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
	public Set< Entry< K, L >> entrySet()
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
	public RefSet< K > keySet()
	{
		return new RefSetWrapper<>( map.keySet() );
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
	public RefCollection< L > values()
	{
		return map.values();
	}

	@Override
	public void clear()
	{
		map.clear();
	}

	@Override
	public L createValueRef()
	{
		return map.createValueRef();
	}

	@Override
	public void releaseValueRef( final L obj )
	{
		map.releaseValueRef( obj );
	}

	@Override
	public L put( final K key, final L value, final L ref )
	{
		return put( key, value );
	}

	@Override
	public L removeWithRef( final Object key, final L ref )
	{
		return remove( key );
	}

	@Override
	public L get( final Object key, final L ref )
	{
		return get( key );
	}
}
