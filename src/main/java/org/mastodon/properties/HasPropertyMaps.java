package org.mastodon.properties;

import org.mastodon.pool.Pool;


// TODO: REMOVE?
/**
 * Something that has a {@link PropertyMaps} managing property maps. This is
 * currently only implemented by {@link Pool}.
 *
 * @param <O>
 *
 * @author Tobias Pietzsch
 */
public interface HasPropertyMaps< O >
{
	public PropertyMaps< O > getPropertyMaps();
}
