package org.mastodon.labels;

import org.mastodon.pool.TestObject;
import org.mastodon.pool.TestObjectPool;

public class LabelSetsExample
{
	public static void main( final String[] args )
	{
		final TestObjectPool pool = new TestObjectPool( 100 );

		final TestObject a = pool.create().init( 0 );
		final TestObject b = pool.create().init( 1 );
		final TestObject c = pool.create().init( 2 );

		System.out.println( "a = " + a );
		System.out.println( "b = " + b );
		System.out.println( "c = " + c );
		System.out.println();

		final LabelSets< TestObject, Integer > labelsets = new LabelSets<>( pool );
		System.out.println( "labels(a) = " + labelsets.getLabels( a ) );
		System.out.println( "labels(b) = " + labelsets.getLabels( b ) );
		System.out.println( "labels(c) = " + labelsets.getLabels( c ) );
		System.out.println();

		labelsets.getLabels( a ).add( 13 );
		labelsets.getLabels( a ).add( 42 );
		labelsets.getLabels( b ).add( 42 );
		labelsets.getLabels( c ).add( 1 );
		System.out.println( "labels(a) = " + labelsets.getLabels( a ) );
		System.out.println( "labels(b) = " + labelsets.getLabels( b ) );
		System.out.println( "labels(c) = " + labelsets.getLabels( c ) );
		System.out.println( "labels(a).contains(42) = " + labelsets.getLabels( a ).contains( 42 ) );
		System.out.println( "labels(c).contains(1) = " + labelsets.getLabels( c ).contains( 1 ) );
		System.out.println();

		labelsets.getLabels( a ).remove( 42 );
		labelsets.getLabels( b ).remove( 99 );
		labelsets.getLabels( c ).remove( 1 );
		System.out.println( "labels(a) = " + labelsets.getLabels( a ) );
		System.out.println( "labels(b) = " + labelsets.getLabels( b ) );
		System.out.println( "labels(c) = " + labelsets.getLabels( c ) );
		System.out.println( "labels(a).contains(42) = " + labelsets.getLabels( a ).contains( 42 ) );
		System.out.println( "labels(c).contains(1) = " + labelsets.getLabels( c ).contains( 1 ) );
	}
}
