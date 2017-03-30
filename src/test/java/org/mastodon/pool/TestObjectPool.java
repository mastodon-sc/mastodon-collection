package org.mastodon.pool;

public class TestObjectPool extends Pool< TestObject, ByteMappedElement >
{
	public TestObjectPool( final int initialCapacity )
	{
		this( initialCapacity, new TestObjectFactory() );
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

	private TestObjectPool( final int initialCapacity, final TestObjectPool.TestObjectFactory f )
	{
		super( initialCapacity, f );
		f.pool = this;
	}

	private static class TestObjectFactory implements PoolObject.Factory< TestObject, ByteMappedElement >
	{
		private TestObjectPool pool;

		@Override
		public int getSizeInBytes()
		{
			return TestObject.SIZE_IN_BYTES;
		}

		@Override
		public TestObject createEmptyRef()
		{
			return new TestObject( pool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestObject > getRefClass()
		{
			return TestObject.class;
		}
	};
}
