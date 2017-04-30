package org.mastodon.kdtree;

import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;

/**
 * Incremental nearest-neighbor search in set of points. The interface describes
 * implementations that perform the search for a specified location and provide
 * iteration of points in order of increasing distance to the query location.
 * Iteration is implemented through the {@link RealCursor} interface, providing
 * access to the data, location and distance to the current nearest neighbor
 * until the iterator is forwarded or the next search is performed.
 *
 * @author Tobias Pietzsch
 */
public interface IncrementalNearestNeighborSearch< T > extends RealCursor< T >
{
	/**
	 * Perform nearest-neighbor search for a reference coordinate.
	 *
	 * @param reference
	 */
	public void search( final RealLocalizable reference );

	/**
	 * Access the square Euclidean distance between the reference location as
	 * used for the last search and the current nearest neighbor.
	 */
	public double getSquareDistance();

	/**
	 * Access the Euclidean distance between the reference location as used for
	 * the last search and the current nearest neighbor.
	 */
	public double getDistance();

	/**
	 * Create a copy.
	 */
	@Override
	public IncrementalNearestNeighborSearch< T > copy();
}
