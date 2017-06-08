package org.mastodon.rtree;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttributeValue;

import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;

public class Rect extends PoolObject< Rect, RectPool, ByteMappedElement > implements RealInterval, Geometry
{

	final RealPointAttributeValue rmin;

	final RealPointAttributeValue rmax;

	protected Rect( final RectPool pool )
	{
		super( pool );
		rmin = pool.min.createQuietAttributeValue( this );
		rmax = pool.max.createQuietAttributeValue( this );
	}

	@Override
	protected void setToUninitializedState()
	{}

	public Rect init( final String label, final RealInterval interval )
	{
		pool.label.set( this, label );
		for ( int d = 0; d < numDimensions(); d++ )
		{
			pool.min.setPositionQuiet( this, interval.realMin( d ), d );
			pool.max.setPositionQuiet( this, interval.realMax( d ), d );
		}
		return this;
	}

	/**
	 *
	 * @param bounds
	 *            min & max.
	 * @return
	 */
	public Rect init( final String label, final double... bounds )
	{
		assert bounds.length / 2 == numDimensions(): "Expected " + ( 2 * numDimensions() ) + " arguments, only got " + bounds.length + ".";
		pool.label.set( this, label );
		for ( int d = 0; d < numDimensions(); d++ )
		{
			pool.min.setPositionQuiet( this, bounds[ d ], d );
			pool.max.setPositionQuiet( this, bounds[ d + numDimensions() ], d );
		}
		return this;
	}

	@Override
	public int numDimensions()
	{
		return rmin.numDimensions();
	}

	@Override
	public double realMin( final int d )
	{
		return rmin.getDoublePosition( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < min.length; d++ )
			min[ d ] = rmin.getDoublePosition( d );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
			min.setPosition( rmin.getDoublePosition( d ), d );
	}

	@Override
	public double realMax( final int d )
	{
		return rmax.getDoublePosition( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < max.length; d++ )
			max[ d ] = rmax.getDoublePosition( d );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
			max.setPosition( rmax.getDoublePosition( d ), d );
	}

	@Override
	public String toString()
	{
		return pool.label.get( this ) + ": " + GeometryUtil.printInterval( this );
	}
}
