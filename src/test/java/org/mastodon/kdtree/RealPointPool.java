package org.mastodon.kdtree;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;

import net.imglib2.EuclideanSpace;

class RealPointPool extends Pool< RealPoint, ByteMappedElement > implements EuclideanSpace
{
	static class RealPointLayout extends PoolObjectLayout
	{
		final IntField magicNumberField;
		final DoubleArrayField position;

		RealPointLayout( final int numDimensions )
		{
			magicNumberField = intField();
			position = doubleArrayField( numDimensions );
		}
	}

	final RealPointAttribute< RealPoint > position;

	public RealPointPool( final int numDimensions, final int initialCapacity )
	{
		this( new RealPointLayout( numDimensions ), initialCapacity );
	}

	private RealPointPool( final RealPointLayout layout, final int initialCapacity )
	{
		super( initialCapacity, layout, RealPoint.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		position = new RealPointAttribute<>( layout.position, this );
	}

	@Override
	public RealPoint create( final RealPoint obj )
	{
		return super.create( obj );
	}

	public RealPoint create()
	{
		return super.create( createRef() );
	}

	@Override
	public void delete( final RealPoint obj )
	{
		super.delete( obj );
	}

	@Override
	public int numDimensions()
	{
		return position.numDimensions();
	};

	@Override
	protected RealPoint createEmptyRef()
	{
		return new RealPoint( this );
	}
}
