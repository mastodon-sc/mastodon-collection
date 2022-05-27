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
package org.mastodon.pool;

import java.util.Collection;
import java.util.Iterator;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.util.AbstractRefPoolCollectionWrapper;

/**
 * Wrap a {@link Pool} as a {@link RefCollection}. This allows for querying the
 * underlying pool using basic {@link Collection} methods. Only the
 * {@code isEmpty(),} {@code size(),} {@code iterator()} methods are
 * implemented. The remaining {@link Collection} methods are unsuited for pools
 * and throw {@link UnsupportedOperationException}.
 * <p>
 * Moreover, pools wrapped like this can be passed to {@link RefCollections}
 * {@code .create...()} methods for creating specialized {@link RefCollection}s
 * of objects in the pool.
 *
 * @param <O>
 *            the type of the pool object used in the wrapped {@link Pool}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class PoolCollectionWrapper< O extends PoolObject< O, ?, ? > > extends AbstractRefPoolCollectionWrapper< O, Pool< O, ? > >
{
	/**
	 * Wrap the specified {@link Pool} as a {@link RefCollection}.
	 */
	PoolCollectionWrapper( final Pool< O, ? > pool )
	{
		super( pool );
	}

	@Override
	public int size()
	{
		return pool.size();
	}

	@Override
	public Iterator< O > iterator()
	{
		return pool.iterator();
	}
}
