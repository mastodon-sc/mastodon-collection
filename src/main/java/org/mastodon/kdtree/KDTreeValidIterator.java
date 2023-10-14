/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.kdtree;

import java.util.Iterator;

import org.mastodon.RefPool;
import org.mastodon.pool.MappedElement;

import net.imglib2.RealLocalizable;

public class KDTreeValidIterator<
		O extends RealLocalizable,
		T extends MappedElement >
	implements Iterator< O >
{
	public static <
			O extends RealLocalizable,
			T extends MappedElement >
		KDTreeValidIterator< O, T > create( final KDTree< O, T > kdtree )
	{
		return new KDTreeValidIterator<>( kdtree );
	}

	private final O ref;

	private final O nextref;

	private O next;

	private final Iterator< KDTreeNode< O, T > > kdtreeIter;

	private final RefPool< O > objPool;

	private boolean hasNext;

	public KDTreeValidIterator( final KDTree< O, T > tree )
	{
		this.objPool = tree.getObjectPool();
		ref = objPool.createRef();
		nextref = objPool.createRef();
		kdtreeIter = tree.iterator();
		hasNext = prepareNext();
	}

	private boolean prepareNext()
	{
		while ( kdtreeIter.hasNext() )
		{
			final KDTreeNode< O, T > n = kdtreeIter.next();
			if ( n.isValid() )
			{
				next = objPool.getObject( n.getDataIndex(), nextref );
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasNext()
	{
		return hasNext;
	}

	@Override
	public O next()
	{
		if ( hasNext )
		{
			final O current = objPool.getObject( objPool.getId( next ), ref );
			hasNext = prepareNext();
			return current;
		}
		return null;
	}
}
