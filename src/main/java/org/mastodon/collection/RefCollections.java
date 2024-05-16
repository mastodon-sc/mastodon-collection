/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mastodon.RefPool;
import org.mastodon.collection.ref.RefArrayDeque;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.collection.ref.RefArrayStack;
import org.mastodon.collection.ref.RefPoolBackedRefCollection;
import org.mastodon.collection.ref.RefSetImp;
import org.mastodon.collection.wrap.RefCollectionWrapper;
import org.mastodon.collection.wrap.RefDequeWrapper;
import org.mastodon.collection.wrap.RefListWrapper;
import org.mastodon.collection.wrap.RefSetWrapper;
import org.mastodon.collection.wrap.RefStackWrapper;
import org.mastodon.pool.Pool;

/**
 * Static utility methods to create collections for objects of a specified
 * {@link RefCollection}.
 * <p>
 * This specified {@link RefCollection} is for example the collection of
 * vertices of a graph. Depending on the implementation, this vertex collection
 * could be a {@link Pool} or a wrapped standard {@link java.util.Collection}
 * (see {@link RefCollectionWrapper} etc).
 * <p>
 * If specified {@link RefCollection} implements
 * {@link RefPoolBackedRefCollection}, specialized collections are created that
 * are backed by Trove collections over pool indices. Otherwise, standard
 * {@code java.util} {@link Collections} are created and wrapped as
 * {@link RefCollection}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class RefCollections
{
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static < O > Iterator< O > safeIterator( final Iterator< O > iterator, final RefCollection< O > collection )
	{
		if ( iterator instanceof MaybeRefIterator )
			if ( ( ( MaybeRefIterator ) iterator ).isRefIterator() )
				return new SafeRefIteratorWrapper( iterator, collection );
		return iterator;
	}

	public static < O > RefSet< O > createRefSet( final RefCollection< O > collection )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefSetImp<>( pool );
		else
			return wrap( new HashSet< O >() );
	}

	public static < O > RefSet< O > createRefSet( final RefCollection< O > collection, final int initialCapacity )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefSetImp<>( pool, initialCapacity );
		else
			return wrap( new HashSet< O >( initialCapacity ) );
	}

	public static < O > RefList< O > createRefList( final RefCollection< O > collection )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefArrayList<>( pool );
		else
			return wrap( new ArrayList< O >() );
	}

	public static < O > RefList< O > createRefList( final RefCollection< O > collection, final int initialCapacity )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefArrayList<>( pool, initialCapacity );
		else
			return wrap( new ArrayList< O >( initialCapacity ) );
	}

	public static < O > RefDeque< O > createRefDeque( final RefCollection< O > collection )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefArrayDeque<>( pool );
		else
			return wrap( new ArrayDeque< O >() );
	}

	public static < O > RefDeque< O > createRefDeque( final RefCollection< O > collection, final int initialCapacity )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefArrayDeque<>( pool, initialCapacity );
		else
			return wrap( new ArrayDeque< O >( initialCapacity ) );
	}

	public static < O > RefStack< O > createRefStack( final RefCollection< O > collection )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefArrayStack<>( pool );
		else
			return wrapAsStack( new ArrayDeque< O >() );
	}

	public static < O > RefStack< O > createRefStack( final RefCollection< O > collection, final int initialCapacity )
	{
		final RefPool< O > pool = tryGetRefPool( collection );
		if ( pool != null )
			return new RefArrayStack<>( pool, initialCapacity );
		else
			return wrapAsStack( new ArrayDeque< O >( initialCapacity ) );
	}

	public static < O > RefPool< O > tryGetRefPool( final RefCollection< O > collection )
	{
		return ( collection instanceof RefPoolBackedRefCollection )
				? ( (org.mastodon.collection.ref.RefPoolBackedRefCollection< O > ) collection ).getRefPool()
				: null;
	}

	private static < O > RefSet< O > wrap( final Set< O > set )
	{
		return new RefSetWrapper<>( set );
	}

	private static < O > RefList< O > wrap( final List< O > set )
	{
		return new RefListWrapper<>( set );
	}

	private static < O > RefDeque< O > wrap( final Deque< O > set )
	{
		return new RefDequeWrapper<>( set );
	}

	private static < O > RefStack< O > wrapAsStack( final Deque< O > set )
	{
		return new RefStackWrapper<>( set );
	}
}
