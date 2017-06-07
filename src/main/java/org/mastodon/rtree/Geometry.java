package org.mastodon.rtree;

import net.imglib2.RealInterval;

public interface Geometry extends RealInterval
{
	/**
	 * Returns the distance to the specified rectangle.
	 *
	 * @param r
	 *            rectangle to measure distance to, specified by its min & max
	 *            coordinates.
	 * @return distance to the rectangle r from the geometry
	 */
//	double distance( double[] r );

//	/**
//	 * Returns the minimum bounding rectangle of this geometry, by storing it
//	 * into the specified double array (min & max coordinates).
//	 *
//	 * @param mbr
//	 *            the double array in which to store the MBR. Must be of length
//	 *            equal to 2 times the dimensionality of this geometry.
//	 */
//	void mbr( double[] mbr );
//
//	double getMBRmin( int d );
//
//	double getMBRmax( int d );

	/**
	 *
	 * @param r
	 *            rectangle to measure distance to, specified by its min & max
	 *            coordinates.
	 * @return <code>true</code> if the specified rectangle intersects with this
	 *         object.
	 */
//	boolean intersects( double[] r );
}
