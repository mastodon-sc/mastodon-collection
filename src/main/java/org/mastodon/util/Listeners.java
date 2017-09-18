package org.mastodon.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A set of listeners of type {@code T}.
 *
 * @param <T>
 *            listener type
 */
public interface Listeners< T >
{
	/**
	 * Add a listener to this set.
	 *
	 * @param listener
	 *            the listener to add.
	 * @return {@code true} if the listener was added. {@code false} if it was
	 *         already present.
	 */
	public boolean add( final T listener );

	/**
	 * Removes a listener from this set.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was successfully removed.
	 *         {@code false} if the listener was not present.
	 */
	public boolean remove( final T listener );

	public default boolean addAll( final Collection< ? extends T > listeners )
	{
		return listeners.stream().map( l -> add( l ) ).reduce( Boolean::logicalOr ).get();
	}

	public default boolean removeAll( final Collection< ? extends T > listeners )
	{
		return listeners.stream().map( l -> remove( l ) ).reduce( Boolean::logicalOr ).get();
	}

	/**
	 * Implements {@link Listeners} using an {@link ArrayList}.
	 */
	public static class List< T > implements Listeners< T >
	{
		public final ArrayList< T > list = new ArrayList<>();

		@Override
		public boolean add( final T listener )
		{
			if ( !list.contains( listener ) )
			{
				list.add( listener );
				return true;
			}
			return false;
		}

		@Override
		public boolean remove( final T listener )
		{
			return list.remove( listener );
		}
	}

	/**
	 * Extends {@link Listeners.List}, making {@code add} and {@code remove}
	 * methods synchronized.
	 */
	public static class SynchronizedList< T > extends List< T >
	{
		@Override
		public synchronized boolean add( final T listener )
		{
			return super.add( listener );
		}

		@Override
		public synchronized boolean remove( final T listener )
		{
			return super.remove( listener );
		}
	}
}
