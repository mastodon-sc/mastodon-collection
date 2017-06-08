package org.mastodon.rtree;

import net.imglib2.RealInterval;

/**
 * Static utilities related to geometrical computations for R-Trees.
 *
 * @author Jean-Yves Tinevez
 *
 */
public class GeometryUtil
{

	/**
	 * Calculate the area by which the interval 1 would be enlarged if the
	 * interval 2 is added (union) to it.
	 *
	 * @param interval1
	 *            the first interval.
	 * @param interval2
	 *            the second interval.
	 * @return the enlargement.
	 */
	public static final double enlargement( final RealInterval interval1, final RealInterval interval2 )
	{
		double largeArea = 1.;
		for ( int d = 0; d < interval1.numDimensions(); d++ )
			largeArea *= ( Math.max( interval1.realMax( d ), interval2.realMax( d ) ) - Math.min( interval1.realMin( d ), interval2.realMin( d ) ) );

		return largeArea - area( interval1 );
	}

	public static final double area( final RealInterval interval )
	{
		double area = 1.;
		for ( int d = 0; d < interval.numDimensions(); d++ )
			area *= ( interval.realMax( d ) - interval.realMin( d ) );

		return area;
	}

	public static final double distance( final RealInterval interval1, final RealInterval interval2 )
	{
		if (intersects(interval1, interval2))
			return 0.;

		double d2 = 0.;
		for ( int d = 0; d < interval1.numDimensions(); d++ )
		{
			final double dx = ( interval1.realMax( d ) < interval2.realMin( d ))
					? interval2.realMin( d ) - interval1.realMax( d )
							: interval1.realMin( d ) - interval2.realMax( d );

					d2 += dx * dx;
		}
		return Math.sqrt( d2 );
	}

	public static final boolean intersects( final RealInterval interval1, final RealInterval interval2 )
	{
		for ( int d = 0; d < interval1.numDimensions(); d++ )
		{
			if ( interval1.realMax( d ) < interval2.realMin( d ) )
				return false;
			if ( interval2.realMax( d ) < interval1.realMin( d ) )
				return false;
		}
		return true;
	}

	public static final String printInterval( final RealInterval interval )
	{
		String out = "(Interval empty)";

		if ( interval == null || interval.numDimensions() == 0 )
			return out;

		out = "[" + String.format( "%.1f", interval.realMin( 0 ) );

		for ( int i = 1; i < interval.numDimensions(); i++ )
			out += ", " + String.format( "%.1f", interval.realMin( i ) );

		out += "] -> [" + String.format( "%.1f", interval.realMax( 0 ) );

		for ( int i = 1; i < interval.numDimensions(); i++ )
			out += ", " + String.format( "%.1f", interval.realMax( i ) );

		out += "], dimensions (" + String.format( "%.1f", interval.realMax( 0 ) - interval.realMin( 0 ) );

		for ( int i = 1; i < interval.numDimensions(); i++ )
			out += ", " + String.format( "%.1f", interval.realMax( i ) - interval.realMin( i ) );

		out += ")";

		return out;
	}

	private GeometryUtil()
	{}
}
