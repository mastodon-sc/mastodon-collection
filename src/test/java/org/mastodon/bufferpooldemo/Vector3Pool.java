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
package org.mastodon.bufferpooldemo;

import java.nio.FloatBuffer;

import org.mastodon.pool.BufferMappedElement;
import org.mastodon.pool.BufferMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

public class Vector3Pool extends Pool< Vector3, BufferMappedElement >
{
	static class Vector3Layout extends PoolObjectLayout
	{
		final DoubleArrayField position = doubleArrayField( 3 );
	}

	static final Vector3Layout layout = new Vector3Layout();

	final RealPointAttribute< Vector3 > position;

	public Vector3Pool( final int initialCapacity )
	{
		super(
				initialCapacity,
				layout,
				Vector3.class,
				SingleArrayMemPool.factory( BufferMappedElementArray.factory ) );
		position = new RealPointAttribute<>( layout.position, this );
	}

	public FloatBuffer getFloatBuffer()
	{
		@SuppressWarnings( "unchecked" )
		final SingleArrayMemPool< BufferMappedElementArray, ? > memPool = ( SingleArrayMemPool< BufferMappedElementArray, ? > ) getMemPool();
		final BufferMappedElementArray dataArray = memPool.getDataArray();
		return dataArray.getBuffer().asFloatBuffer();
	}

	@Override
	public Vector3 create( final Vector3 obj )
	{
		return super.create( obj );
	}

	public Vector3 create()
	{
		return super.create( createRef() );
	}

	@Override
	public void delete( final Vector3 obj )
	{
		super.delete( obj );
	}

	@Override
	protected Vector3 createEmptyRef()
	{
		return new Vector3( this );
	};
}
