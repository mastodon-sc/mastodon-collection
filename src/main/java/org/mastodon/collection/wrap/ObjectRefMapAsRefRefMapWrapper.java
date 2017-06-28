package org.mastodon.collection.wrap;

import org.mastodon.collection.ObjectRefMap;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefRefMap;

/**
 * Wraps a {@link ObjectRefMap} as a {@link RefRefMap}.
 */
public class ObjectRefMapAsRefRefMapWrapper< K, L > extends AbstractRefRefMapWrapper< K, L, ObjectRefMap< K, L > >
{
	public ObjectRefMapAsRefRefMapWrapper( final ObjectRefMap< K, L > map )
	{
		super( map );
	}

	@Override
	public RefCollection< L > values()
	{
		return map.values();
	}

	@Override
	public L createValueRef()
	{
		return map.createValueRef();
	}

	@Override
	public void releaseValueRef( final L obj )
	{
		map.releaseValueRef( obj );
	}

	@Override
	public L put( final K key, final L value, final L ref )
	{
		return map.put( key, value, ref );
	}

	@Override
	public L removeWithRef( final Object key, final L ref )
	{
		return map.removeWithRef( key, ref );
	}

	@Override
	public L get( final Object key, final L ref )
	{
		return map.get( key, ref );
	}
}
