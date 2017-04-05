package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.ref.RefDoubleHashMap;

import gnu.trove.map.TObjectDoubleMap;

public class DoublePropertyMap< O > extends AbstractPropertyMap< O, Double >
{
	private final TObjectDoubleMap< O > map;

	private final double noEntryValue;

	public DoublePropertyMap( final RefCollection< O > pool, final double noEntryValue )
	{
		map = RefCollections.createRefDoubleMap( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public DoublePropertyMap( final RefCollection< O > pool, final double noEntryValue, final int initialCapacity )
	{
		map = RefCollections.createRefDoubleMap( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public DoublePropertyMap( final RefPool< O > pool, final double noEntryValue )
	{
		map = new RefDoubleHashMap<>( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public DoublePropertyMap( final RefPool< O > pool, final double noEntryValue, final int initialCapacity )
	{
		map = new RefDoubleHashMap<>( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public double set( final O key, final double value )
	{
		notifyBeforePropertyChange( key );
		return map.put( key, value );
	}

	@Override
	public Double set( final O key, final Double value )
	{
		notifyBeforePropertyChange( key );
		final double old = map.put( key, value );
		return ( old == noEntryValue ) ? null : Double.valueOf( old );
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

	public double getNoEntryValue()
	{
		return noEntryValue;
	}
}
