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
package org.mastodon;

import org.mastodon.collection.util.HashBimap;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;

/**
 * A pool of objects (usually reusable {@link Ref} objects). It provides methods
 * to create object references and a bidirectional mapping between integer IDs
 * and objects.
 * <p>
 * Implementations:
 * <ul>
 * <li>A mapping between {@link PoolObject}s and their internal pool indices.
 * Implemented in {@link Pool}.</li>
 * <li>A mapping between Java objects and IDs that are assigned upon first
 * access. In this case, {@link #createRef()} and {@link #releaseRef(Object)}
 * simply return {@code null} and do nothing, respectively (see
 * {@link HashBimap}).</li>
 * </ul>
 *
 * @param <O>
 *            the object type.
 *
 * @author Tobias Pietzsch
 */
public interface RefPool< O >
{
	/**
	 * Generates an object reference.
	 *
	 * @return a new, uninitialized, reference object.
	 */
	public O createRef();

	/**
	 * Releases a previously created reference object.
	 *
	 * @param obj
	 *            the reference object to release.
	 */
	public void releaseRef( final O obj );

	/**
	 * Get the object associated with the given {@code id}.
	 * <p>
	 * If this pool stores {@link Ref} objects, the provided reference
	 * {@code obj} will be used to refer to the object at {@code index} in the
	 * pool, and returned.
	 *
	 * @param id
	 *            internal pool index.
	 * @param obj
	 *            reusable reference that may be used to refer to object
	 *            associated with {@code id}, and return it.
	 * @return the object associated with the given {@code id}.
	 */
	public O getObject( final int id, final O obj );

	/**
	 * Get the object associated with the given {@code id}.
	 * <p>
	 * If this pool stores {@link Ref} objects, the provided reference
	 * {@code obj} will be used to refer to the object at {@code index} in the
	 * pool, and returned.
	 *
	 * @param id
	 *            internal pool index.
	 * @param obj
	 *            reusable reference that may be used to refer to object
	 *            associated with {@code id}, and return it.
	 * @return the object associated with the given {@code id} or {@code null}
	 *         if no such object exists.
	 */
	public O getObjectIfExists( final int id, final O obj );

	/**
	 * Get the (unique) ID associated with the given object.
	 * <p>
	 * If the object is a {@link Ref}, then the ID depends on the data the
	 * {@link Ref} is currently pointing to. That is, {@link #getId(Object)}
	 * will return different IDs for the same {@code a}, depending on which
	 * object the {@link Ref} currently points to.
	 *
	 * @param o
	 *            the object (reference).
	 * @return the ID of the (data referred to by) {@code o}.
	 */
	public int getId( O o );

	/**
	 * Get the type of objects stored in this pool.
	 *
	 * @return the type of objects stored in this pool.
	 */
	public Class< O > getRefClass();
}
