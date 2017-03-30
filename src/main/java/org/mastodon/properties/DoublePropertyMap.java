package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.ref.RefDoubleHashMap;

import gnu.trove.map.TObjectDoubleMap;

public class DoublePropertyMap< O > extends AbstractPropertyMap< O, Double >
{
	private final TObjectDoubleMap< O > map;

	public DoublePropertyMap( final RefCollection< O > pool, final double noEntryValue )
	{
		map = RefCollections.createRefDoubleMap( pool, noEntryValue, pool.size() );
		tryRegisterPropertyMap( pool );
	}

	public DoublePropertyMap( final RefPool< O > pool, final double noEntryValue )
	{
		map = new RefDoubleHashMap<>( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public void set( final O key, final double value )
	{
		notifyBeforePropertyChange( key );
		map.put( key, value );
	}

	@Override
	public void set( final O key, final Double value )
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

	public double getDouble( final O key )
	{
		return map.get( key );
	}

	@Override
	public Double get( final O key )
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
