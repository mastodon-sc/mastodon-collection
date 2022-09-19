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
