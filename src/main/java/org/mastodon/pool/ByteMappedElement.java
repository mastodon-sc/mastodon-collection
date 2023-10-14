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
package org.mastodon.pool;

/**
 * A {@link MappedElement} that stores its data in a portion of a {@code byte[]}
 * array.
 *
 * <p>
 * Contract: A {@link ByteMappedElement} may be used on different
 * {@link ByteMappedElementArray}s but they all must have the same
 * bytesPerElement.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class ByteMappedElement implements MappedElement
{
	/**
	 * How many bytes are required to store one element.
	 */
	private final int bytesPerElement;

	/**
	 * The current base offset (in bytes) into the underlying
	 * {@link ByteMappedElementArray#data storage array}.
	 */
	private int baseOffset;

	/**
	 * Contains the {@link ByteMappedElementArray#data storage array}.
	 */
	private ByteMappedElementArray dataArray;

	/**
	 * Create a new proxy for representing element is in the given
	 * {@link ByteMappedElementArray}.
	 *
	 * @param dataArray
	 *            initial storage.
	 * @param index
	 *            initial element index in storage.
	 */
	public ByteMappedElement( final ByteMappedElementArray dataArray, final int index )
	{
		this.dataArray = dataArray;
		this.bytesPerElement = dataArray.bytesPerElement;
		this.baseOffset = index * bytesPerElement;
	}

	void setDataArray( final ByteMappedElementArray dataArray )
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
		ByteUtils.putByte( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public byte getByte( final int offset )
	{
		return ByteUtils.getByte( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putBytes( final byte[] bytes, final int bytesoffset, final int byteslength, final int offset )
	{
		ByteUtils.copyBytes( bytes, bytesoffset, dataArray.data, baseOffset + offset, byteslength );
	}

	@Override
	public void getBytes( final byte[] bytes, final int bytesoffset, final int byteslength, final int offset )
	{
		ByteUtils.copyBytes( dataArray.data, baseOffset + offset, bytes, bytesoffset, byteslength );
	}

	@Override
	public void putBoolean( final boolean value, final int offset )
	{
		ByteUtils.putBoolean( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public boolean getBoolean( final int offset )
	{
		return ByteUtils.getBoolean( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putShort( final short value, final int offset )
	{
		ByteUtils.putShort( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public short getShort( final int offset )
	{
		return ByteUtils.getShort( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putInt( final int value, final int offset )
	{
		ByteUtils.putInt( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public int getInt( final int offset )
	{
		return ByteUtils.getInt( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putIndex( final int value, final int offset )
	{
		ByteUtils.putIndex( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public int getIndex( final int offset )
	{
		return ByteUtils.getIndex( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putLong( final long value, final int offset )
	{
		ByteUtils.putLong( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public long getLong( final int offset )
	{
		return ByteUtils.getLong( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putFloat( final float value, final int offset )
	{
		ByteUtils.putFloat( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public float getFloat( final int offset )
	{
		return ByteUtils.getFloat( dataArray.data, baseOffset + offset );
	}

	@Override
	public void putDouble( final double value, final int offset )
	{
		ByteUtils.putDouble( value, dataArray.data, baseOffset + offset );
	}

	@Override
	public double getDouble( final int offset )
	{
		return ByteUtils.getDouble( dataArray.data, baseOffset + offset );
	}

	public void copy( final ByteMappedElement src, final int offset, final int length )
	{
		System.arraycopy( src.dataArray.data, src.baseOffset + offset, dataArray.data, baseOffset + offset, length );
	}

	/**
	 * Two {@link ByteMappedElement} are equal if they refer to the same index
	 * in the same {@link ByteMappedElementArray}.
	 */
	@Override
	public boolean equals( final Object obj )
	{
		if ( obj instanceof ByteMappedElement )
		{
			final ByteMappedElement e = ( ByteMappedElement ) obj;
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
