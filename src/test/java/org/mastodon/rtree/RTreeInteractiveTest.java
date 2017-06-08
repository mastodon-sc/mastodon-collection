package org.mastodon.rtree;

public class RTreeInteractiveTest
{

	public static void main( final String[] args )
	{
		final RectPool pool = new RectPool( 2, 10 );
		final double[][] bounds = new double[][] {
			{ 0., 0., 1., 1. },
			{ 1.1, 1.1, 2., 2. },
			{ 4., 0., 5., 1. },
			{ 0., 4., 1., 5. },
			{ 4., 4., 5., 5. }
		};

		final Rect rref = pool.createRef();
		for ( int i = 0; i < bounds.length; i++ )
			pool.create( rref ).init( Character.toString( ( char ) ( 'A' + i ) ), bounds[ i ] );

		System.out.println( "Reactangle in play:" );
		for ( final Rect rect : pool )
			System.out.println( rect );

		/*
		 * Create R-Tree.
		 */

		final RTree< Rect > rtree = RTree.rtree( pool.asRefCollection(), pool );
		final RTreeNode< Rect > tref = rtree.createRef();
		for ( final Rect rect : pool )
			rtree.add( rect, tref );

		System.out.println( "\nTree content:" );
		for ( final RTreeNode< Rect > node : rtree )
			System.out.println( node );

	}

}
