/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.collection;

import static org.mastodon.collection.RefCollections.tryGetRefPool;

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
 * {@link RefPoolBackedRefCollection}, specialized maps are created that are
 * backed by Trove collections over pool indices. Otherwise, standard
 * {@link java.util.Map}s are created and wrapped as {@link RefRefMap} or
 * similar.
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
			return wrapROM( new RefObjectHashMap<>( keyPool ) );
		}
		else if ( keyPool == null && valuePool != null )
		{
			return wrapORM(new ObjectRefHashMap<>( valuePool ));
		}
		else
		{
			return wrapM( new HashMap< K, V >() );
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
			return wrapM( new HashMap< K, V >( initialCapacity ) );
		}
	}

	public static < T > RefRefMap< T, T > createRefRefMap( final RefCollection< T > collection )
	{
		return createRefRefMap( collection, collection );
	}

	public static < T > RefRefMap< T, T > createRefRefMap( final RefCollection< T > collection, final int initialCapacity )
	{
		return createRefRefMap( collection, collection, initialCapacity );
	}


	public static < K, V > RefObjectMap< K, V > createRefObjectMap( final RefCollection< K > keyCollection )
	{
		final RefPool< K > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new RefObjectHashMap<>( pool );
		else
			return wrapM( new HashMap< K, V >() );
	}

	public static < K, V > RefObjectMap< K, V > createRefObjectMap( final RefCollection< K > keyCollection, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new RefObjectHashMap<>( pool, initialCapacity );
		else
			return wrapM( new HashMap< K, V >( initialCapacity ) );
	}

	public static < K, V > ObjectRefMap< K, V > createObjectRefMap( final RefCollection< V > valueCollection )
	{
		final RefPool< V > pool = tryGetRefPool( valueCollection );
		if ( pool != null )
			return new ObjectRefHashMap<>( pool );
		else
			return wrapM( new HashMap< K, V >() );
	}

	public static < K, V > ObjectRefMap< K, V > createObjectRefMap( final RefCollection< V > valueCollection, final int initialCapacity )
	{
		final RefPool< V > pool = tryGetRefPool( valueCollection );
		if ( pool != null )
			return new ObjectRefHashMap<>( pool, initialCapacity );
		else
			return wrapM( new HashMap< K, V >( initialCapacity ) );
	}

	public static < K > RefIntMap< K > createRefIntMap( final RefCollection< K > keyCollection, final int noEntryValue )
	{
		final RefPool< K > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new RefIntHashMap<>( pool, noEntryValue );
		else
			return new RefIntMapWrapper<>( noEntryValue );
	}

	public static < K > RefIntMap< K > createRefIntMap( final RefCollection< K > keyCollection, final int noEntryValue, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		else
			return new RefIntMapWrapper<>( noEntryValue, initialCapacity );
	}

	public static < K > RefDoubleMap< K > createRefDoubleMap( final RefCollection< K > keyCollection, final double noEntryValue )
	{
		final RefPool< K > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new RefDoubleHashMap<>( pool, noEntryValue );
		else
			return new RefDoubleMapWrapper<>( noEntryValue );
	}

	public static < K > RefDoubleMap< K > createRefDoubleMap( final RefCollection< K > keyCollection, final double noEntryValue, final int initialCapacity )
	{
		final RefPool< K > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new RefDoubleHashMap<>( pool, noEntryValue, initialCapacity );
		else
			return new RefDoubleMapWrapper<>( noEntryValue, initialCapacity );
	}

	public static < V > IntRefMap< V > createIntRefMap( final RefCollection< V > keyCollection, final int noEntryKey )
	{
		final RefPool< V > pool = tryGetRefPool( keyCollection );
		if ( pool != null )
			return new IntRefHashMap<>( pool, noEntryKey );
		else
			return new IntRefMapWrapper<>( noEntryKey );
	}

	public static < V > IntRefMap< V > createIntRefMap( final RefCollection< V > valueCollection, final int noEntryKey, final int initialCapacity )
	{
		final RefPool< V > pool = tryGetRefPool( valueCollection );
		if ( pool != null )
			return new IntRefHashMap<>( pool, noEntryKey, initialCapacity );
		else
			return new IntRefMapWrapper<>( noEntryKey, initialCapacity );
	}

	private static < K, V > RefRefMap< K, V > wrapM( final Map< K, V > map )
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
