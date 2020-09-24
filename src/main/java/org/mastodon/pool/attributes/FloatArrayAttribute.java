/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import static org.mastodon.pool.ByteUtils.FLOAT_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.FloatArrayField;
import org.mastodon.properties.BeforePropertyChangeListener;
import org.mastodon.properties.PropertyChangeListener;

public class FloatArrayAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	private final int length;

	public FloatArrayAttribute( final FloatArrayField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
		this.length = layoutField.numElements();
	}

	public void setQuiet( final O key, final int index, final float value )
	{
		access( key ).putFloat( value, offset + index * FLOAT_SIZE );
	}

	public void set( final O key, final int index, final float value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putFloat( value, offset + index * FLOAT_SIZE );
		notifyPropertyChanged( key );
	}

	public float get( final O key, final int index )
	{
		return access( key ).getFloat( offset + index * FLOAT_SIZE );
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
	 * {@link #setQuiet(PoolObject, int, float)} without sending notification
	 * for all of them.
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
	 * {@link #setQuiet(PoolObject, int, float)} without sending notification
	 * for all of them.
	 * </p>
	 */
	@Override
	public void notifyPropertyChanged( final O object )
	{
		super.notifyPropertyChanged( object );
	}

	public FloatArrayAttributeValue createAttributeValue( final O key )
	{
		return new FloatArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return FloatArrayAttribute.this.length();
			}

			@Override
			public float get( final int index )
			{
				return FloatArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final float value )
			{
				FloatArrayAttribute.this.set( key, index, value );
			}
		};
	}

	public FloatArrayAttributeValue createQuietAttributeValue( final O key )
	{
		return new FloatArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return FloatArrayAttribute.this.length();
			}

			@Override
			public float get( final int index )
			{
				return FloatArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final float value )
			{
				FloatArrayAttribute.this.setQuiet( key, index, value );
			}
		};
	}
}
