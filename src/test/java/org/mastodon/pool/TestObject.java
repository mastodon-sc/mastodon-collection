package org.mastodon.pool;

public class TestObject extends PoolObject< TestObject, TestObjectPool, ByteMappedElement >
{
	TestObject( final TestObjectPool pool )
	{
		super( pool );
	}

	public TestObject init( final int id )
	{
		pool.id.setQuiet( this, id );
		return this;
	}

	@Override
	protected void setToUninitializedState()
	{
		pool.id.setQuiet( this, -1 );
	}

	public int getId()
	{
		return pool.id.get( this );
	}

	public void setId( final int id )
	{
		pool.id.set( this, id );
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "TestObject(" );
		sb.append( getId() );
		sb.append( ")" );
		return sb.toString();
	}
}
