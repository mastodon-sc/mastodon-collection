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
import org.mastodon.pool.PoolObjectLayout.DoubleField;

public class DoubleAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public DoubleAttribute( final DoubleField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final double value )
	{
		access( key ).putDouble( value, offset );
	}

	public void set( final O key, final double value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putDouble( value, offset );
		notifyPropertyChanged( key );
	}

	public double get( final O key )
	{
		return access( key ).getDouble( offset );
	}

	public DoubleAttributeValue createAttributeValue( final O key )
	{
		return new DoubleAttributeValue()
		{
			@Override
			public double get()
			{
				return DoubleAttribute.this.get( key );
			}

			@Override
			public void set( final double value )
			{
				DoubleAttribute.this.set( key, value );
			}
		};
	}

	public DoubleAttributeValue createQuietAttributeValue( final O key )
	{
		return new DoubleAttributeValue()
		{
			@Override
			public double get()
			{
				return DoubleAttribute.this.get( key );
			}

			@Override
			public void set( final double value )
			{
				DoubleAttribute.this.setQuiet( key, value );
			}
		};
	}
}
