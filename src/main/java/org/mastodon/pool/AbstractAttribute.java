package org.mastodon.pool;

import org.mastodon.properties.AbstractProperty;

/**
 * Makes {@link PoolObject#access} visible to subclasses.
 *
 * @author Tobias Pietzsch
 */
public class AbstractAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractProperty< O >
{
	protected MappedElement access( final O obj )
	{
		return obj.access;
	}
}
