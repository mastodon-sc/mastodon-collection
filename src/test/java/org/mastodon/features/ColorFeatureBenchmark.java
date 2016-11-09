package org.mastodon.features;

import java.awt.Color;
import java.util.Random;

import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pooldemo.Vector3;
import org.mastodon.pooldemo.Vector3Pool;

public class ColorFeatureBenchmark
{
	// 5 millions.
	private static final int nobj = 5000000;

	private static final int nrepeats = 5;

	private static final Random ran = new Random( 1l );

	public static void main( final String[] args )
	{
		/*
		 * Use Object-based color feature.
		 */

		System.out.println();
		final ObjFeature< Vector3, Color > colorFeature1 = new ObjFeature<>( "Color1" );
		System.out.println( colorFeature1 );
		testSetGetFeature( colorFeature1 );
		testSparseColorsFeature( colorFeature1 );

		/*
		 * Direct color features.
		 */

		System.out.println();
		final ColorFeature< Vector3 > colorFeature2 = new ColorFeature<>( "Color2" );
		System.out.println( colorFeature2 );
		testSetGetFeature( colorFeature2 );
		testSparseColorsFeature( colorFeature2 );

		/*
		 * Do it again.
		 */

		/*
		 * Use Object-based color feature.
		 */

		System.out.println();
		System.out.println( colorFeature1 );
		testSetGetFeature( colorFeature1 );
		testSparseColorsFeature( colorFeature1 );

		/*
		 * Direct color features.
		 */

		System.out.println();
		System.out.println( colorFeature2 );
		testSetGetFeature( colorFeature2 );
		testSparseColorsFeature( colorFeature2 );
	}

	private static final void testSetGetFeature( final Feature< ?, Vector3, FeatureValue< Color > > f )
	{

		// Create collection.
		final Vector3Pool pool = new Vector3Pool( nobj );
		final Vector3 ref = pool.createRef();
		final RefArrayList< Vector3 > vecs = new RefArrayList<>( pool );
		for ( int i = 0; i < nobj; i++ )
			vecs.add( pool.create( ref ).init( i, i, i ) );

		/*
		 * Memory. This part requires the classmexer to be on build-path and
		 * used as a java agent: -javaagent:classmexer.jar
		 */
//		final long size1 = com.javamex.classmexer.MemoryUtil.deepMemoryUsageOf( vecs.iterator().next().feature( f ) ) / 1000000;

		// setting
		final long s1 = System.currentTimeMillis();
		for ( int i = 0; i < nrepeats; i++ )
			for ( final Vector3 v : vecs )
				v.feature( f ).set( new Color( ran.nextInt( 256 ^ 3 ) ) );

		final long e1 = System.currentTimeMillis();
		System.out.println( String.format( "Setting color feature for %d objects: %.1f ms.",
				nobj, ( ( double ) e1 - s1 ) / nrepeats ) );

		// getting
		final long s2 = System.currentTimeMillis();
		for ( int i = 0; i < nrepeats; i++ )
		{
			for ( final Vector3 v : vecs )
			{
				@SuppressWarnings( "unused" )
				final Color color = v.feature( f ).get();
			}
		}
		final long e2 = System.currentTimeMillis();
		System.out.println( String.format( "Getting color feature for %d objects: %.1f ms.",
				nobj, ( ( double ) e2 - s2 ) / nrepeats ) );

		/*
		 * Memory. This part requires the classmexer to be on build-path and
		 * used as a java agent: -javaagent:classmexer.jar
		 */
//		final long size2 = com.javamex.classmexer.MemoryUtil.deepMemoryUsageOf( vecs.iterator().next().feature( f ) ) / 1000000;
//		System.out.println( String.format( "Size of direct color feature storage for " + f + ": %d MB.", size2 - size1 ) );
	}

	private static final void testSparseColorsFeature( final Feature< ?, Vector3, FeatureValue< Color > > f )
	{
		// Number of different colors.
		final int ncolors = 5;

		// Create collection.
		final Vector3Pool pool = new Vector3Pool( nobj );
		final Vector3 ref = pool.createRef();
		final RefArrayList< Vector3 > vecs = new RefArrayList<>( pool );
		for ( int i = 0; i < nobj; i++ )
			vecs.add( pool.create( ref ).init( i, i, i ) );

		/*
		 * Memory. This part requires the classmexer to be on build-path and
		 * used as a java agent: -javaagent:classmexer.jar
		 */
//		final long size1 = com.javamex.classmexer.MemoryUtil.deepMemoryUsageOf( vecs.iterator().next().feature( f ) ) / 1000000;

		// setting
		final long s1 = System.currentTimeMillis();
		for ( int i = 0; i < nrepeats; i++ )
			for ( final Vector3 v : vecs )
				v.feature( f ).set( new Color( 100 + i % ncolors ) );

		final long e1 = System.currentTimeMillis();
		System.out.println( String.format( "Setting sparse color feature for %d objects: %.1f ms.",
				nobj, ( ( double ) e1 - s1 ) / nrepeats ) );

		// getting
		final long s2 = System.currentTimeMillis();
		for ( int i = 0; i < nrepeats; i++ )
		{
			for ( final Vector3 v : vecs )
			{
				@SuppressWarnings( "unused" )
				final Color color = v.feature( f ).get();
			}
		}
		final long e2 = System.currentTimeMillis();
		System.out.println( String.format( "Getting sparse color feature for %d objects: %.1f ms.",
				nobj, ( ( double ) e2 - s2 ) / nrepeats ) );

		/*
		 * Memory. This part requires the classmexer to be on build-path and
		 * used as a java agent: -javaagent:classmexer.jar
		 */
//		final long size2 = com.javamex.classmexer.MemoryUtil.deepMemoryUsageOf( vecs.iterator().next().feature( f ) ) / 1000000;
//		System.out.println( String.format( "Size of direct color feature storage for " + f + ": %d MB.", size2 - size1 ) );
	}

	private ColorFeatureBenchmark()
	{}
}
