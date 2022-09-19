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

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;

/**
 * A simple, fast and thread-safe pool, that allows garbage collection of the
 * pooled objects. The pool is meant to be used in {@link Pool} to store unused
 * references.
 * <p>
 * The idea behind this pool is to use {@link ThreadLocal} to have one pool per
 * thread. These thread local pools don't need to be thread-safe. They can
 * therefore by much simple and faster.
 * <p>
 * Additionally {@link SoftReference}s are used to allow garbage collection.
 */
class ThreadLocalSoftReferencePool<T>
{
	private final ThreadLocal<SoftReference<ArrayDeque<T>>> queues = ThreadLocal.withInitial(
			() -> new SoftReference<>( new ArrayDeque<>() )
	);

	/**
	 * Gets an object from the pool.
	 *
	 * @return the object or null, if the pool is empty.
	 */
	public T get()
	{
		ArrayDeque<T> queue = queues.get().get();
		if(queue == null)
			return null;
		return queue.pollLast();
	}

	/**
	 * Puts an object into the pool.
	 *
	 * @param element
	 */
	public void put( T element )
	{
		ArrayDeque<T> queue = queues.get().get();
		if(queue == null)
		{
			queue = new ArrayDeque<>();
			queues.set(new SoftReference<>( queue ));
		}
		queue.push( element );
	}
}
