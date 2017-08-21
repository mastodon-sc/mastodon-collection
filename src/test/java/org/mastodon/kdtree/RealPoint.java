package org.mastodon.kdtree;

import org.mastodon.util.DelegateRealLocalizable;
import org.mastodon.util.DelegateRealPositionable;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttributeValue;

import net.imglib2.RealLocalizable;

class RealPoint extends PoolObject< RealPoint, RealPointPool, ByteMappedElement >
		implements DelegateRealLocalizable, DelegateRealPositionable
{
	private final RealPointAttributeValue position;

	RealPoint( final RealPointPool pool )
	{
		super( pool );
		position = pool.position.createQuietAttributeValue( this );
//		position = pool.position.createAttributeValue( this );
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
	public RealPointAttributeValue delegate()
	{
		return position;
	}
}
