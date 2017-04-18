package org.mastodon.kdtree;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttribute;

import net.imglib2.RealLocalizable;

class RealPoint extends PoolObject< RealPoint, RealPointPool, ByteMappedElement >
		implements RealPointAttribute.DelegateRealLocalizable, RealPointAttribute.DelegateRealPositionable
{
	private final RealPointAttribute< RealPoint >.AbstractRealPointAccess realPointAccess;

	RealPoint( final RealPointPool pool )
	{
		super( pool );
//		realPointAccess = pool.position.new QuietRealPointAccess( this );
		realPointAccess = pool.position.new RealPointAccess( this );
	}

	public RealPoint init( final double... position )
	{
		pool.position.setPositionQuiet( this, position );
		return this;
	}

	public RealPoint init( final RealLocalizable position )
	{
		pool.position.setPositionQuiet( this, position );
		return this;
	}

	@Override
	public String toString()
	{
		final int n = numDimensions();
		final StringBuilder sb = new StringBuilder();
		sb.append( "( " );
		for ( int d = 0; d < n; d++ )
		{
			sb.append( getDoublePosition( d ) );
			if ( d < n - 1 )
				sb.append( ", " );
		}
		sb.append( " )" );
		return sb.toString();
	}

	@Override
	protected void setToUninitializedState()
	{}

	@Override
	public RealPointAttribute< RealPoint >.AbstractRealPointAccess delegate()
	{
		return realPointAccess;
	}
}
