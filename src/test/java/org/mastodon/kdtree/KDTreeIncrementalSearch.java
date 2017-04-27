package org.mastodon.kdtree;

import java.util.Random;

import org.mastodon.collection.RefRefMap;
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

	RefRefMap< RealPoint, KDTreeNode< RealPoint, DoubleMappedElement > > vertexToNodeMap;

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
		vertexToNodeMap = KDTree.createRefToKDTreeNodeMap( kdtree );
	}

	public void markInvalid()
	{
		final int numInvalidDataVertices = numDataVertices / 2;
		final Random rnd = new Random( 124 );
		final KDTreeNode< RealPoint, DoubleMappedElement > node = kdtree.createRef();
		for ( int i = 0; i < numInvalidDataVertices; ++i )
		{
			final int j = rnd.nextInt( kdtree.size() );
			kdtree.getObject( j, node );
			node.setValid( false );
		}
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

//		System.out.println();
//		System.out.println();
//		for ( final RealPoint p : sorted )
//			System.out.println( Util.distance( query, p ) );

		System.out.println();
		System.out.println();

		final IncrementalNearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > ins = new IncrementalNearestNeighborSearchOnKDTree<>( kdtree );
		ins.search( query );
		int i = 0;
		while ( ins.hasNext() )
		{
			System.out.println( Util.distance( query, ins.next() ) );
			if ( ! sorted.get( i ).equals( ins.get() ) )
				System.err.println( "mismatch" );
			++i;
		}

//		int i = 0;
//		while ( ins.hasNext() && i < 10 )
//		{
//			++i;
//			System.out.println( Util.distance( query, ins.next() ) );
//		}
//		System.out.println();
//
//		final RealCursor< RealPoint > inscopy = ins.copy();
//		while ( ins.hasNext() )
//			System.out.println( Util.distance( query, ins.next() ) );
//		inscopy.reset();
//		while ( inscopy.hasNext() )
//			System.out.println( " === " + Util.distance( query, inscopy.next() ) );
	}

	void createValidOrderedNeighborList( final RealPoint query )
	{
		final RefArrayList< RealPoint > sorted = new RefArrayList<>( vertexPool, numTestVertices );
		final KDTreeNode< RealPoint, DoubleMappedElement > noderef = kdtree.createRef();
		for ( final RealPoint v : dataVertices )
			if ( vertexToNodeMap.get( v, noderef ).isValid() )
				sorted.add( v );
		kdtree.releaseRef( noderef );
		sorted.sort( ( p1, p2 ) -> {
			final double d = Util.distance( query, p1 ) - Util.distance( query, p2 );
			return d < 0 ? -1 : ( d > 0 ? 1 : 0 );
		} );

//		System.out.println();
//		System.out.println();
//		for ( final RealPoint p : sorted )
//			System.out.println( Util.distance( query, p ) );

		System.out.println();
		System.out.println();

		final IncrementalNearestValidNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > ins = new IncrementalNearestValidNeighborSearchOnKDTree<>( kdtree );
		ins.search( query );
		int i = 0;
		while ( ins.hasNext() )
		{
			System.out.println( Util.distance( query, ins.next() ) );
			if ( ! sorted.get( i ).equals( ins.get() ) )
				System.err.println( "mismatch" );
			++i;
		}

	}

	void run()
	{
		createDataVertices();
//		createOrderedNeighborList( testVertices.get( 0 ) );

		markInvalid();
		createValidOrderedNeighborList( testVertices.get( 0 ) );

	}

	public static void main( final String[] args )
	{
		new KDTreeIncrementalSearch().run();
	}


}
