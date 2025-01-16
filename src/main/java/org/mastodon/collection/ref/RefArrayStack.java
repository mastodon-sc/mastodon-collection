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
package org.mastodon.collection.ref;

import org.mastodon.Ref;
import org.mastodon.RefPool;
import org.mastodon.collection.RefStack;
import org.mastodon.pool.PoolObject;

/**
 * A {@link RefStack} implementation for {@link Ref}s entirely based on a
 * {@link RefArrayList}.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <O>
 *            recursive type of the {@link PoolObject}s stored in this stack.
 */
public class RefArrayStack< O > extends RefArrayList< O > implements RefStack< O >
{

	/*
	 * CONSTRUCTOR
	 */

	/**
	 * Instantiates an empty stack for the specified pool with default capacity.
	 *
	 * @param pool
	 *            the pool to draw objects from in order to build this stack.
	 */
	public RefArrayStack( final RefPool< O > pool )
	{
		super( pool );
	}

	/**
	 * Instantiates an empty stack for the specified pool.
	 *
	 * @param pool
	 *            the pool to draw objects from in order to build this stack.
	 * @param initialCapacity
	 *            the initial capacity.
	 */
	public RefArrayStack( final RefPool< O > pool, final int initialCapacity )
	{
		super( pool, initialCapacity );
	}

	/*
	 * METHODS
	 */

	@Override
	public O peek()
	{
		return get( size() - 1 );
	}

	@Override
	public O peek( final O obj )
	{
		return get( size() - 1, obj );
	}

	@Override
	public O pop()
	{
		return remove( size() - 1 );
	}

	@Override
	public O pop( final O obj )
	{
		return remove( size() - 1, obj );
	}

	@Override
	public void push( final O obj )
	{
		add( obj );
	}

	@Override
	public int search( final Object obj )
	{
		if ( !( elementType.isInstance( obj ) ) )
			return -1;

		@SuppressWarnings( "unchecked" )
		final int value = pool.getId( ( O ) obj );
		final int index = getIndexCollection().lastIndexOf( value );
		if ( index < 0 )
		{
			return -1;
		}
		else
		{
			return size() - index;
		}
	}
}
