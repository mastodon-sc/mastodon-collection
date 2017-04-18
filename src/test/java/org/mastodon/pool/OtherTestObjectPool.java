package org.mastodon.pool;

import org.mastodon.pool.attributes.IntAttribute;

public class OtherTestObjectPool extends Pool< OtherTestObject, ByteMappedElement >
{
	static class OtherTestObjectLayout extends PoolObjectLayout
	{
		final IntField id = intField();
	}

	static OtherTestObjectLayout layout = new OtherTestObjectLayout();

	final IntAttribute< OtherTestObject > id;

	public OtherTestObjectPool( final int initialCapacity )
	{
		super( initialCapacity, layout, OtherTestObject.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id );
	}

	@Override
	public OtherTestObject create( final OtherTestObject obj )
	{
		return super.create( obj );
	}

	public OtherTestObject create()
	{
		return super.create( createRef() );
	}

	@Override
	public void delete( final OtherTestObject obj )
	{
		super.delete( obj );
	}

	@Override
	protected OtherTestObject createEmptyRef()
	{
		return new OtherTestObject( this );
	}
}
