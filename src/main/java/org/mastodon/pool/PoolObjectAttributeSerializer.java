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
package org.mastodon.pool;

import org.mastodon.io.AttributeSerializer;

/**
 * Provides serialization of (parts of) a {@link PoolObject} of type {@code O}
 * to a byte array.
 * <p>
 * This can be used for undo/redo and raw i/o.
 * </p>
 *
 * @param <O>
 *            type of object.
 *
 * @author Tobias Pietzsch
 */
public class PoolObjectAttributeSerializer< O extends PoolObject< O, ?, ? > > implements AttributeSerializer< O >
{
	private final int offset;

	private final int length;

	public PoolObjectAttributeSerializer(
			final int offset,
			final int length )
	{
		this.offset = offset;
		this.length = length;
	}

	@Override
	public int getNumBytes()
	{
		return length;
	}

	@Override
	public void getBytes( final O obj, final byte[] bytes )
	{
		obj.access.getBytes( bytes, 0, length, offset );
	}

	@Override
	public void setBytes( final O obj, final byte[] bytes )
	{
		obj.access.putBytes( bytes, 0, length, offset );
	}

	@Override
	public void notifySet( final O obj )
	{}
}
