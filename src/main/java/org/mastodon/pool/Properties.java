package org.mastodon.pool;

import java.util.ArrayList;

import org.mastodon.properties.Property;
import org.mastodon.properties.PropertyMap;

/**
 * Maintains a list of {@link Property}s for a {@link Pool}. The purpose is to
 * broadcast {@link #pauseListeners()} and {@link #resumeListeners()} events to
 * all {@link Property}s.
 * <p>
 * This is used both for the {@link AbstractAttribute}s of the pool (which are
 * registered automatically), and for {@link PropertyMap}s that are tied to the
 * pool (e.g. as member variables).
 * </p>
 * <p>
 * Temporary {@link PropertyMap}s (that are created for example by algorithms to
 * hold temporary information about graph vertices) should not be registered
 * here.
 * </p>
 *
 * @param <O>
 *            object type (key type for all {@link Property}s)
 *
 * @author Tobias Pietzsch
 */
public class Properties< O >
{
	private final ArrayList< Property< O > > properties = new ArrayList<>();

	void add( final Property< O > property )
	{
		properties.add( property );
	}

	/**
	 * Forward to {@link Property#pauseListeners()} of all registered
	 * properties.
	 */
	public void pauseListeners()
	{
		properties.forEach( a -> a.pauseListeners() );
	}

	/**
	 * Forward to {@link Property#resumeListeners()} of all registered
	 * properties.
	 */
	public void resumeListeners()
	{
		properties.forEach( a -> a.resumeListeners() );
	}
}