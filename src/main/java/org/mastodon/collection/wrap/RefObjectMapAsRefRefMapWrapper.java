package org.mastodon.collection.wrap;

import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;

/**
 * Wraps a {@link RefObjectMap} as a {@link RefRefMap}.
 */
public class RefObjectMapAsRefRefMapWrapper< K, L > extends AbstractRefRefMapWrapper< K, L, RefObjectMap< K, L > >
{
	public RefObjectMapAsRefRefMapWrapper( final RefObjectMap< K, L > map )
	{
		super( map );
	}

	@Override
	public RefSet< K > keySet()
	{
		return map.keySet();
	}
}
