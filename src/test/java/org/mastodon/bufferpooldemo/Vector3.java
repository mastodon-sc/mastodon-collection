package org.mastodon.bufferpooldemo;

import org.mastodon.collection.util.DelegateRealLocalizable;
import org.mastodon.collection.util.DelegateRealPositionable;
import org.mastodon.pool.BufferMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttributeValue;

public class Vector3 extends PoolObject< Vector3, Vector3Pool, BufferMappedElement >
	implements DelegateRealLocalizable, DelegateRealPositionable
{
	private final RealPointAttributeValue position;

	Vector3( final Vector3Pool pool )
	{
		super( pool );

		/*
		 * doesn't send property change events
		 */
//		position = pool.position.createQuietAttributeValue( this );

		/*
		 * sends property change events
		 */
		position = pool.position.createAttributeValue( this );
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
	public RealPointAttributeValue delegate()
	{
		return position;
	}
}
