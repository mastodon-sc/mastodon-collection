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

import static org.mastodon.pool.ByteUtils.BOOLEAN_SIZE;
import static org.mastodon.pool.ByteUtils.BYTE_SIZE;
import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;
import static org.mastodon.pool.ByteUtils.FLOAT_SIZE;
import static org.mastodon.pool.ByteUtils.INDEX_SIZE;
import static org.mastodon.pool.ByteUtils.INT_SIZE;
import static org.mastodon.pool.ByteUtils.LONG_SIZE;
import static org.mastodon.pool.ByteUtils.SHORT_SIZE;

public abstract class PoolObjectLayout
{
	protected static class CurrentSizeInBytes
	{
		int size = 0;
	}

	protected final CurrentSizeInBytes currentSizeInBytes = new CurrentSizeInBytes();

	/**
	 * Called by the pool on the (completed) layout to determine the object size
	 * for the {@link MemPool}.
	 *
	 * @return the size in bytes of a {@link PoolObject} specified by this
	 *         layout.
	 */
	public int getSizeInBytes()
	{
		return currentSizeInBytes.size;
	}

	public static class PrimitiveField
	{
		private final int offset;

		private final int sizeInBytes;

		protected PrimitiveField( final CurrentSizeInBytes sib, final int elementSizeInBytes )
		{
			offset = sib.size;
			sizeInBytes = elementSizeInBytes;
			sib.size += elementSizeInBytes;
		}

		public int getOffset()
		{
			return offset;
		}

		public int getSizeInBytes()
		{
			return sizeInBytes;
		}
	}

	public static class PrimitiveArrayField extends PrimitiveField
	{
		private final int numElements;

		protected PrimitiveArrayField( final CurrentSizeInBytes sib, final int numElements, final int elementSizeInBytes )
		{
			super( sib, numElements * elementSizeInBytes );
			this.numElements = numElements;
		}

		public int numElements()
		{
			return numElements;
		}
	}

	/*
	 * BYTE
	 */

	public static class ByteField extends PrimitiveField
	{
		ByteField( final CurrentSizeInBytes sib )
		{
			super( sib, BYTE_SIZE );
		}
	}

	/**
	 * Append an {@link ByteField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link ByteField} specification
	 */
	protected ByteField byteField()
	{
		return new ByteField( currentSizeInBytes );
	}

	public static class ByteArrayField extends PrimitiveArrayField
	{
		ByteArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, BYTE_SIZE );
		}
	}

	/**
	 * Append an {@link ByteArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code byte[]} array
	 * @return the {@link ByteArrayField} specification
	 */
	protected ByteArrayField byteArrayField( final int numElements )
	{
		return new ByteArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * BOOLEAN
	 */

	public static class BooleanField extends PrimitiveField
	{
		BooleanField( final CurrentSizeInBytes sib )
		{
			super( sib, BOOLEAN_SIZE );
		}
	}

	/**
	 * Append an {@link BooleanField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link BooleanField} specification
	 */
	protected BooleanField booleanField()
	{
		return new BooleanField( currentSizeInBytes );
	}

	public static class BooleanArrayField extends PrimitiveArrayField
	{
		BooleanArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, BOOLEAN_SIZE );
		}
	}

	/**
	 * Append an {@link BooleanArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code boolean[]} array
	 * @return the {@link BooleanArrayField} specification
	 */
	protected BooleanArrayField booleanArrayField( final int numElements )
	{
		return new BooleanArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * INT
	 */

	public static class IntField extends PrimitiveField
	{
		IntField( final CurrentSizeInBytes sib )
		{
			super( sib, INT_SIZE );
		}
	}

	/**
	 * Append an {@link IntField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link IntField} specification
	 */
	protected IntField intField()
	{
		return new IntField( currentSizeInBytes );
	}

	public static class IntArrayField extends PrimitiveArrayField
	{
		IntArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, INT_SIZE );
		}
	}

	/**
	 * Append an {@link IntArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code int[]} array
	 * @return the {@link IntArrayField} specification
	 */
	protected IntArrayField intArrayField( final int numElements )
	{
		return new IntArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * SHORT
	 */

	public static class ShortField extends PrimitiveField
	{
		ShortField( final CurrentSizeInBytes sib )
		{
			super( sib, SHORT_SIZE );
		}
	}

	/**
	 * Append an {@link ShortField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link ShortField} specification
	 */
	protected ShortField shortField()
	{
		return new ShortField( currentSizeInBytes );
	}

	public static class ShortArrayField extends PrimitiveArrayField
	{
		ShortArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, SHORT_SIZE );
		}
	}

	/**
	 * Append an {@link ShortArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code short[]} array
	 * @return the {@link ShortArrayField} specification
	 */
	protected ShortArrayField shortArrayField( final int numElements )
	{
		return new ShortArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * LONG
	 */

	public static class LongField extends PrimitiveField
	{
		LongField( final CurrentSizeInBytes sib )
		{
			super( sib, LONG_SIZE );
		}
	}

	/**
	 * Append an {@link LongField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link LongField} specification
	 */
	protected LongField longField()
	{
		return new LongField( currentSizeInBytes );
	}

	public static class LongArrayField extends PrimitiveArrayField
	{
		LongArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, LONG_SIZE );
		}
	}

	/**
	 * Append an {@link LongArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code long[]} array
	 * @return the {@link LongArrayField} specification
	 */
	protected LongArrayField longArrayField( final int numElements )
	{
		return new LongArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * FLOAT
	 */

	public static class FloatField extends PrimitiveField
	{
		FloatField( final CurrentSizeInBytes sib )
		{
			super( sib, FLOAT_SIZE );
		}
	}

	/**
	 * Append an {@link FloatField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link FloatField} specification
	 */
	protected FloatField floatField()
	{
		return new FloatField( currentSizeInBytes );
	}

	public static class FloatArrayField extends PrimitiveArrayField
	{
		FloatArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, FLOAT_SIZE );
		}
	}

	/**
	 * Append an {@link FloatArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code float[]} array
	 * @return the {@link FloatArrayField} specification
	 */
	protected FloatArrayField floatArrayField( final int numElements )
	{
		return new FloatArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * DOUBLE
	 */

	public static class DoubleField extends PrimitiveField
	{
		DoubleField( final CurrentSizeInBytes sib )
		{
			super( sib, DOUBLE_SIZE );
		}
	}

	/**
	 * Append an {@link DoubleField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link DoubleField} specification
	 */
	protected DoubleField doubleField()
	{
		return new DoubleField( currentSizeInBytes );
	}

	public static class DoubleArrayField extends PrimitiveArrayField
	{
		DoubleArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, DOUBLE_SIZE );
		}
	}

	/**
	 * Append an {@link DoubleArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code double[]} array
	 * @return the {@link DoubleArrayField} specification
	 */
	protected DoubleArrayField doubleArrayField( final int numElements )
	{
		return new DoubleArrayField( currentSizeInBytes, numElements );
	}

	/*
	 * INDEX
	 */

	public static class IndexField extends PrimitiveField
	{
		IndexField( final CurrentSizeInBytes sib )
		{
			super( sib, INDEX_SIZE );
		}
	}

	/**
	 * Append an {@link IndexField} to this {@link PoolObjectLayout}.
	 *
	 * @return the {@link IndexField} specification
	 */
	protected IndexField indexField()
	{
		return new IndexField( currentSizeInBytes );
	}

	public static class IndexArrayField extends PrimitiveArrayField
	{
		IndexArrayField( final CurrentSizeInBytes sib, final int numElements )
		{
			super( sib, numElements, INDEX_SIZE );
		}
	}

	/**
	 * Append an {@link IndexArrayField} with the specified number of elements to
	 * this {@link PoolObjectLayout}.
	 *
	 * @param numElements
	 *            the number of elements in the {@code index[]} array
	 * @return the {@link IndexArrayField} specification
	 */
	protected IndexArrayField indexArrayField( final int numElements )
	{
		return new IndexArrayField( currentSizeInBytes, numElements );
	}
}
