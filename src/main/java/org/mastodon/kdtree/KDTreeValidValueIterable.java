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
package org.mastodon.kdtree;

import java.util.Iterator;

import org.mastodon.RefPool;
import org.mastodon.pool.MappedElement;

import gnu.trove.deque.TIntArrayDeque;
import gnu.trove.list.array.TIntArrayList;
import net.imglib2.RealLocalizable;

public class KDTreeValidValueIterable<
		O extends RealLocalizable,
		T extends MappedElement >
	implements Iterable< O >
{
	private final TIntArrayList nodes;

	private final TIntArrayList subtrees;

	private final KDTree< O, T > tree;

	private final boolean isDoubleIndices;

	public KDTreeValidValueIterable( final TIntArrayList singleNodes, final TIntArrayList subtrees, final KDTree< O, T > tree, final boolean isDoubleIndices )
	{
		this.nodes = singleNodes;
		this.subtrees = subtrees;
		this.tree = tree;
		this.isDoubleIndices = isDoubleIndices;
	}

	@Override
	public Iterator< O > iterator()
	{
		return isDoubleIndices ? new DoublesIter( tree ) : new Iter( tree );
	}

	private class Iter implements Iterator< O >
	{
		private int nextNodeIndex;

		private int nextSubtreeIndex;

		private final TIntArrayDeque stack;

		private final KDTreeNode< O, T > current;

		private final RefPool< O > pool;

		private final O ref;

		private final O nextref;

		private O next;

		private boolean hasNext;

		private Iter( final KDTree< O, T > tree )
		{
			nextNodeIndex = 0;
			nextSubtreeIndex = 0;
			stack = new TIntArrayDeque();
			current = tree.createRef();
			pool = tree.getObjectPool();
			ref = pool.createRef();
			nextref = pool.createRef();
			hasNext = prepareNext();
		}

		private boolean prepareNext()
		{
			while ( true )
			{
				if ( !stack.isEmpty() )
				{
					tree.getObject( stack.pop(), current );
					final int left = current.getLeftIndex();
					final int right = current.getRightIndex();
					if ( left >= 0 )
						stack.push( left );
					if ( right >= 0 )
						stack.push( right );
				}
				else if ( nextSubtreeIndex < subtrees.size() )
				{
					tree.getObject( subtrees.get( nextSubtreeIndex++ ), current );
					final int left = current.getLeftIndex();
					final int right = current.getRightIndex();
					if ( left >= 0 )
						stack.push( left );
					if ( right >= 0 )
						stack.push( right );
				}
				else if ( nextNodeIndex < nodes.size() )
				{
					tree.getObject( nodes.get( nextNodeIndex++ ), current );
				}
				else
					return false;

				if ( current.isValid() )
				{
					next = pool.getObject( current.getDataIndex(), nextref );
					return true;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public O next()
		{
			if ( hasNext )
			{
				final O current = pool.getObject( pool.getId( next ), ref );
				hasNext = prepareNext();
				return current;
			}
			return null;
		}

		@Override
		public void remove()
		{}
	}

	private class DoublesIter implements Iterator< O >
	{
		private int nextNodeIndex;

		private int nextSubtreeIndex;

		private final TIntArrayDeque stack;

		private final double[] doubles;

		private final int n;

		private final RefPool< O > pool;

		private final O ref;

		private final O nextref;

		private O next;

		private boolean hasNext;

		private DoublesIter( final KDTree< O, T > tree )
		{
			nextNodeIndex = 0;
			nextSubtreeIndex = 0;
			stack = new TIntArrayDeque();
			doubles = tree.getDoubles();
			n = tree.numDimensions();
			pool = tree.getObjectPool();
			ref = pool.createRef();
			nextref = pool.createRef();
			hasNext = prepareNext();
		}

		private boolean prepareNext()
		{
			while ( true )
			{
				int currentIndex;
				if ( !stack.isEmpty() )
				{
					currentIndex = stack.pop();
					final long leftright = Double.doubleToRawLongBits( doubles[ currentIndex + n ] );
					final int left = ( int ) ( leftright >> 32 );
					final int right = ( int ) leftright;
					if ( left >= 0 )
						stack.push( left );
					if ( right >= 0 )
						stack.push( right );
				}
				else if ( nextSubtreeIndex < subtrees.size() )
				{
					currentIndex = subtrees.get( nextSubtreeIndex++ );
					final long leftright = Double.doubleToRawLongBits( doubles[ currentIndex + n ] );
					final int left = ( int ) ( leftright >> 32 );
					final int right = ( int ) leftright;
					if ( left >= 0 )
						stack.push( left );
					if ( right >= 0 )
						stack.push( right );
				}
				else if ( nextNodeIndex < nodes.size() )
				{
					currentIndex = nodes.get( nextNodeIndex++ );
				}
				else
					return false;

				final int flags = ( int ) ( Double.doubleToRawLongBits( doubles[ currentIndex + n + 1 ] ) >> 32 );
				if ( flags == 0 ) // if node is valid
				{
					final int objIndex = ( int ) Double.doubleToRawLongBits( doubles[ currentIndex + n + 1 ] );
					next = pool.getObject( objIndex, nextref );
					return true;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public O next()
		{
			if ( hasNext )
			{
				final O current = pool.getObject( pool.getId( next ), ref );
				hasNext = prepareNext();
				return current;
			}
			return null;
		}

		@Override
		public void remove()
		{}
	}
}
