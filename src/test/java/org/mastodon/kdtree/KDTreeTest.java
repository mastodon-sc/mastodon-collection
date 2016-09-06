package org.mastodon.kdtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.collection.ref.RefSetImp;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.DoubleMappedElement;
import org.mastodon.pool.SingleArrayMemPool;

import net.imglib2.RealLocalizable;

public class KDTreeTest
{
	final int numDataVertices = 10000;

	final int numInvalidDataVertices = 1000;

	final int numTestVertices = 100;

	final double minCoordinateValue = -5.0;

	final double maxCoordinateValue = 5.0;

	RealPointPool vertexPool;

	RefArrayList< RealPoint > dataVertices;

	RefArrayList< RealPoint > testVertices;

	RefSetImp< RealPoint > invalidDataVertices;

	@Before
	public void createDataVertices()
	{
		vertexPool = new RealPointPool( 3, numDataVertices + numTestVertices );
		dataVertices = new RefArrayList<>( vertexPool, numDataVertices );
		testVertices = new RefArrayList<>( vertexPool, numTestVertices );
		invalidDataVertices = new RefSetImp<>( vertexPool, numInvalidDataVertices );

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
		for ( int i = 0; i < numInvalidDataVertices; ++i )
		{
			final int j = rnd.nextInt( numDataVertices );
			dataVertices.getQuick( j, vertex );
			invalidDataVertices.add( vertex );
		}
		vertexPool.releaseRef( vertex );
	}

	@Test
	public void testCreateKDTree()
	{
		final KDTree< RealPoint, DoubleMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool );
		assertNotNull( kdtree );
		assertEquals( kdtree.size(), dataVertices.size() );
	}

	/**
	 * Find nearest neighbor by exhaustive search. For verification of KDTree results
	 *
	 * @param nearest
	 *            is returned, referencing the nearest data point to t.
	 * @param t
	 *            query
	 */
	private RealPoint findNearestNeighborExhaustive( final RealPoint nearest, final RealLocalizable t )
	{
		double minDistance = Double.MAX_VALUE;

		final int n = t.numDimensions();
		final double[] tpos = new double[ n ];
		final double[] ppos = new double[ n ];
		t.localize( tpos );

		for ( final RealPoint p : dataVertices )
		{
			p.localize( ppos );
			double dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += ( tpos[ i ] - ppos[ i ] ) * ( tpos[ i ] - ppos[ i ] );
			if ( dist < minDistance )
			{
				minDistance = dist;
				nearest.refTo( p );
			}
		}

		return nearest;
	}

	/**
	 * Find nearest valid neighbor by exhaustive search. For verification of KDTree results
	 *
	 * @param nearest
	 *            is returned, referencing the nearest data point to t.
	 * @param t
	 *            query
	 */
	private RealPoint findNearestValidNeighborExhaustive( final RealPoint nearest, final RealLocalizable t )
	{
		double minDistance = Double.MAX_VALUE;

		final int n = t.numDimensions();
		final double[] tpos = new double[ n ];
		final double[] ppos = new double[ n ];
		t.localize( tpos );

		for ( final RealPoint p : dataVertices )
		{
			if ( invalidDataVertices.contains( p ) )
				continue;

			p.localize( ppos );
			double dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += ( tpos[ i ] - ppos[ i ] ) * ( tpos[ i ] - ppos[ i ] );
			if ( dist < minDistance )
			{
				minDistance = dist;
				nearest.refTo( p );
			}
		}

		return nearest;
	}

	@Test
	public void testNearestNeighborSearch()
	{
		final KDTree< RealPoint, DoubleMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool );
		final NearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > kd = new NearestNeighborSearchOnKDTree<>( kdtree );
		final RealPoint nnExhaustive = vertexPool.createRef();
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t );
			final RealLocalizable nnKdtree = kd.getSampler().get();
			findNearestNeighborExhaustive( nnExhaustive, t );
			assertEquals( nnKdtree, nnExhaustive );
		}
		vertexPool.releaseRef( nnExhaustive );
	}

	@Test
	public void testNearestNeighborSearchBytes()
	{
		final KDTree< RealPoint, ByteMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		final NearestNeighborSearchOnKDTree< RealPoint, ByteMappedElement > kd = new NearestNeighborSearchOnKDTree<>( kdtree );
		final RealPoint nnExhaustive = vertexPool.createRef();
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t );
			final RealLocalizable nnKdtree = kd.getSampler().get();
			findNearestNeighborExhaustive( nnExhaustive, t );
			assertEquals( nnKdtree, nnExhaustive );
		}
		vertexPool.releaseRef( nnExhaustive );
	}

	@Test
	public void testNearestValidNeighborSearch()
	{
		final KDTree< RealPoint, DoubleMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool );
		final RefRefMap< RealPoint, KDTreeNode< RealPoint, DoubleMappedElement > > map = KDTree.createRefToKDTreeNodeMap( kdtree );
		for ( final RealPoint invalid : invalidDataVertices )
			map.get( invalid ).setValid( false );
		final NearestValidNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > kd = new NearestValidNeighborSearchOnKDTree<>( kdtree );
		final RealPoint nnExhaustive = vertexPool.createRef();
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t );
			final RealLocalizable nnKdtree = kd.getSampler().get();
			findNearestValidNeighborExhaustive( nnExhaustive, t );
			assertEquals( nnKdtree, nnExhaustive );
		}
		vertexPool.releaseRef( nnExhaustive );
	}

	@Test
	public void testNearestValidNeighborSearchBytes()
	{
		final KDTree< RealPoint, ByteMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		final RefRefMap< RealPoint, KDTreeNode< RealPoint, ByteMappedElement > > map = KDTree.createRefToKDTreeNodeMap( kdtree );
		for ( final RealPoint invalid : invalidDataVertices )
			map.get( invalid ).setValid( false );
		final NearestValidNeighborSearchOnKDTree< RealPoint, ByteMappedElement > kd = new NearestValidNeighborSearchOnKDTree<>( kdtree );
		final RealPoint nnExhaustive = vertexPool.createRef();
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t );
			final RealLocalizable nnKdtree = kd.getSampler().get();
			findNearestValidNeighborExhaustive( nnExhaustive, t );
			assertEquals( nnKdtree, nnExhaustive );
		}
		vertexPool.releaseRef( nnExhaustive );
	}
}
