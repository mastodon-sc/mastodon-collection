package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.ref.RefIntHashMap;

import gnu.trove.map.TObjectIntMap;

public class IntPropertyMap< O > extends AbstractPropertyMap< O, Integer >
{
	private final TObjectIntMap< O > map;

	private final int noEntryValue;

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue )
	{
		map = RefCollections.createRefIntMap( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = RefCollections.createRefIntMap( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue )
	{
		map = new RefIntHashMap<>( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public int set( final O key, final int value )
	{
		notifyBeforePropertyChange( key );
		return map.put( key, value );
	}

	@Override
	public Integer set( final O key, final Integer value )
	{
		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	@Override
	public void remove( final O key )
	{
		notifyBeforePropertyChange( key );
		map.remove( key );
	}

	public int getInt( final O key )
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

	public int getNoEntryValue()
	{
		return noEntryValue;
	}
}