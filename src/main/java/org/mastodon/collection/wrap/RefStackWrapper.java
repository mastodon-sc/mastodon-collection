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

import java.util.Deque;
import java.util.Iterator;

import org.mastodon.collection.RefStack;

/**
 * Wraps a {@link Deque} as a {@link RefStack}.
 *
 * @param <O>
 *            the type of elements maintained by this stack.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class RefStackWrapper< O > extends AbstractRefCollectionWrapper< O, Deque< O > > implements RefStack< O >
{
	public RefStackWrapper( final Deque< O > deque )
	{
		super( deque );
	}

	@Override
	public void push( final O obj )
	{
		collection.push( obj );
	}

	@Override
	public O peek()
	{
		return collection.peek();
	}

	@Override
	public O peek( final O obj )
	{
		return collection.peek();
	}

	@Override
	public O pop()
	{
		return collection.pop();
	}

	@Override
	public O pop( final O obj )
	{
		return collection.pop();
	}

	@Override
	public int search( final Object obj )
	{
		final Iterator< O > iter = collection.descendingIterator();
		int i = 1;
		while ( iter.hasNext() )
		{
			if ( iter.next().equals( obj ) )
				return i;
			++i;
		}
		return -1;
	}
}
