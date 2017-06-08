package org.mastodon.rtree;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.RealPointAttribute;
import org.mastodon.properties.ObjPropertyMap;

import net.imglib2.EuclideanSpace;

public class RectPool extends Pool< Rect, ByteMappedElement > implements EuclideanSpace
{

	static class RectLayout extends PoolObjectLayout
	{

		final DoubleArrayField min;
		final DoubleArrayField max;

		public RectLayout( final int numDimensions )
		{
			min = doubleArrayField( numDimensions );
			max = doubleArrayField( numDimensions );
		}
	}

	final ObjPropertyMap< Rect, String > label = new ObjPropertyMap<>( this );
	final RealPointAttribute< Rect > min;
	final RealPointAttribute< Rect > max;

	public RectPool( final int numDimensions, final int initialCapacity )
	{
		this( new RectLayout( numDimensions ), initialCapacity );
	}

	public RectPool( final RectLayout layout, final int initialCapacity )
	{
		super( initialCapacity, layout, Rect.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		min = new RealPointAttribute<>( layout.min, this );
		max = new RealPointAttribute<>( layout.max, this );
	}

	@Override
	protected Rect createEmptyRef()
	{
		return new Rect( this );
	}

	@Override
	public Rect create( final Rect obj )
	{
		return super.create( obj );
	}

	@Override
	public int numDimensions()
	{
		return min.numDimensions();
	}
}
