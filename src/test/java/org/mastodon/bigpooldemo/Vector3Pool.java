package org.mastodon.bigpooldemo;

import org.mastodon.pool.BigPool;
import org.mastodon.pool.BigPoolObject;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.MultiArrayMemPool;

public class Vector3Pool extends BigPool< Vector3, ByteMappedElement >
{
	public Vector3Pool( final long initialCapacity )
	{
		this( initialCapacity, new Vector3Factory() );
	}

	@Override
	public Vector3 create( final Vector3 obj )
	{
		return super.create( obj );
	}

	public Vector3 create()
	{
		return super.create( createRef() );
	}

	public void delete( final Vector3 obj )
	{
		deleteByInternalPoolIndex( obj.getInternalPoolIndex() );
	}

	private Vector3Pool( final long initialCapacity, final Vector3Pool.Vector3Factory f )
	{
		super( initialCapacity, f );
		f.pool = this;
	}

	private static class Vector3Factory implements BigPoolObject.Factory< Vector3, ByteMappedElement >
	{
		private Vector3Pool pool;

		@Override
		public int getSizeInBytes()
		{
			return Vector3.SIZE_IN_BYTES;
		}

		@Override
		public Vector3 createEmptyRef()
		{
			return new Vector3( pool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return MultiArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< Vector3 > getRefClass()
		{
			return Vector3.class;
		}
	};
}
