package org.mastodon.collection.ref;

import org.junit.Before;
import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

/**
 * Set up a TestObjectPool with objects k0 .. k4.
 * <p>
 * Create RefRefHashMap { k1 -> k2,  k2 -> k3,  k3 -> k4,  k4 -> k0 }.
 * <p>
 * This leaves objects k0 not occurring as key, and k1 not accurring as value.
 *
 *
 * @author Tobias Pietzsch
 * @author Jean-Yves Tinevez
 */
public abstract class RefRefHashMapSamePoolAbstractTest
{
	protected TestObjectPool pool;

	protected TestObject k0;

	protected TestObject k1;

	protected TestObject k2;

	protected TestObject k3;

	protected TestObject k4;

	protected RefRefHashMap< TestObject, TestObject > map;

	@Before
	public void setUp()
	{
		pool = new TestObjectPool( 10 );

		k0 = pool.create().init( 0 );
		k1 = pool.create().init( 1 );
		k2 = pool.create().init( 2 );
		k3 = pool.create().init( 3 );
		k4 = pool.create().init( 4 );

		map = new RefRefHashMap<>( pool, pool );
		map.put( k1, k2 );
		map.put( k2, k3 );
		map.put( k3, k4 );
		map.put( k4, k0 );
		/*
		 * 4 mappings. k0 not occurring as key, k1 not occurring as value.
		 */
	}
}
