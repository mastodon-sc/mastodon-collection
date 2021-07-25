/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A {@link MappedElementArray} that stores {@link BufferMappedElement
 * BufferMappedElements} in a {@code byte[]} array.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class BufferMappedElementArray implements MappedElementArray< BufferMappedElementArray, BufferMappedElement >
{
	/**
	 * The current data storage. This is changed when the array is
	 * {@link #resize(int) resized}.
	 */
	ByteBuffer data;

	/**
	 * How many bytes on element in this array accupies.
	 */
	final int bytesPerElement;

	/**
	 * How many elements are stored in this array.
	 */
	private int size;

	/**
	 * Create a new array containing {@code numElements} elements of
	 * {@code bytesPerElement} bytes each.
	 */
	private BufferMappedElementArray( final int numElements, final int bytesPerElement )
	{
		final long numBytes = ( long ) numElements * bytesPerElement;
		this.bytesPerElement = bytesPerElement;
		if ( numBytes > Integer.MAX_VALUE )
			throw new IllegalArgumentException(
					"trying to create a " + getClass().getName() + " with more than " + maxSize() + " elements of " + bytesPerElement + " bytes.");

		this.data = ByteBuffer.allocateDirect( ( int ) numBytes ).order( ByteOrder.nativeOrder() );
		this.size = numElements;
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public int maxSize()
	{
		return Integer.MAX_VALUE / bytesPerElement;
	}

	@Override
	public BufferMappedElement createAccess()
	{
		return new BufferMappedElement( this, 0 );
	}

	@Override
	public void updateAccess( final BufferMappedElement access, final int index )
	{
		access.setDataArray( this );
		access.setElementIndex( index );
	}

	/**
	 * {@inheritDoc} Moves the data using
	 * {@link System#arraycopy(Object, int, Object, int, int)}, using
	 * <code>swapTmp</code> as a temporary.
	 */
	@Override
	public void swapElement( final int index, final BufferMappedElementArray array, final int arrayIndex )
	{
		final int baseOffset = index * bytesPerElement;
		final int arrayBaseOffset = arrayIndex * bytesPerElement;

		for ( int i = 0; i < bytesPerElement; ++i )
		{
			final byte tmp = this.data.get( baseOffset + i );
			this.data.put( baseOffset + i, array.data.get( arrayBaseOffset + i ) );
			array.data.put( arrayBaseOffset + i, tmp );
		}
	}

	/**
	 * {@inheritDoc} The storage array is reallocated and the old contents
	 * copied over.
	 */
	@Override
	public void resize( final int numElements )
	{
		final long numBytes = ( long ) numElements * bytesPerElement;
		if ( numBytes > Integer.MAX_VALUE )
			throw new IllegalArgumentException(
					"trying to resize a " + getClass().getName() + " to more than " + maxSize() + " elements of " + bytesPerElement + " bytes.");
		final ByteBuffer buf = ByteBuffer.allocateDirect( ( int ) numBytes ).order( ByteOrder.nativeOrder() );
		data.rewind();
		buf.put( data );
		size = numElements;
		data = buf;
	}

	/**
	 * A factory for {@link BufferMappedElementArray}s.
	 */
	public static final MappedElementArray.Factory< BufferMappedElementArray > factory = new MappedElementArray.Factory< BufferMappedElementArray >()
	{
		@Override
		public BufferMappedElementArray createArray( final int numElements, final int bytesPerElement )
		{
			return new BufferMappedElementArray( numElements, bytesPerElement );
		}
	};

	/**
	 * Wrap an existing buffer
	 * TODO: clarify doc
	 */
	private BufferMappedElementArray( final ByteBuffer byteBuffer, final int bytesPerElement )
	{
		this.bytesPerElement = bytesPerElement;
		this.data = byteBuffer;
		this.size = byteBuffer.capacity() / bytesPerElement;
	}

	/**
	 * Creates a (one-time use) factory to wrap the specified
	 * {@code ByteBuffer}.
	 * 
	 * TODO: clarify doc
	 * 
	 * @param byteBuffer
	 *            the byte buffer.
	 * @return a new factory.
	 */
	public static final MappedElementArray.Factory< BufferMappedElementArray > wrappingFactory( final ByteBuffer byteBuffer )
	{
		return new MappedElementArray.Factory< BufferMappedElementArray >()
		{
			@Override
			public BufferMappedElementArray createArray( final int numElements, final int bytesPerElement )
			{
				assert( numElements == byteBuffer.capacity() / bytesPerElement );
				return new BufferMappedElementArray( byteBuffer, bytesPerElement );
			}
		};
	}

	/**
	 * Returns a new factory that can create memory pool relying on the
	 * specified byte buffer.
	 * 
	 * TODO: doc
	 * 
	 * TODO: where to put this???
	 * 
	 * @param <A>
	 *            the type the mapped element array in the memory pool.
	 * @param byteBuffer
	 *            the byte buffer.
	 * @return a new {@link MemPool.Factory}.
	 */
	public static < A extends MappedElementArray< A, BufferMappedElement > >
			MemPool.Factory< BufferMappedElement > wrappingMemPoolFactory( final ByteBuffer byteBuffer )
	{
		return new MemPool.Factory< BufferMappedElement >()
		{
			@Override
			public MemPool< BufferMappedElement > createPool( final int capacity, final int bytesPerElement, final MemPool.FreeElementPolicy freeElementPolicy )
			{
				final int numElements = byteBuffer.capacity() / bytesPerElement;
				final MemPool< BufferMappedElement > pool = new SingleArrayMemPool<>( wrappingFactory( byteBuffer ), numElements, bytesPerElement, freeElementPolicy );
				pool.size = numElements;
				pool.allocatedSize = numElements;
				return pool;
			}
		};
	}

	public ByteBuffer getBuffer()
	{
		data.rewind();
		return data;
	}
}
