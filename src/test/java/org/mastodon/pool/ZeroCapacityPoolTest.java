package org.mastodon.pool;

import org.junit.Test;

public class ZeroCapacityPoolTest
{
	@Test
	public void zeroCapacityPool()
	{
		final TestObjectPool pool = new TestObjectPool( 0 );
		pool.create().init( 100 );
	}

	@Test
	public void zeroCapacityMultiArrayPool()
	{
		final TestObjectPool pool = new TestObjectPool( 0, true );
		pool.create().init( 100 );
	}
}
