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
package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.IntArrayField;
import org.mastodon.properties.BeforePropertyChangeListener;
import org.mastodon.properties.PropertyChangeListener;

public class IntArrayAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	private final int length;

	public IntArrayAttribute( final IntArrayField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
		this.length = layoutField.numElements();
	}

	public void setQuiet( final O key, final int index, final int value )
	{
		access( key ).putInt( value, offset + index * INT_SIZE );
	}

	public void set( final O key, final int index, final int value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putInt( value, offset + index * INT_SIZE );
		notifyPropertyChanged( key );
	}

	public int get( final O key, final int index )
	{
		return access( key ).getInt( offset + index * INT_SIZE );
	}

	public int length()
	{
		return length;
	}

	/**
	 * Notify {@link BeforePropertyChangeListener}s that the value of this
	 * property is about to change.
	 * <p>
	 * This is exposed publicly to be able to change a few elements with
	 * {@link #setQuiet(PoolObject, int, int)} without sending notification for
	 * all of them.
	 * </p>
	 */
	@Override
	public void notifyBeforePropertyChange( final O object )
	{
		super.notifyBeforePropertyChange( object );
	}

	/**
	 * Notify {@link PropertyChangeListener}s that the value of this property
	 * has changed.
	 * <p>
	 * This is exposed publicly to be able to change a few elements with
	 * {@link #setQuiet(PoolObject, int, int)} without sending notification for
	 * all of them.
	 * </p>
	 */
	@Override
	public void notifyPropertyChanged( final O object )
	{
		super.notifyPropertyChanged( object );
	}

	public IntArrayAttributeValue createAttributeValue( final O key )
	{
		return new IntArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return IntArrayAttribute.this.length();
			}

			@Override
			public int get( final int index )
			{
				return IntArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final int value )
			{
				IntArrayAttribute.this.set( key, index, value );
			}
		};
	}

	public IntArrayAttributeValue createQuietAttributeValue( final O key )
	{
		return new IntArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return IntArrayAttribute.this.length();
			}

			@Override
			public int get( final int index )
			{
				return IntArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final int value )
			{
				IntArrayAttribute.this.setQuiet( key, index, value );
			}
		};
	}
}
