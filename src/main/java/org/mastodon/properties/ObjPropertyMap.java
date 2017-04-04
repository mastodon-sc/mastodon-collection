package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.ref.RefObjectHashMap;

public class ObjPropertyMap< O, T > extends AbstractPropertyMap< O, T >
{
	private final RefObjectMap< O, T > map;

	public ObjPropertyMap( final RefCollection< O > pool )
	{
		map = RefCollections.createRefObjectMap( pool );
		tryRegisterPropertyMap( pool );
	}

	public ObjPropertyMap( final RefCollection< O > pool, final int initialCapacity )
	{
		map = RefCollections.createRefObjectMap( pool, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public ObjPropertyMap( final RefPool< O > pool )
	{
		map = new RefObjectHashMap<>( pool );
		tryRegisterPropertyMap( pool );
	}

	public ObjPropertyMap( final RefPool< O > pool, final int initialCapacity )
	{
		map = new RefObjectHashMap<>( pool, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	@Override
	public void set( final O key, final T value )
	{
		notifyBeforePropertyChange( key );
		map.put( key, value );
	}

	@Override
	public void remove( final O key )
	{
		notifyBeforePropertyChange( key );
		map.remove( key );
	}

	@Override
	public T get( final O key )
	{
		return map.get( key );
	}

	@Override
	public boolean isSet( final O key )
	{
		return map.containsKey( key );
	}

	public void release()
	{
		map.clear();
		tryUnregisterPropertyMap();
	}
}
