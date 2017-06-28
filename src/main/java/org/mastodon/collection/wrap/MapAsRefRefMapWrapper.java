package org.mastodon.collection.wrap;

import java.util.Map;

import org.mastodon.collection.RefRefMap;

/**
 * Wraps a standard {@link Map} as a {@link RefRefMap}.
 */
public class MapAsRefRefMapWrapper< K, L > extends AbstractRefRefMapWrapper< K, L, Map< K, L > >
{
	public MapAsRefRefMapWrapper( final Map< K, L > map )
	{
		super( map );
	}
}
