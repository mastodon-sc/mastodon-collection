package org.mastodon.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mastodon.Options;

/**
 * These tests ensure that proper exceptions are raised when a Pool is queried
 * with invalid index.
 */
public class PoolInvalidAccessTest
{
	@Before
	public void checkDebugFlag()
	{
		// This class tests DEBUG behaviour. If DEBUG==false, ignore tests.
		Assume.assumeTrue( Options.DEBUG );
	}

	@Test( expected = NoSuchElementException.class )
	public void testGetObjectNegative()
	{
		final int nTargets = 10;
		final TestObjectPool pool = new TestObjectPool( nTargets );

		final TestObject ref = pool.createEmptyRef();
		for ( int i = 0; i < nTargets; i++ )
			pool.create( ref ).init( i );

		// Good.
		pool.getObject( 0, ref );
		// Not good.
		pool.getObject( -1, ref );
	}

	@Test( expected = NoSuchElementException.class )
	public void testGetObjectOver()
	{
		final int nTargets = 10;
		final TestObjectPool pool = new TestObjectPool( nTargets );

		final TestObject ref = pool.createEmptyRef();
		for ( int i = 0; i < nTargets; i++ )
			pool.create( ref ).init( i );

		pool.getObject( 0, ref );
		pool.getObject( nTargets + 1, ref );
	}

	@Test( expected = NoSuchElementException.class )
	public void testGetObjectDeleted()
	{
		final int nTargets = 10;
		final TestObjectPool pool = new TestObjectPool( nTargets );

		final TestObject ref = pool.createEmptyRef();
		for ( int i = 0; i < nTargets; i++ )
			pool.create( ref ).init( i );

		// Remove first 5.
		final Iterator< TestObject > it = pool.iterator( ref );
		int nit = 0;
		while ( it.hasNext() && nit++ < 5 )
			pool.delete( it.next() );

		// Should crash.
		pool.getObject( 0, ref );
	}

	@Test( expected = NoSuchElementException.class )
	public void testGetObjectInvalid()
	{
		final int nTargets = 10;
		final TestObjectPool pool = new TestObjectPool( nTargets );

		final TestObject ref = pool.createEmptyRef();
		for ( int i = 0; i < nTargets / 2; i++ )
			pool.create( ref ).init( i );

		final TestObject o = pool.create( ref ).init( 1234987239 );
		final int id = o.getInternalPoolIndex();

		for ( int i = 0; i < nTargets / 2; i++ )
			pool.create( ref ).init( i + 10 );


		final TestObject td = pool.getObject( id, ref );
		pool.delete( td );
		pool.getObject( id, ref );
	}

	@Test( expected = NoSuchElementException.class )
	public void testPoolIterator()
	{
		final int nTargets = 10;
		final TestObjectPool pool = new TestObjectPool( nTargets );

		final TestObject ref = pool.createEmptyRef();
		for ( int i = 0; i < nTargets; i++ )
			pool.create( ref ).init( i );

		assertEquals( "Pool does not have the expected size.", nTargets, pool.size() );

		// Remove them all.
		for ( final TestObject o : pool )
			pool.delete( o );

		assertEquals( "Pool should be of size 0.", 0, pool.size() );

		final Iterator< TestObject > it = pool.iterator();
		assertFalse( "Pool iterator should not have a next element.", it.hasNext() );

		// Re-add 5.
		for ( int i = 0; i < nTargets / 2; i++ )
			pool.create( ref ).init( nTargets + i );

		// Re-delete
		for ( final TestObject o : pool )
			pool.delete( o );

		// Should trigger NoSuchElementException.
		it.next();
		pool.releaseRef( ref );
	}

}
