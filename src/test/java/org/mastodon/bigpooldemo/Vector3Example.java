package org.mastodon.bigpooldemo;

public class Vector3Example
{
	public static void main( final String[] args )
	{
		final long size = 10l;
		final Vector3Pool pool = new Vector3Pool( size );

		final Vector3 ref = pool.createRef();
		for ( long i = 0; i < size; ++i )
			pool.create( ref ).init( i, i, i );
		pool.releaseRef( ref );

		for ( final Vector3 v : pool )
			System.out.println( v );

	}
}
