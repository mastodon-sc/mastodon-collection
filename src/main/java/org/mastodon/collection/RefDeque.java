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
package org.mastodon.collection;

import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * A {@link Deque} that is a {@link RefCollection}. It provides variants of
 * {@link Deque} methods that take object references that can be used for
 * retrieval. Depending on concrete implementation, these object references can
 * be cleared, ignored or re-used.
 *
 * @param <O>
 *            the type of elements maintained by this deque.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface RefDeque< O > extends RefCollection< O >, Deque< O >
{
    /**
	 * Retrieves and removes the first element of this deque. This method
	 * differs from {@link #pollFirst(Object) pollFirst} only in that it throws
	 * an exception if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #removeFirst()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the head of this deque. The object actually returned might be the
	 *         one specified as parameter, depending on concrete implementation.
	 * @throws NoSuchElementException
	 *             if this deque is empty
	 */
    public O removeFirst( final O obj );

    /**
	 * Retrieves and removes the last element of this deque. This method differs
	 * from {@link #pollLast(Object) pollLast} only in that it throws an
	 * exception if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #removeLast()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the tail of this deque. The object actually returned might be the
	 *         one specified as parameter, depending on concrete implementation.
	 * @throws NoSuchElementException
	 *             if this deque is empty
	 */
	public O removeLast( final O obj );

    /**
	 * Retrieves and removes the first element of this deque, or returns
	 * <tt>null</tt> if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #pollFirst()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the head of this deque, or <tt>null</tt> if this deque is empty.
	 *         The object actually returned might be the one specified as
	 *         parameter, depending on concrete implementation.
	 */
	public O pollFirst( final O obj );

    /**
	 * Retrieves and removes the last element of this deque, or returns
	 * <tt>null</tt> if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #pollLast()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the tail of this deque, or <tt>null</tt> if this deque is empty.
	 *         The object actually returned might be the one specified as
	 *         parameter, depending on concrete implementation.
	 */
	public O pollLast( final O obj );

	/**
	 * Retrieves, but does not remove, the first element of this deque. This
	 * method differs from {@link #peekFirst(Object) peekFirst} only in that it
	 * throws an exception if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #getFirst()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the head of this deque. The object actually returned might be the
	 *         one specified as parameter, depending on concrete implementation.
	 * @throws NoSuchElementException
	 *             if this deque is empty
	 */
	public O getFirst( final O obj );

    /**
	 * Retrieves, but does not remove, the last element of this deque. This
	 * method differs from {@link #peekLast peekLast} only in that it throws an
	 * exception if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #getLast()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the tail of this deque. The object actually returned might be the
	 *         one specified as parameter, depending on concrete implementation.
	 * @throws NoSuchElementException
	 *             if this deque is empty
	 */
	public O getLast( final O obj );

    /**
	 * Retrieves, but does not remove, the first element of this deque, or
	 * returns <tt>null</tt> if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #peekFirst()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the head of this deque, or <tt>null</tt> if this deque is empty.
	 *         The object actually returned might be the one specified as
	 *         parameter, depending on concrete implementation.
	 */
	public O peekFirst( final O obj );

    /**
	 * Retrieves, but does not remove, the last element of this deque, or
	 * returns <tt>null</tt> if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #peekLast()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the tail of this deque, or <tt>null</tt> if this deque is empty.
	 *         The object actually returned might be the one specified as
	 *         parameter, depending on concrete implementation.
	 */
	public O peekLast( final O obj );

	/**
	 * Retrieves and removes the head of the queue represented by this deque (in
	 * other words, the first element of this deque), or returns <tt>null</tt>
	 * if this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of {@link #poll()}.
	 * It is equivalent to {@link #pollFirst(Object)}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the first element of this deque, or <tt>null</tt> if this deque
	 *         is empty. The object actually returned might be the one specified
	 *         as parameter, depending on concrete implementation.
	 */
	public O poll( final O obj );

    /**
	 * Retrieves, but does not remove, the head of the queue represented by this
	 * deque (in other words, the first element of this deque). This method
	 * differs from {@link #peek peek} only in that it throws an exception if
	 * this deque is empty.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #element()}. It is equivalent to {@link #getFirst(Object)}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the head of the queue represented by this deque. The object
	 *         actually returned might be the one specified as parameter,
	 *         depending on concrete implementation.
	 * @throws NoSuchElementException
	 *             if this deque is empty
	 */
    public O element( final O obj );

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque (in other words, the first element of this deque), or
     * returns <tt>null</tt> if this deque is empty.
     *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #peek()}. It is equivalent to {@link #peekFirst(Object)}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
     * @return the head of the queue represented by this deque, or
     *         <tt>null</tt> if this deque is empty. The object
	 *         actually returned might be the one specified as parameter,
	 *         depending on concrete implementation.
     */
    public O peek( final O obj );

    /**
     * Pops an element from the stack represented by this deque.  In other
     * words, removes and returns the first element of this deque.
     *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #pop()}. It is equivalent to {@link #removeFirst(Object)}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
     * @return the element at the front of this deque (which is the top
     *         of the stack represented by this deque). The object
	 *         actually returned might be the one specified as parameter,
	 *         depending on concrete implementation.
     * @throws NoSuchElementException if this deque is empty
     */
    public O pop( final O obj );
}
