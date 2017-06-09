package org.mastodon.rtree;

public class RTreeInteractiveTest
{

	public static void main( final String[] args )
	{
		final RectPool pool = new RectPool( 2, 10 );
		final double[][] bounds = new double[][] {
			{ 0., 0., 1., 1. }, // A
			{ 1.1, 1.1, 2., 2. }, // B
			{ 4.5, 0., 5.5, 1. }, // C
			{ 0., 4., 1., 5. }, // D
			{ 4.5, 4., 5.5, 5. }, // E

			{ 0., 9., 1., 10. }, // F
			{ 4.5, 9., 5.5, 10. }, // G
			{ 9., 9., 10., 10. }, // H
			{ 9., 4., 10., 5. }, // I
			{ 9., 0., 10., 1. }, // J

			{ 1.1, 10.1, 2., 11. }, // K
			{ 5.6, 10.1, 6.5, 11. }, // L
			{ 10.1, 10.1, 11., 11. }, // M
			{ 10.1, 5.1, 11., 6. }, // N
			{ 10.1, 1.1, 11., 2. }, // O
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
		for ( final Rect rect : pool )
		{
			rtree.add( rect );

			System.out.println( "\n\n\nAfter adding rect, Tree content:" );
			for ( final RTreeNode< Rect > node : rtree )
				System.out.println( node );
		}

	}

}
