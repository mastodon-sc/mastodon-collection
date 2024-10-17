/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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
