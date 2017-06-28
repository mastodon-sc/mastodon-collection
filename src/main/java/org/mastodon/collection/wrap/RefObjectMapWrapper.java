package org.mastodon.collection.wrap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.RefRefMap;

/**
 * Wraps a {@link RefObjectMap} as a {@link RefRefMap}.
 */
public class RefObjectMapWrapper< K, V > implements RefRefMap< K, V >
{

	private final RefObjectMap< K, V > map;

	public RefObjectMapWrapper( final RefObjectMap< K, V > map )
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
	public Set< Entry< K, V >> entrySet()
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
	public Set< K > keySet()
	{
		return map.keySet();
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
	public Collection< V > values()
	{
		return map.values();
	}

	@Override
	public K createKeyRef()
	{
		return map.createKeyRef();
	}

	@Override
	public void releaseKeyRef( final K obj )
	{
		map.releaseKeyRef( obj );
	}

	@Override
	public void clear()
	{
		map.clear();
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
		return put( key, value );
	}

	@Override
	public V removeWithRef( final Object key, final V ref )
	{
		return remove( key );
	}

	@Override
	public V get( final Object key, final V ref )
	{
		return get( key );
	}

}
