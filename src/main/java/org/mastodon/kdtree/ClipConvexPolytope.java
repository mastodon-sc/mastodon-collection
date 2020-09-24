/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.kdtree.ConvexPolytope;

/**
 * Partition points in an Euclidean space into sets that are inside and outside
 * a given convex polytope, respectively.
 *
 * @param <T>
 *            type of points (usually {@link RealLocalizable}).
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface ClipConvexPolytope< T > extends EuclideanSpace
{
	/**
	 * Partition points into inside and outside of the given
	 * {@link ConvexPolytope}.
	 *
	 * @param polytope
	 *            polytope to clip with.
	 */
	public void clip( final ConvexPolytope polytope );

	/**
	 * Partition points into inside and outside of a convex polytope. The
	 * polytope is specified by a set of planes, such that the polytope
	 * comprises points that are in the positive half-space of all planes.
	 *
	 * @param planes
	 *            array of planes specifying the polytope to clip with. Each
	 *            plane <em>x·n=m</em> is given as a {@code double} array
	 *            <em>[n<sub>1</sub>, ..., n<sub>N</sub>, m]</em>
	 */
	public void clip( final double[][] planes );

	/**
	 * Get points inside the convex polytope specified in the last
	 * {@link #clip(ConvexPolytope)} operation.
	 *
	 * @return points inside the convex polytope.
	 */
	public Iterable< T > getInsideValues();

	/**
	 * Get points outside the convex polytope specified in the last
	 * {@link #clip(ConvexPolytope)} operation.
	 *
	 * @return points outside the convex polytope.
	 */
	public Iterable< T > getOutsideValues();
}
