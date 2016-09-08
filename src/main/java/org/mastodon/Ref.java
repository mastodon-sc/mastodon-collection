package org.mastodon;

import org.mastodon.pool.PoolObject;

// TODO revise javadoc
public interface Ref< O extends Ref< O > >
{
	/**
	 * Gets the element index that this {@link PoolObject} currently refers to.
	 *
	 * @return the element index that this {@link PoolObject} currently refers
	 *         to.
	 */
	public int getInternalPoolIndex();

	/**
	 * Makes this {@link PoolObject} refer to the same data as the specified
	 * {@code obj}.
	 *
	 * @param obj
	 *            A {@link PoolObject}, usually of the same type as this one.
	 * @return this object.
	 */
	public O refTo( final O obj );
}
