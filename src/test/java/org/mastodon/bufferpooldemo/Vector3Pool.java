package org.mastodon.bufferpooldemo;

import java.nio.FloatBuffer;

import org.mastodon.pool.BufferMappedElement;
import org.mastodon.pool.BufferMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

public class Vector3Pool extends Pool< Vector3, BufferMappedElement >
{
	static class Vector3Layout extends PoolObjectLayout
	{
		final DoubleArrayField position = doubleArrayField( 3 );
	}

	static final Vector3Layout layout = new Vector3Layout();

	final RealPointAttribute< Vector3 > position;

	public Vector3Pool( final int initialCapacity )
	{
		super(
				initialCapacity,
				layout,
				Vector3.class,
				SingleArrayMemPool.factory( BufferMappedElementArray.factory ) );
		position = new RealPointAttribute<>( layout.position, this );
	}

	public FloatBuffer getFloatBuffer()
	{
		final SingleArrayMemPool< BufferMappedElementArray, ? > memPool = ( SingleArrayMemPool< BufferMappedElementArray, ? > ) getMemPool();
		final BufferMappedElementArray dataArray = memPool.getDataArray();
		return dataArray.getBuffer().asFloatBuffer();
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

	@Override
	public void delete( final Vector3 obj )
	{
		super.delete( obj );
	}

	@Override
	protected Vector3 createEmptyRef()
	{
		return new Vector3( this );
	};
}
