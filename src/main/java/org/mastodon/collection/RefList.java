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
package org.mastodon.collection;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * A {@link List} that is a {@link RefCollection}. It provides variants of
 * {@link List} methods that take object references that can be used for
 * retrieval. Depending on concrete implementation, these object references can
 * be cleared, ignored or re-used.
 *
 * @param <O>
 *            the type of elements maintained by this list.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface RefList< O > extends RefCollection< O >, List< O >
{
	/**
	 * Returns the element at the specified position in this list.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #get(int)}.
	 *
	 * @param index
	 *            index of the element to return.
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the element at the specified index. The object actually returned
	 *         might be the one specified as parameter, depending on concrete
	 *         implementation.
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e.,
	 *             <tt>index &lt; 0 || index &gt;= size()</tt>.
	 */
	public O get( final int index, final O obj );

	/**
	 * Removes the element at the specified position in this list (optional
	 * operation). Shifts any subsequent elements to the left (subtracts one
	 * from their indices). Returns the element that was removed from the list.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #remove(Object)}.
	 *
	 * @param index
	 *            the index of the element to be removed.
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the element previously at the specified position. The object
	 *         actually returned might be the one specified as parameter,
	 *         depending on concrete implementation.
	 * @throws UnsupportedOperationException
	 *             if the remove operation is not supported by this list.
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e.,
	 *             <tt>index &lt; 0 || index &gt;= size()</tt>.
	 */
	public O remove( final int index, final O obj );

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element (optional operation).
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #set(int, Object)}.
	 *
	 * @param index
	 *            index of the element to replace.
	 * @param obj
	 *            element to be stored at the specified position.
	 * @param replacedObj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the element previously at the specified position. The object
	 *         actually returned might be the one specified as parameter
	 *         {@code replacedObj}, depending on concrete implementation.
	 * @throws UnsupportedOperationException
	 *             if the set operation is not supported by this list.
	 * @throws ClassCastException
	 *             if the class of the specified element prevents it from being
	 *             added to this list.
	 * @throws NullPointerException
	 *             if the specified element is null and this list does not
	 *             permit null elements.
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this list.
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e.,
	 *             <tt>index &lt; 0 || index &gt;= size()</tt>.
	 */
	public O set( final int index, final O obj, final O replacedObj );

	/**
	 * Shuffle the elements of the list using the specified random number
	 * generator.
	 *
	 * @param rand
	 *            a random number generator.
	 */
	public void shuffle( Random rand );

	/**
	 * Sort the values in the list, in ascending order according to the
	 * specified {@link Comparator}.
	 *
	 * @param comparator
	 *            the comparator to use for ordering.
	 */
	@Override
	public void sort( Comparator< ? super O > comparator );

	/**
	 * Swaps the elements at the specified positions in this list.
	 *
	 * @param i
	 *            the index of one element to be swapped.
	 * @param j
	 *            the index of the other element to be swapped.
	 */
	public void swap( int i, int j );
}
