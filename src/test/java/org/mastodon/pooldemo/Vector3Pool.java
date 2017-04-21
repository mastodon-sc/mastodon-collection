package org.mastodon.pooldemo;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

public class Vector3Pool extends Pool< Vector3, ByteMappedElement >
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
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		position = new RealPointAttribute<>( layout.position, this );
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
