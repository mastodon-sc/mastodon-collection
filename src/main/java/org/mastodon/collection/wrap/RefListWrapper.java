/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.mastodon.collection.RefList;

/**
 * Wraps a standard {@link List} in a {@link RefList}.
 *
 * @param <O>
 *            the type of elements in this list.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class RefListWrapper< O > extends AbstractRefCollectionWrapper< O, List< O > > implements RefList< O >
{
	public RefListWrapper( final List< O > list )
	{
		super( list );
	}

	@Override
	public boolean addAll( final int index, final Collection< ? extends O > c )
	{
		return collection.addAll( index, c );
	}

	@Override
	public O get( final int index )
	{
		return collection.get( index );
	}

	@Override
	public O set( final int index, final O element )
	{
		return collection.set( index, element );
	}

	@Override
	public void add( final int index, final O element )
	{
		collection.add( index, element );
	}

	@Override
	public O remove( final int index )
	{
		return collection.remove( index );
	}

	@Override
	public int indexOf( final Object o )
	{
		return collection.indexOf( o );
	}

	@Override
	public int lastIndexOf( final Object o )
	{
		return collection.lastIndexOf( o );
	}

	@Override
	public ListIterator< O > listIterator()
	{
		return collection.listIterator();
	}

	@Override
	public ListIterator< O > listIterator( final int index )
	{
		return collection.listIterator( index );
	}

	@Override
	public List< O > subList( final int fromIndex, final int toIndex )
	{
		return collection.subList( fromIndex, toIndex );
	}

	@Override
	public O get( final int index, final O obj )
	{
		return collection.get( index );
	}

	@Override
	public O remove( final int index, final O obj )
	{
		return collection.remove( index );
	}

	@Override
	public O set( final int index, final O obj, final O replacedObj )
	{
		return collection.set( index, obj );
	}

	@Override
	public void shuffle( final Random rand )
	{
		Collections.shuffle( collection, rand );
	}

	@Override
	public void sort( final Comparator< ? super O > comparator )
	{
		Collections.sort( collection, comparator );
	}

	@Override
	public void swap( final int i, final int j )
	{
		Collections.swap( collection, i, j );
	}
}
