package org.mastodon.pool;

public class OtherTestObjectPool extends Pool< OtherTestObject, ByteMappedElement >
{
	public OtherTestObjectPool( final int initialCapacity )
	{
		this( initialCapacity, new TestObjectFactory() );
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

	private OtherTestObjectPool( final int initialCapacity, final OtherTestObjectPool.TestObjectFactory f )
	{
		super( initialCapacity, f );
		f.pool = this;
	}

	private static class TestObjectFactory implements PoolObject.Factory< OtherTestObject, ByteMappedElement >
	{
		private OtherTestObjectPool pool;

		@Override
		public int getSizeInBytes()
		{
			return TestObject.SIZE_IN_BYTES;
		}

		@Override
		public OtherTestObject createEmptyRef()
		{
			return new OtherTestObject( pool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< OtherTestObject > getRefClass()
		{
			return OtherTestObject.class;
		}
	};
}
