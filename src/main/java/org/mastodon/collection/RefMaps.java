package org.mastodon.collection;

import java.util.HashMap;
import java.util.Map;

import org.mastodon.RefPool;
import org.mastodon.collection.ref.IntRefHashMap;
import org.mastodon.collection.ref.ObjectRefHashMap;
import org.mastodon.collection.ref.RefDoubleHashMap;
import org.mastodon.collection.ref.RefIntHashMap;
import org.mastodon.collection.ref.RefObjectHashMap;
import org.mastodon.collection.ref.RefPoolBackedRefCollection;
import org.mastodon.collection.ref.RefRefHashMap;
import org.mastodon.collection.wrap.IntRefMapWrapper;
import org.mastodon.collection.wrap.RefDoubleMapWrapper;
import org.mastodon.collection.wrap.RefIntMapWrapper;
import org.mastodon.collection.wrap.RefRefMapWrapper;
import org.mastodon.pool.Pool;

/**
 * Static utility methods to create maps for objects of a specified
 * {@link RefCollection}.
 * <p>
 * This specified {@link RefCollection} is for example the collection of
 * vertices of a graph. Depending on the implementation, this vertex collection
 * could be a {@link Pool} or a wrapped standard {@link java.util.Collection}.
 * <p>
 * If specified {@link RefCollection} implements
 * {@link RefPoolBackedRefCollection}, specialized maps are created that
 * are backed by Trove collections over pool indices. Otherwise, standard
 * {@code java.util} {@link Maps} are created and wrapped as
 * {@link RefRefMap} or similar.
 *
 * @author Tobias Pietzsch
 * @author Jean-Yves Tinevez
 */
public class RefMaps
{

	public static < K, V > RefRefMap< K, V > createRefRefMap( final RefCollection< K > keyCollection, final RefCollection< V > valueCollection )
	{
		final RefPool< K > keyPool = tryGetRefPool( keyCollection );
		final RefPool< V > valuePool = tryGetRefPool( valueCollection );
		if ( keyPool != null && valuePool != null )
		{
			return new RefRefHashMap<>( keyPool, valuePool );
		}
		else if ( keyPool != null && valuePool == null )
		{
			return wrap( new RefObjectHashMap<>( keyPool ) );
		}
		else if ( keyPool == null && valuePool != null )
		{
			return wrapORM(new ObjectRefHashMap<>( valuePool ));
		}
		else
		{
			return wrap( new HashMap< K, V >() );
		}
	}

	public static < K, V > RefRefMap< K, V > createRefRefMap( final RefCollection< K > keyCollection, final RefCollection< V > valueCollection, final int initialCapacity )
	{
		final RefPool< K > keyPool = tryGetRefPool( keyCollection );
		final RefPool< V > valuePool = tryGetRefPool( valueCollection );
		if ( keyPool != null && valuePool != null )
		{
			return new RefRefHashMap<>( keyPool, valuePool, initialCapacity );
		}
		else if ( keyPool != null && valuePool == null )
		{
			return wrapROM( new RefObjectHashMap<>( keyPool, initialCapacity ) );
		}
		else if ( keyPool == null && valuePool != null )
		{
			return wrapORM(new ObjectRefHashMap<>( valuePool, initialCapacity ));
		}
		else
		{
			return wrap( new HashMap< K, V >( initialCapacity ) );
		}
	}

	public static < K, V > RefObjectMap< K, V > createRefObjectMap( final RefCollection< K > collection )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefObjectHashMap<>( pool );
		else
			return wrap( new HashMap< K, V >() );
	}

	public static < K, V > ObjectRefMap< K, V > createObjectRefMap( final RefCollection< V > collection, final int initialCapacity )
	{
		final RefPool< V > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new ObjectRefHashMap<>( pool, initialCapacity );
		else
			return wrap( new HashMap< K, V >( initialCapacity ) );
	}

	public static < K, V > ObjectRefMap< K, V > createObjectRefMap( final RefCollection< V > collection )
	{
		final RefPool< V > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new ObjectRefHashMap<>( pool );
		else
			return wrap( new HashMap< K, V >() );
	}

	public static < K, V > RefObjectMap< K, V > createRefObjectMap( final RefCollection< K > collection, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefObjectHashMap<>( pool, initialCapacity );
		else
			return wrap( new HashMap< K, V >( initialCapacity ) );
	}

	public static < K > RefRefMap< K, K > createRefRefMap( final RefCollection< K > collection )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefRefHashMap<>( pool, pool );
		else
			return wrap( new HashMap< K, K >() );
	}

	public static < K > RefRefMap< K, K > createRefRefMap( final RefCollection< K > collection, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefRefHashMap<>( pool, pool, initialCapacity );
		else
			return wrap( new HashMap< K, K >( initialCapacity ) );
	}

	public static < K > RefIntMap< K > createRefIntMap( final RefCollection< K > collection, final int noEntryValue )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefIntHashMap<>( pool, noEntryValue );
		else
			return new RefIntMapWrapper<>( noEntryValue );
	}

	public static < K > RefIntMap< K > createRefIntMap( final RefCollection< K > collection, final int noEntryValue, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		else
			return new RefIntMapWrapper<>( noEntryValue, initialCapacity );
	}

	public static < K > RefDoubleMap< K > createRefDoubleMap( final RefCollection< K > collection, final double noEntryValue )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefDoubleHashMap<>( pool, noEntryValue );
		else
			return new RefDoubleMapWrapper<>( noEntryValue );
	}

	public static < K > RefDoubleMap< K > createRefDoubleMap( final RefCollection< K > collection, final double noEntryValue, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefDoubleHashMap<>( pool, noEntryValue, initialCapacity );
		else
			return new RefDoubleMapWrapper<>( noEntryValue, initialCapacity );
	}

	public static < V > IntRefMap< V > createIntRefMap( final RefCollection< V > collection, final int noEntryKey )
	{
		final RefPool< V > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new IntRefHashMap<>( pool, noEntryKey );
		else
			return new IntRefMapWrapper<>( noEntryKey );
	}

	public static < V > IntRefMap< V > createIntRefMap( final RefCollection< V > collection, final int noEntryKey, final int initialCapacity )
	{
		final RefPool< V > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new IntRefHashMap<>( pool, noEntryKey, initialCapacity );
		else
			return new IntRefMapWrapper<>( noEntryKey, initialCapacity );
	}

	private static < O > RefPool< O > tryGetRefPool( final RefCollection< O > collection )
	{
		return ( collection instanceof RefPoolBackedRefCollection )
				? ( ( org.mastodon.collection.ref.RefPoolBackedRefCollection< O > ) collection ).getRefPool()
				: null;
	}

	private static < K, V > RefRefMap< K, V > wrap( final Map< K, V > map )
	{
		return new RefRefMapWrapper.FromMap<>( map );
	}


	private static < K, V > RefRefMap< K, V > wrapORM( final ObjectRefMap< K, V > map )
	{
		return new RefRefMapWrapper.FromObjectRefMap<>( map );
	}


	private static < K, V > RefRefMap< K, V > wrapROM( final RefObjectMap< K, V > map )
	{
		return new RefRefMapWrapper.FromRefObjectMap<>(map);
	}

	private RefMaps()
	{}

}
