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
package org.mastodon.pool;

import java.util.Arrays;

/**
 * A {@link MappedElementArray} that stores {@link DoubleMappedElement
 * DoubleMappedElements} in a {@code double[]} array.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class DoubleMappedElementArray implements MappedElementArray< DoubleMappedElementArray, DoubleMappedElement >
{
	/**
	 * The current data storage. This is changed when the array is
	 * {@link #resize(int) resized}.
	 */
	double[] data;

	final private double[] swapTmp;

	/**
	 * How many bytes on element in this array accupies.
	 */
	final int bytesPerElement;

	/**
	 * How many elements are stored in this array.
	 */
	private int size;

	private long doubleSizeFromByteSize( final long byteSize )
	{
		return ( byteSize + ByteUtils.DOUBLE_SIZE - 1 ) / ByteUtils.DOUBLE_SIZE;
	}

	/**
	 * Create a new array containing {@code numElements} elements of
	 * {@code bytesPerElement} bytes each.
	 */
	private DoubleMappedElementArray( final int numElements, final int bytesPerElement )
	{
		final long numDoubles = doubleSizeFromByteSize( ( long ) numElements * bytesPerElement );
		if ( numDoubles > Integer.MAX_VALUE )
			throw new IllegalArgumentException(
					"trying to create a " + getClass().getName() + " with more than " + maxSize() + " elements of " + bytesPerElement + " bytes.");

		this.data = new double[ ( int ) numDoubles ];
		this.swapTmp = new double[ ( int ) doubleSizeFromByteSize( bytesPerElement ) ];
		this.bytesPerElement = bytesPerElement;
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
	public DoubleMappedElement createAccess()
	{
		return new DoubleMappedElement( this, 0 );
	}

	@Override
	public void updateAccess( final DoubleMappedElement access, final int index )
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
	public void swapElement( final int index, final DoubleMappedElementArray array, final int arrayIndex )
	{
		final long baseOffset = ( long ) index * bytesPerElement;
		final long arrayBaseOffset = ( long ) arrayIndex * bytesPerElement;
		DoubleUtils.copyBytes( data, baseOffset, swapTmp, 0, bytesPerElement );
		DoubleUtils.copyBytes( array.data, arrayBaseOffset, data, baseOffset, bytesPerElement );
		DoubleUtils.copyBytes( swapTmp, 0, array.data, arrayBaseOffset, bytesPerElement );
	}

	/**
	 * {@inheritDoc} The storage array is reallocated and the old contents
	 * copied over.
	 */
	@Override
	public void resize( final int numElements )
	{
		final long numDoubles = doubleSizeFromByteSize( ( long ) numElements * bytesPerElement );
		if ( numDoubles > Integer.MAX_VALUE )
			throw new IllegalArgumentException(
					"trying to resize a " + getClass().getName() + " to more than " + maxSize() + " elements of " + bytesPerElement + " bytes.");
		data = Arrays.copyOf( data, ( int ) numDoubles );
		size = numElements;
	}

	/**
	 * <b>For internal use only!</b>
	 *
	 * @return the data array used in this class.
	 */
	public double[] getCurrentDataArray()
	{
		return data;
	}

	/**
	 * A factory for {@link DoubleMappedElementArray}s.
	 */
	public static final MappedElementArray.Factory< DoubleMappedElementArray > factory = new MappedElementArray.Factory< DoubleMappedElementArray >()
	{
		@Override
		public DoubleMappedElementArray createArray( final int numElements, final int bytesPerElement )
		{
			return new DoubleMappedElementArray( numElements, bytesPerElement );
		}
	};
}
