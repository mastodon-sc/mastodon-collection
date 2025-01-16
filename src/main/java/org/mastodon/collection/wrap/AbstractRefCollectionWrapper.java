/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.collection.wrap;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefList;

/**
 * Wraps a {@link Collection} as a {@link RefCollection}.
 * <p>
 * This is the base class for wrappers of specific {@link Collection}s, e.g.,
 * {@link RefListWrapper} wraps a {@link List} as a {@link RefList}.
 *
 * @param <O>
 *            type of objects stored in the {@link Collection}.
 * @param <C>
 *            type of the {@link Collection}
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public abstract class AbstractRefCollectionWrapper< O, C extends Collection<  O  > > implements RefCollection< O >
{
	protected final C collection;

	AbstractRefCollectionWrapper( final C collection )
	{
		this.collection = collection;
	}

	@Override
	public int size()
	{
		return collection.size();
	}

	@Override
	public boolean isEmpty()
	{
		return collection.isEmpty();
	}

	@Override
	public boolean contains( final Object o )
	{
		return collection.contains( o );
	}

	@Override
	public Iterator< O > iterator()
	{
		return collection.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return collection.toArray();
	}

	@Override
	public < T > T[] toArray( final T[] a )
	{
		return collection.toArray( a );
	}

	@Override
	public boolean add( final O e )
	{
		return collection.add( e );
	}

	@Override
	public boolean remove( final Object o )
	{
		return collection.remove( o );
	}

	@Override
	public boolean containsAll( final Collection< ? > c )
	{
		return collection.containsAll( c );
	}

	@Override
	public boolean addAll( final Collection< ? extends O > c )
	{
		return collection.addAll( c );
	}

	@Override
	public boolean removeAll( final Collection< ? > c )
	{
		return collection.removeAll( c );
	}

	@Override
	public boolean retainAll( final Collection< ? > c )
	{
		return collection.retainAll( c );
	}

	@Override
	public void clear()
	{
		collection.clear();
	}

	@Override
	public O createRef()
	{
		return null;
	}

	@Override
	public void releaseRef( final O obj )
	{}

}
