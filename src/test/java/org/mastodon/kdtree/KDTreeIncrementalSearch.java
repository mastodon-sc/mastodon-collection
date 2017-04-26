package org.mastodon.kdtree;

import java.util.Random;

import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.DoubleMappedElement;

import net.imglib2.util.Util;

public class KDTreeIncrementalSearch
{
	final int numDataVertices = 100;

	final int numTestVertices = 10;

	final double minCoordinateValue = -5.0;

	final double maxCoordinateValue = 5.0;

	RealPointPool vertexPool;

	RefArrayList< RealPoint > dataVertices;

	RefArrayList< RealPoint > testVertices;

	KDTree< RealPoint, DoubleMappedElement > kdtree;

	public void createDataVertices()
	{
		vertexPool = new RealPointPool( 3, numDataVertices + numTestVertices );
		dataVertices = new RefArrayList<>( vertexPool, numDataVertices );
		testVertices = new RefArrayList<>( vertexPool, numTestVertices );

		final RealPoint vertex = vertexPool.createRef();
		final int n = vertex.numDimensions();
		final double[] p = new double[ n ];
		final double size = ( maxCoordinateValue - minCoordinateValue );
		final Random rnd = new Random( 4379 );
		for ( int i = 0; i < numDataVertices; ++i )
		{
			for ( int d = 0; d < n; ++d )
				p[ d ] = rnd.nextDouble() * size + minCoordinateValue;
			vertexPool.create( vertex );
			vertex.setPosition( p );
			dataVertices.add( vertex );
		}
		for ( int i = 0; i < numTestVertices; ++i )
		{
			for ( int d = 0; d < n; ++d )
				p[ d ] = rnd.nextDouble() * 2 * size + minCoordinateValue - size / 2;
			vertexPool.create( vertex );
			vertex.setPosition( p );
			testVertices.add( vertex );
		}
		vertexPool.releaseRef( vertex );

		kdtree = KDTree.kdtree( dataVertices, vertexPool );
	}

	void createOrderedNeighborList( final RealPoint query )
	{
		final RefArrayList< RealPoint > sorted = new RefArrayList<>( vertexPool, numTestVertices );
		sorted.addAll( dataVertices );
		sorted.sort( ( p1, p2 ) -> {
			final double d = Util.distance( query, p1 ) - Util.distance( query, p2 );
			return d < 0 ? -1 : ( d > 0 ? 1 : 0 );
		} );

		final NearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > ns = new NearestNeighborSearchOnKDTree<>( kdtree );
		ns.search( query );
		System.out.println( "query = " + query );
		System.out.println( "nn :  " + ns.get() );

		System.out.println( ns.getDistance() );

		System.out.println();
		System.out.println();
		for ( final RealPoint p : sorted )
			System.out.println( Util.distance( query, p ) );

		System.out.println();
		System.out.println();

		final IncrementalNearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > ins = new IncrementalNearestNeighborSearchOnKDTree<>( kdtree );
		ins.search( query );
		while ( ins.hasNext() )
			System.out.println( Util.distance( query, ins.next() ) );
	}

	void run()
	{
		createDataVertices();
		createOrderedNeighborList( testVertices.get( 0 ) );


	}

	public static void main( final String[] args )
	{
		new KDTreeIncrementalSearch().run();
	}


}
