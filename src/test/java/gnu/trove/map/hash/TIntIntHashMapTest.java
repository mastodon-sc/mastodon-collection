/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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
package gnu.trove.map.hash;

import org.junit.Test;

import gnu.trove.map.hash.TIntIntHashMap;

public class TIntIntHashMapTest
{

	@Test
	public void test()
	{
		final TIntIntHashMap map = new TIntIntHashMap( 10, 0.5f, -100000, -2000000 );

		map.put( 1, 0 );
		map.put( 2, 5 );
		map.put( 3, 2 );
		map.put( 4, 3 );

//		final TIntCollection values = map.valueCollection();
//		final int initSize = values.size();

		/*
		 * Known problem. Check https://bitbucket.org/trove4j/trove/issue/25/
		 * _k__v_hashmaptvalueviewremove-is
		 */

//		final boolean removed0 = values.remove( 5 );
//		assertTrue( "Could not remove an existing value.", removed0 );
//		assertEquals( "Value collection has not been shrinked by iterator.remove().", initSize - 1, values.size() );
//		assertEquals( "Corresponding map has not been shrinked by iterator.remove().", initSize - 1, map.size() );

	}

}
