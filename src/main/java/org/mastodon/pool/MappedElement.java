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
package org.mastodon.pool;

/**
 * Maps into region of underlying memory area (a primitive array or similar).
 * This memory is split into equal-sized regions. By
 * {@link MappedElementArray#updateAccess(MappedElement, int)}, the index of the
 * region that this {@link MappedElement} represents can be set. This translates
 * into computing a base offset in the memory area. Then values of different
 * types can be read or written at (byte) offsets relative to the current base
 * offset. For example {@code putLong( 42l, 2 )} would put write the
 * {@code long} value 42 into the bytes 2 ... 10 relative to the current base
 * offset.
 *
 * <p>
 * This is used to build imglib2-like proxy objects that map into primitive
 * arrays.
 *
 * <p>
 * Note: The method for updating the base offset
 * {@link MappedElementArray#updateAccess(MappedElement, int)} needs to be in
 * the {@link MappedElementArray}, not here. This is because data might be split
 * up into several {@link MappedElementArray MappedElementArrays}, in which case
 * the reference to the memory area must be updated in addition to the base
 * offset.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface MappedElement
{
	public void putByte( final byte value, final int offset );

	public byte getByte( final int offset );

	public void putBytes( final byte[] bytes, final int bytesoffset, final int byteslength, final int offset );

	public void getBytes( final byte[] bytes, final int bytesoffset, final int byteslength, final int offset );

	public void putBoolean( final boolean value, final int offset );

	public boolean getBoolean( final int offset );

	public void putShort( final short value, final int offset );

	public short getShort( final int offset );

	public void putInt( final int value, final int offset );

	public int getInt( final int offset );

	public void putIndex( final int value, final int offset );

	public int getIndex( final int offset );

	public void putLong( final long value, final int offset );

	public long getLong( final int offset );

	public void putFloat( final float value, final int offset );

	public float getFloat( final int offset );

	public void putDouble( final double value, final int offset );

	public double getDouble( final int offset );

	public default void putBytes( final byte[] bytes, final int offset )
	{
		putBytes( bytes, 0, bytes.length, offset );
	}

	public default void getBytes( final byte[] bytes, final int offset )
	{
		getBytes( bytes, 0, bytes.length, offset );
	}
}
