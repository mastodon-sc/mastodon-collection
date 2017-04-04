package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.ref.RefIntHashMap;

import gnu.trove.map.TObjectIntMap;

public class IntPropertyMap< O > extends AbstractPropertyMap< O, Integer >
{
	private final TObjectIntMap< O > map;

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue )
	{
		map = RefCollections.createRefIntMap( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = RefCollections.createRefIntMap( pool, noEntryValue, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue )
	{
		map = new RefIntHashMap<>( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public void set( final O key, final int value )
	{
		notifyBeforePropertyChange( key );
		map.put( key, value );
	}

	@Override
	public void set( final O key, final Integer value )
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

	public int getInteger( final O key )
	{
		return map.get( key );
	}

	@Override
	public Integer get( final O key )
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
