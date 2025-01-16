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
package gnu.trove.iterator;

import gnu.trove.list.TIntList;

/**
 * Iterate a sorted {@link TIntList} by starting at a given element and then
 * alternatingly going one element backward and forward.
 *
 * @author Jean-Yves Tinevez
 */
public class TIntAlternatingIterator implements TIntIterator
{
	private final TIntList list;

	private final int startValue;

	private boolean hasNext;

	private int inc;

	private int dec;

	private boolean nextIsIncrement;

	private int index;

	private int startIndex;

	/**
	 *
	 * @param list
	 *            the list to iterate. <b>Must be sorted.</b>
	 * @param startValue
	 *            the value to start the iterator with. It will be searched for
	 *            in the list, and the smallest entry <em>e &ge; startValue</em>
	 *            will be used as a starting point.
	 */
	public TIntAlternatingIterator( final TIntList list, final int startValue )
	{
		this.list = list;
		this.startValue = startValue;
		init();
	}

	private void init()
	{
		if ( list.isEmpty() )
		{
			hasNext = false;
			return;
		}

		startIndex = list.binarySearch( startValue );
		if ( startIndex < 0 )
		{
			startIndex = -( 1 + startIndex );
		}

		dec = 1;
		inc = 0;
		nextIsIncrement = true;
		hasNext = true;
		prepare();
	}

	private void prepare()
	{
		if ( startIndex - dec < 0 && startIndex + inc >= list.size() )
		{
			hasNext = false;
			return;
		}

		if ( startIndex - dec < 0 || ( nextIsIncrement && startIndex + inc < list.size() ) )
		{
			index = startIndex + inc;
			inc++;
			nextIsIncrement = false;
			return;
		}
		if ( startIndex + inc >= list.size() || ( !nextIsIncrement && startIndex - dec >= 0 ) )
		{
			index = startIndex - dec;
			dec++;
			nextIsIncrement = true;
			return;
		}
	}

	@Override
	public boolean hasNext()
	{
		return hasNext;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException( "remove() is not supported for this iterator." );
	}

	@Override
	public int next()
	{
		final int next = list.get( index );
		prepare();
		return next;
	}
}
