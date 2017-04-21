package org.mastodon.pool;

import org.mastodon.pool.attributes.IntAttribute;

public class TestObjectPool extends Pool< TestObject, ByteMappedElement >
{
	static class TestObjectLayout extends PoolObjectLayout
	{
		final IntField id = intField();
	}

	static TestObjectLayout layout = new TestObjectLayout();

	final IntAttribute< TestObject > id;

	public TestObjectPool( final int initialCapacity )
	{
		super( initialCapacity, layout, TestObject.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
	}

	@Override
	public TestObject create( final TestObject obj )
	{
		return super.create( obj );
	}

	public TestObject create()
	{
		return super.create( createRef() );
	}

	@Override
	public void delete( final TestObject obj )
	{
		super.delete( obj );
	}

	@Override
	protected TestObject createEmptyRef()
	{
		return new TestObject( this );
	}
}
