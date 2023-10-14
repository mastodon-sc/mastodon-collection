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
 * An array of {@link MappedElement MappedElements}. The array can grow, see
 * {@link #resize(int)}, which involves reallocating and copying the underlying
 * primitive array.
 *
 * @param <T>
 *            the {@link MappedElement} type stored in this array.
 * @param <A>
 *            the type of the primitive array.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface MappedElementArray< A extends MappedElementArray< A, T >, T extends MappedElement >
{
	/**
	 * Get the number of {@link MappedElement elements} in this array.
	 *
	 * @return number of {@link MappedElement elements} in this array.
	 */
	public int size();

	/**
	 * The maximum number of {@link MappedElement elements} that could be
	 * maximally contained in a {@link MappedElementArray} of this type.
	 *
	 * This depends on the size of a single element and the strategy used to
	 * store their data. For example, a {@link ByteMappedElementArray} stores
	 * data in a {@code byte[]} array, which means that at most
	 * 2GB/size(element) can be stored. If data would be mapped into a
	 * {@code long[]} it would be 8 times more, etc.
	 *
	 * @return maximum number of {@link MappedElement elements} storable in a
	 *         {@link MappedElementArray} of the same type.
	 */
	public int maxSize();

	/**
	 * Create a new proxy referring to the element at index 0.
	 *
	 * @return new access (proxy).
	 */
	public T createAccess();

	/**
	 * Updates the given {@link MappedElement} to refer the element at
	 * {@code index} in this array.
	 * 
	 * @param access
	 *            the mapped element.
	 * @param index
	 *            the element index.
	 */
	public void updateAccess( final T access, final int index );

	/**
	 * Sets the size of this array to contain {@code numElements}
	 * {@link MappedElement elements}.
	 *
	 * @param numElements
	 *            new number of {@link MappedElement elements} in this array.
	 */
	public void resize( final int numElements );

	/**
	 * Swaps the {@link MappedElement} data at {@code index} in this
	 * {@link MappedElementArray} with the element at {@code arrayIndex} in the
	 * {@link MappedElementArray} {@code array}.
	 *
	 * @param index
	 *            index of element to swap in this array.
	 * @param array
	 *            other array
	 * @param arrayIndex
	 *            index of element to swap in other array.
	 */
	public void swapElement( final int index, final A array, final int arrayIndex );

	/**
	 * A factory for {@link MappedElementArray}.
	 *
	 * @param <A>
	 *            the type of {@link MappedElementArray} created by this
	 *            factory.
	 */
	public static interface Factory< A > // A extends MappedElementArray< T >
	{
		/**
		 * Creates an array containing {@code numElements} elements of
		 * {@code bytesPerElement} bytes each.
		 * 
		 * @param numElements
		 *            the number of elements to store in the array.
		 * @param bytesPerElement
		 *            the number of bytes occupied b a single element.
		 * @return a new array.
		 */
		public A createArray( final int numElements, final int bytesPerElement );
	}
}
