/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Random;

import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.DoubleMappedElement;

import net.imglib2.RealLocalizable;
import net.imglib2.util.BenchmarkHelper;

public class KDTreeBenchmark
{
	private final int numDataVertices;

	private final int numTestVertices;

	private final double minCoordinateValue;

	private final double maxCoordinateValue;

	private final RealPointPool vertexPool;

	private final RefArrayList< RealPoint > dataVertices;

	private final RefArrayList< RealPoint > testVertices;

	public KDTreeBenchmark(final int numDataVertices, final int numTestVertices, final double minCoordinateValue, final double maxCoordinateValue)
	{
		this.numDataVertices = numDataVertices;
		this.numTestVertices = numTestVertices;
		this.minCoordinateValue = minCoordinateValue;
		this.maxCoordinateValue = maxCoordinateValue;
		vertexPool = new RealPointPool( 3, numDataVertices + numTestVertices );
		dataVertices = new RefArrayList<>( vertexPool, numDataVertices );
		testVertices = new RefArrayList<>( vertexPool, numTestVertices );
		createDataVertices();
	}

	private void createDataVertices()
	{
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
	}

	private KDTree< RealPoint, DoubleMappedElement > kdtree;

	public void createKDTree()
	{
		kdtree = KDTree.kdtree( dataVertices, vertexPool );
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

	public void nearestNeighborSearch( final int numRuns )
	{
		final NearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > kd = new NearestNeighborSearchOnKDTree<>( kdtree );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.getSampler().get();
			}
	}

	public void incrementalNearestNeighborSearch( final int numRuns )
	{
		final IncrementalNearestNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > kd = new IncrementalNearestNeighborSearchOnKDTree<>( kdtree );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.next();
			}
	}

	public void incrementalNearestValidNeighborSearch( final int numRuns )
	{
		final IncrementalNearestValidNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > kd = new IncrementalNearestValidNeighborSearchOnKDTree<>( kdtree );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.next();
			}
	}

	public void nearestValidNeighborSearch( final int numRuns )
	{
		final NearestValidNeighborSearchOnKDTree< RealPoint, DoubleMappedElement > kd = new NearestValidNeighborSearchOnKDTree<>( kdtree );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.getSampler().get();
			}
	}

	private net.imglib2.KDTree< RealPoint > kdtreeImgLib2;

	public void createKDTreeImgLib2()
	{
		kdtreeImgLib2 = new net.imglib2.KDTree<>( dataVertices, dataVertices );
	}

	public void nearestNeighborSearchImgLib2( final int numRuns )
	{
		final net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree< RealPoint > kd = new net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree<>( kdtreeImgLib2 );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.getSampler().get();
			}
	}

	public void kNearestNeighborSearchImgLib2( final int numRuns )
	{
		final net.imglib2.neighborsearch.KNearestNeighborSearchOnKDTree< RealPoint > kd = new net.imglib2.neighborsearch.KNearestNeighborSearchOnKDTree<>( kdtreeImgLib2, 10 );
		for ( int i = 0; i < numRuns; ++i )
			for ( final RealLocalizable t : testVertices )
			{
				kd.search( t );
				kd.getSampler().get();
			}
	}

	public static void main( final String[] args )
	{
		final KDTreeBenchmark b = new KDTreeBenchmark( 10000, 1000, -5, 5 );
		final boolean printIndividualTimes = true;

		System.out.println( "createKDTree()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.createKDTree();
			}
		} );

		b.markInvalid();

		System.out.println( "nearestNeighborSearch()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.nearestNeighborSearch( 10 );
			}
		} );

		System.out.println( "incrementalNearestNeighborSearch()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.incrementalNearestNeighborSearch( 10 );
			}
		} );

		System.out.println( "incrementalNearestValidNeighborSearch()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.incrementalNearestValidNeighborSearch( 10 );
			}
		} );

		System.out.println( "nearestValidNeighborSearch()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.nearestValidNeighborSearch( 10 );
			}
		} );

		System.out.println( "createKDTreeImgLib2()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.createKDTreeImgLib2();
			}
		} );

		System.out.println( "nearestNeighborSearchImgLib2()" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.nearestNeighborSearchImgLib2( 10 );
			}
		} );

		System.out.println( "kNearestNeighborSearchImgLib2() 10 neighbors" );
		BenchmarkHelper.benchmarkAndPrint( 10, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				b.kNearestNeighborSearchImgLib2( 10 );
			}
		} );
	}
}
