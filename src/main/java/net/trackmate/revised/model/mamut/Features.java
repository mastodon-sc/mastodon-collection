package net.trackmate.revised.model.mamut;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.trackmate.io.FeatureSerializers;
import net.trackmate.io.RawFeatureIO;
import net.trackmate.revised.model.IntVertexFeature;
import net.trackmate.revised.model.ObjVertexFeature;

public class Features
{
	static final int NO_ENTRY_VALUE = -1;


//	public static RawFeatureIO.FeatureSerializers< Spot, Link > featureSerializers = new RawFeatureIO.FeatureSerializers<>();
//	featureIoRegistry.vertexMapSerializers.put( Features.LABEL, new ObjVertexFeatureSerializer< Spot, String >() );

	public static final ObjVertexFeature< Spot, String > LABEL = new ObjVertexFeature<>( "label" );
	public static final IntVertexFeature< Spot > TRACKLENGTH = new IntVertexFeature<>( "track length", NO_ENTRY_VALUE );


	static {
		FeatureSerializers.put( LABEL, new RawFeatureIO.ObjVertexFeatureSerializer<>() );
		FeatureSerializers.put( TRACKLENGTH, new RawFeatureIO.IntVertexFeatureSerializer<>() );
	}

	private Features() {};

	public static void main( final String[] args )
	{
		final Model model = new Model();
		final Spot ref = model.getGraph().vertexRef();

		final Random random = new Random();
		final double[] pos = new double[ 3 ];
		final double[][] cov = new double[ 3 ][ 3 ];

		for ( int i = 0; i < 100000; ++i )
		{
			for ( int d = 0; d < 3; ++d )
				pos[ d ] = random.nextDouble();
			final Spot spot = model.addSpot( 0, pos, cov, ref );

			spot.feature( LABEL ).set( "the vertex label " + i );
			spot.feature( TRACKLENGTH ).set( 3 );
		}
//		System.out.println( "label = " + spot.feature( LABEL ).get() );
//		System.out.println( "tracklength (as Integer) = " + spot.feature( TRACKLENGTH ).get() );
//		System.out.println( "tracklength (as int) = " + spot.feature( TRACKLENGTH ).getInt() );

		model.getGraph().releaseRef( ref );

		while( true )
		{
		try
		{
			final File file = new File( "/Users/pietzsch/Desktop/model_with_features.raw" );

			long t0 = System.currentTimeMillis();
			model.saveRaw( file );
			long t1 = System.currentTimeMillis();
			System.out.println( "saved in " + ( t1 - t0 ) + " ms" );

			final Model loaded = new Model();
			final Spot s = loaded.addSpot( 0, pos, cov, loaded.getGraph().vertexRef() );
			s.feature( LABEL );
			s.feature( TRACKLENGTH );
			t0 = System.currentTimeMillis();
			loaded.loadRaw( file );
			t1 = System.currentTimeMillis();
			System.out.println( "loaded in " + ( t1 - t0 ) + " ms" );

			final Spot next = model.getGraph().vertices().iterator().next();
			System.out.println( next.feature( LABEL ).get() );
			System.out.println( next.feature( TRACKLENGTH ).get() );
		}
		catch ( final IOException e )
		{
			e.printStackTrace();
		}
		}
	}
}
