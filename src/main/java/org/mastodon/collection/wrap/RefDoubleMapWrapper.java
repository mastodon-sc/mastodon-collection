/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.collection.wrap;

import org.mastodon.collection.RefDoubleMap;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;

public class RefDoubleMapWrapper< K > extends TObjectDoubleHashMap< K > implements RefDoubleMap< K >
{
	/*
	 * This is not exactly a wrapper, since TROVE provides the right mother
	 * class, but we stick to the naming convention.
	 */

	private static final float DEFAULT_LOAD_FACTOR = Constants.DEFAULT_LOAD_FACTOR;

	public RefDoubleMapWrapper( final double noEntryValue, final int initialCapacity )
	{
		super( initialCapacity, DEFAULT_LOAD_FACTOR, noEntryValue );
	}

	public RefDoubleMapWrapper( final double noEntryValue )
	{
		this( noEntryValue, Constants.DEFAULT_CAPACITY );
	}

	@Override
	public K createRef()
	{
		return null;
	}

	@Override
	public void releaseRef( final K obj )
	{}

	@Override
	public boolean forEachKey( final TObjectProcedure< ? super K > procedure, final K ref )
	{
		return forEachKey( procedure );
	}

	@Override
	public boolean forEachEntry( final TObjectDoubleProcedure< ? super K > procedure, final K ref )
	{
		return forEachEntry( procedure );
	}

	@Override
	public boolean retainEntries( final TObjectDoubleProcedure< ? super K > procedure, final K ref )
	{
		return retainEntries( procedure );
	}

}
