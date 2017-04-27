package org.mastodon.kdtree;

import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * Nearest-neighbor search in an Euclidean space. The interface describes
 * implementations that perform the search for a specified location and provide
 * access to the data, location and distance of the found nearest neighbor until
 * the next search is performed. In a multi-threaded application, each thread
 * will thus need its own {@link NearestNeighborSearch}.
 *
 * @author Stephan Saalfeld
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
	 * Create a copy.
	 */
	@Override
	public IncrementalNearestNeighborSearch< T > copy();
}
