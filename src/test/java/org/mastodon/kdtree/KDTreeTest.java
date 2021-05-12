/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
import net.imglib2.util.Util;

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
	 * Create list of data vertices ordered by distance to the query point. For
	 * verification of KDTree results
	 *
	 * @param sorted
	 *            is returned and stores the ordered list of data vertices.
	 * @param query
	 *            query
	 */
	private RefArrayList< RealPoint > getOrderedNeighborList( final RefArrayList< RealPoint > sorted, final RealLocalizable query )
	{
		sorted.resetQuick();
		sorted.addAll( dataVertices );
		sorted.sort( ( p1, p2 ) -> {
			final double d = Util.distance( query, p1 ) - Util.distance( query, p2 );
			return d < 0 ? -1 : ( d > 0 ? 1 : 0 );
		} );
		return sorted;
	}

	/**
	 * Create list of valid data vertices ordered by distance to the query point. For
	 * verification of KDTree results
	 *
	 * @param sorted
	 *            is returned and stores the ordered list of valid data vertices.
	 * @param query
	 *            query
	 */
	private RefArrayList< RealPoint > getOrderedValidNeighborList( final RefArrayList< RealPoint > sorted, final RealLocalizable query )
	{
		sorted.resetQuick();
		sorted.addAll( dataVertices );
		sorted.removeAll( invalidDataVertices );
		sorted.sort( ( p1, p2 ) -> {
			final double d = Util.distance( query, p1 ) - Util.distance( query, p2 );
			return d < 0 ? -1 : ( d > 0 ? 1 : 0 );
		} );
		return sorted;
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
		double minDistance = Double.POSITIVE_INFINITY;

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
		double minDistance = Double.POSITIVE_INFINITY;

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

	@Test
	public void testIncrementalNearestNeighborSearch()
	{
		final KDTree< RealPoint, DoubleMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool );
		final RefArrayList< RealPoint > sorted = new RefArrayList<>( vertexPool, numDataVertices );
		final IncrementalNearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > ins = new IncrementalNearestNeighborSearchOnKDTree<>( kdtree );
		for ( final RealLocalizable t : testVertices )
		{
			getOrderedNeighborList( sorted, t );
			ins.search( t );
			int i = 0;
			while ( ins.hasNext() )
				assertEquals( sorted.get( i++ ), ins.next() );
			assertEquals( i, sorted.size() );
		}
	}

	@Test
	public void testIncrementalNearestValidNeighborSearch()
	{
		final KDTree< RealPoint, DoubleMappedElement > kdtree = KDTree.kdtree( dataVertices, vertexPool );
		final RefRefMap< RealPoint, KDTreeNode< RealPoint, DoubleMappedElement > > map = KDTree.createRefToKDTreeNodeMap( kdtree );
		for ( final RealPoint invalid : invalidDataVertices )
			map.get( invalid ).setValid( false );
		final RefArrayList< RealPoint > sorted = new RefArrayList<>( vertexPool, numDataVertices );
		final IncrementalNearestValidNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > ins = new IncrementalNearestValidNeighborSearchOnKDTree<>( kdtree );
		for ( final RealLocalizable t : testVertices )
		{
			getOrderedValidNeighborList( sorted, t );
			ins.search( t );
			int i = 0;
			while ( ins.hasNext() )
				assertEquals( sorted.get( i++ ), ins.next() );
			assertEquals( i, sorted.size() );
		}
	}
}
