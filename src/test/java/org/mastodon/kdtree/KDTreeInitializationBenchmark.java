package org.mastodon.kdtree;

import net.imglib2.util.StopWatch;

import org.mastodon.collection.RefList;
import org.mastodon.collection.ref.RefArrayList;

/**
 * Measure how long it takes to initialize a KDTree with 1_000_000 points
 * if the points are not randomly distributed, but lie on a circle. This
 * is a difficult case scenario for KDTree initialization.
 */
public class KDTreeInitializationBenchmark
{
	public static void main( final String... args )
	{
		final int count = 1_000_000;

		final RealPointPool vertexPool = new RealPointPool( 3, count );
		final RefList< RealPoint > positions = pointsInACircle( vertexPool, count );

		final StopWatch watch = StopWatch.createAndStart();
		for ( int i = 0; i < 10; i++ )
			KDTree.kdtree( positions, vertexPool );
		System.out.println( watch );
	}

	private static RefList< RealPoint > pointsInACircle( final RealPointPool vertexPool, final int count )
	{
		final RefList< RealPoint > positions = new RefArrayList<>( vertexPool );
		final RealPoint point = positions.createRef();
		for ( int i = 0; i < count; i++ )
		{
			final double angle = 2 * Math.PI * i / count;
			point.init( Math.sin( angle ), Math.cos( angle ), 1 );
			positions.add( point );
		}
		return positions;
	}
}
