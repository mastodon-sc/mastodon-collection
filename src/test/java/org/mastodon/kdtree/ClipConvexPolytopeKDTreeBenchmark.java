/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.collection.RefList;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.DoubleMappedElement;

import net.imglib2.util.BenchmarkHelper;
import net.imglib2.util.LinAlgHelpers;

public class ClipConvexPolytopeKDTreeBenchmark
{
	public static void main( final String[] args )
	{
		final int w = 400;
		final int h = 400;
		final int nPoints = 100000;
		final Random rand = new Random( 123124 );

		final RealPointPool pool = new RealPointPool( 2, nPoints );
		final RealPoint pRef = pool.createRef();
		final RefList< RealPoint > points = new RefArrayList<>( pool, nPoints );
		for ( int i = 0; i < nPoints; ++i )
		{
			final long x = rand.nextInt( w );
			final long y = rand.nextInt( h );
			points.add( pool.create( pRef ).init( x, y ) );
		}

		final double[][] planes = new double[ 5 ][ 3 ]; // unit normal x, y; d

		double[] plane = planes[ 0 ];
		plane[ 0 ] = 1;
		plane[ 1 ] = 1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = 230;

		plane = planes[ 1 ];
		plane[ 0 ] = -1;
		plane[ 1 ] = 1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -30;

		plane = planes[ 2 ];
		plane[ 0 ] = 0.1;
		plane[ 1 ] = -1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -230;

		plane = planes[ 3 ];
		plane[ 0 ] = -0.5;
		plane[ 1 ] = -1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -290;

		plane = planes[ 4 ];
		plane[ 0 ] = -1;
		plane[ 1 ] = 0.1;
		LinAlgHelpers.scale( plane, 1.0 / LinAlgHelpers.length( plane ), plane );
		plane[ 2 ] = -200;

		System.out.println( "partitioning list of points:" );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int i = 0; i < 500; ++i )
				{
					final RefList< RealPoint >[] insideoutside = getInsidePoints( points, planes, pool );
					if ( insideoutside[ 0 ].size() > 1000000 )
						System.out.println( "bla" );
				}
			}
		} );

		System.out.println( "partitioning kdtree of points:" );
		final KDTree< RealPoint, DoubleMappedElement > kdtree = KDTree.kdtree( points, pool );
		final ClipConvexPolytopeKDTree< RealPoint, DoubleMappedElement > clipper = new ClipConvexPolytopeKDTree<>( kdtree );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				for ( int i = 0; i < 500; ++i )
				{
					clipper.clip( planes );
				}
			}
		} );
	}

	@SuppressWarnings( "unchecked" )
	static RefList< RealPoint >[] getInsidePoints( final RefList< RealPoint > points, final double[][] planes, final RealPointPool pool )
	{
		final int nPlanes = planes.length;
		final int n = points.get( 0 ).numDimensions();
		final RefList< RealPoint > inside = new RefArrayList<>( pool );
		final RefList< RealPoint > outside = new RefArrayList<>( pool );
		A: for ( final RealPoint p : points )
		{
			for ( int i = 0; i < nPlanes; ++i )
			{
				final double[] plane = planes[ i ];
				double dot = 0;
				for ( int d = 0; d < n; ++d )
					dot += p.getDoublePosition( d ) * plane[ d ];
				if ( dot < plane[ n ] )
				{
					outside.add( p );
					continue A;
				}
			}
			inside.add( p );
		}
		return new RefList[] { inside, outside };
	}
}
