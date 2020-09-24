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
package org.mastodon.collection;

import java.util.NoSuchElementException;

/**
 * A stack that is a {@link RefCollection}. Provides standard {@code push()},
 * {@code pop()}, and {@code peek()} methods as well as variants that take
 * object references that can be used for retrieval. Depending on concrete
 * implementation, these object references can be cleared, ignored or re-used.
 *
 * @param <O>
 *            the type of elements maintained by this stack.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface RefStack< O > extends RefCollection< O >
{
    /**
     * Pushes an element onto the top of this stack.
     *
     * @param obj the element to push.
     */
	public void push( O obj );

    /**
     * Retrieves, but does not remove, the object at the top of this stack, or
     * returns {@code null} if this stack is empty.
     *
	 * @return the object at the top of this stack or
     *         {@code null} if this stack is empty.
     */
    public O peek();

    /**
     * Retrieves, but does not remove, the object at the top of this stack, or
     * returns {@code null} if this stack is empty.
     *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #peek()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the object at the top of this stack or
     *         {@code null} if this stack is empty. The object
	 *         actually returned might be the one specified as parameter,
	 *         depending on concrete implementation.
     */
    public O peek( final O obj );

	/**
	 * Removes and returns the object at the top of this stack.
	 *
	 * @return the object at the top of this stack.
	 * @throws NoSuchElementException
	 *             if this stack is empty.
	 */
	public O pop();

	/**
	 * Removes and returns the object at the top of this stack.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #pop()}.
	 *
	 * @param obj
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the object at the top of this stack. The object
	 *         actually returned might be the one specified as parameter,
	 *         depending on concrete implementation.
	 * @throws NoSuchElementException
	 *             if this stack is empty.
	 */
	public O pop( final O obj );

	/**
	 * Returns the 1-based position where an object is on this stack. If the
	 * object {@code obj} occurs as an item in this stack, this method
	 * returns the distance from the top of the stack of the occurrence nearest
	 * the top of the stack; the topmost item on the stack is considered to be
	 * at distance 1.
	 *
	 * @param obj
	 *            the desired object.
	 * @return the 1-based position from the top of the stack where the object
	 *         is located; the return value -1 indicates that the object is not
	 *         on the stack.
	 */
	public int search( final Object obj );
}
