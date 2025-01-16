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
package org.mastodon.pool;

import java.nio.ByteBuffer;

/**
 * A {@link MappedElement} that stores its data in a portion of a {@code byte[]}
 * array.
 *
 * <p>
 * Contract: A {@link BufferMappedElement} may be used on different
 * {@link BufferMappedElementArray}s but they all must have the same
 * bytesPerElement.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class BufferMappedElement implements MappedElement
{
	/**
	 * How many bytes are required to store one element.
	 */
	private final int bytesPerElement;

	/**
	 * The current base offset (in bytes) into the underlying
	 * {@link BufferMappedElementArray#data storage array}.
	 */
	private int baseOffset;

	/**
	 * Contains the {@link BufferMappedElementArray#data storage array}.
	 */
	private BufferMappedElementArray dataArray;

	/**
	 * Create a new proxy for representing element is in the given
	 * {@link BufferMappedElementArray}.
	 *
	 * @param dataArray
	 *            initial storage.
	 * @param index
	 *            initial element index in storage.
	 */
	public BufferMappedElement( final BufferMappedElementArray dataArray, final int index )
	{
		this.dataArray = dataArray;
		this.bytesPerElement = dataArray.bytesPerElement;
		this.baseOffset = index * bytesPerElement;
	}

	void setDataArray( final BufferMappedElementArray dataArray )
	{
		this.dataArray = dataArray;
	}

	/**
	 * Set the index of the element that this {@link MappedElement} represents.
	 * Computes the base offset in the underlying memory area as
	 * <em>baseOffset = index * bytesPerElement</em>.
	 *
	 * @param index
	 *            index of the element that this {@link MappedElement} should
	 *            point to.
	 */
	void setElementIndex( final int index )
	{
		this.baseOffset = index * bytesPerElement;
	}

	@Override
	public void putByte( final byte value, final int offset )
	{
		dataArray.data.put( baseOffset + offset, value );
	}

	@Override
	public byte getByte( final int offset )
	{
		return dataArray.data.get( baseOffset + offset );
	}

	@Override
	public void putBytes( final byte[] bytes, final int bytesoffset, final int byteslength, final int offset )
	{
		final ByteBuffer data = dataArray.data;
		final int dataoffset = baseOffset + offset;
		for ( int i = 0; i < byteslength; ++i )
			data.put( dataoffset + i, bytes[ bytesoffset + i ] );
	}

	@Override
	public void getBytes( final byte[] bytes, final int bytesoffset, final int byteslength, final int offset )
	{
		final ByteBuffer data = dataArray.data;
		final int dataoffset = baseOffset + offset;
		for ( int i = 0; i < byteslength; ++i )
			bytes[ bytesoffset + i ] = data.get( dataoffset + i );
	}

	@Override
	public void putBoolean( final boolean value, final int offset )
	{
		putByte( value ? ( byte ) 1 : ( byte ) 0, offset );
	}

	@Override
	public boolean getBoolean( final int offset )
	{
		return getByte( offset ) == ( byte ) 0 ? false : true;
	}

	@Override
	public void putShort( final short value, final int offset )
	{
		dataArray.data.putShort( baseOffset + offset, value );
	}

	@Override
	public short getShort( final int offset )
	{
		return dataArray.data.getShort( baseOffset + offset );
	}

	@Override
	public void putInt( final int value, final int offset )
	{
		dataArray.data.putInt( baseOffset + offset, value );
	}

	@Override
	public int getInt( final int offset )
	{
		return dataArray.data.getInt( baseOffset + offset );
	}

	@Override
	public void putIndex( final int value, final int offset )
	{
		dataArray.data.putInt( baseOffset + offset, value );
	}

	@Override
	public int getIndex( final int offset )
	{
		return dataArray.data.getInt( baseOffset + offset );
	}

	@Override
	public void putLong( final long value, final int offset )
	{
		dataArray.data.putLong( baseOffset + offset, value );
	}

	@Override
	public long getLong( final int offset )
	{
		return dataArray.data.getLong( baseOffset + offset );
	}

	@Override
	public void putFloat( final float value, final int offset )
	{
		dataArray.data.putFloat( baseOffset + offset, value );
	}

	@Override
	public float getFloat( final int offset )
	{
		return dataArray.data.getFloat( baseOffset + offset );
	}

	@Override
	public void putDouble( final double value, final int offset )
	{
		dataArray.data.putDouble( baseOffset + offset, value );
	}

	@Override
	public double getDouble( final int offset )
	{
		return dataArray.data.getDouble( baseOffset + offset );
	}

	/**
	 * Two {@link BufferMappedElement} are equal if they refer to the same index
	 * in the same {@link BufferMappedElementArray}.
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( obj instanceof BufferMappedElement )
		{
			final BufferMappedElement e = ( BufferMappedElement ) obj;
			return e.dataArray == dataArray && e.baseOffset == baseOffset;
		}
		else
			return false;
	}

	@Override
	public int hashCode()
	{
		return dataArray.hashCode() + 31 * baseOffset;
	}
}
