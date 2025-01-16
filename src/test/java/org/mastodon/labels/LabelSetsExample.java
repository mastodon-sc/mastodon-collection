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
