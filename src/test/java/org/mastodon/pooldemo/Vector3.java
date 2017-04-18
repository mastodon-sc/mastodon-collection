package org.mastodon.pooldemo;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttribute;

public class Vector3 extends PoolObject< Vector3, Vector3Pool, ByteMappedElement >
	implements RealPointAttribute.DelegateRealLocalizable, RealPointAttribute.DelegateRealPositionable
{
	private final RealPointAttribute< Vector3 >.AbstractRealPointAccess realPointAccess;

	Vector3( final Vector3Pool pool )
	{
		super( pool );

		/*
		 * doesn't send property change events
		 */
//		realPointAccess = pool.position.new QuietRealPointAccess( this );

		/*
		 * sends property change events
		 */
		realPointAccess = pool.position.new RealPointAccess( this );
	}

	public Vector3 init( final double... pos )
	{
		pool.position.setPositionQuiet( this, pos );
		return this;
	}

	@Override
	protected void setToUninitializedState()
	{}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		char c = '(';
		for ( int i = 0; i < numDimensions(); i++ )
		{
			sb.append( c );
			sb.append( getDoublePosition( i ) );
			c = ',';
		}
		sb.append( ")" );
		return sb.toString();
	}

	@Override
	public RealPointAttribute< Vector3 >.AbstractRealPointAccess delegate()
	{
		return realPointAccess;
	}
}
