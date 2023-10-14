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
package org.mastodon.pool.attributes;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.ByteField;

public class ByteAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public ByteAttribute( final ByteField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final byte value )
	{
		access( key ).putByte( value, offset );
	}

	public void set( final O key, final byte value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putByte( value, offset );
		notifyPropertyChanged( key );
	}

	public byte get( final O key )
	{
		return access( key ).getByte( offset );
	}

	public ByteAttributeValue createAttributeValue( final O key )
	{
		return new ByteAttributeValue()
		{
			@Override
			public byte get()
			{
				return ByteAttribute.this.get( key );
			}

			@Override
			public void set( final byte value )
			{
				ByteAttribute.this.set( key, value );
			}
		};
	}

	public ByteAttributeValue createQuietAttributeValue( final O key )
	{
		return new ByteAttributeValue()
		{
			@Override
			public byte get()
			{
				return ByteAttribute.this.get( key );
			}

			@Override
			public void set( final byte value )
			{
				ByteAttribute.this.setQuiet( key, value );
			}
		};
	}
}
