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
package org.mastodon.kdtree;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

import net.imglib2.EuclideanSpace;

class RealPointPool extends Pool< RealPoint, ByteMappedElement > implements EuclideanSpace
{
	static class RealPointLayout extends PoolObjectLayout
	{
		final IntField magicNumberField;
		final DoubleArrayField position;

		RealPointLayout( final int numDimensions )
		{
			magicNumberField = intField();
			position = doubleArrayField( numDimensions );
		}
	}

	final RealPointAttribute< RealPoint > position;

	public RealPointPool( final int numDimensions, final int initialCapacity )
	{
		this( new RealPointLayout( numDimensions ), initialCapacity );
	}

	private RealPointPool( final RealPointLayout layout, final int initialCapacity )
	{
		super( initialCapacity, layout, RealPoint.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		position = new RealPointAttribute<>( layout.position, this );
	}

	@Override
	public RealPoint create( final RealPoint obj )
	{
		return super.create( obj );
	}

	public RealPoint create()
	{
		return super.create( createRef() );
	}

	@Override
	public void delete( final RealPoint obj )
	{
		super.delete( obj );
	}

	@Override
	public int numDimensions()
	{
		return position.numDimensions();
	};

	@Override
	protected RealPoint createEmptyRef()
	{
		return new RealPoint( this );
	}
}
