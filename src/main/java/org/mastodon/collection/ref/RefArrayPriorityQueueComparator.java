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
package org.mastodon.collection.ref;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.mastodon.RefPool;

import gnu.trove.impl.Constants;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * Min-heap priority queue for {@link Comparable} Ref objects
 *
 * @param <O>
 *            the type of objects in the queue.
 * @author Tobias Pietzsch
 */
public class RefArrayPriorityQueueComparator< O > implements IntBackedRefCollection< O >, RefPoolBackedRefCollection< O >, Queue< O >
{
	final Class< O > elementType;

	private Comparator< O > comparator;

	private final RefArrayList< O > heap;

	private final O ref1;

	private final O ref2;

	private final O ref3;

	public RefArrayPriorityQueueComparator( final RefPool< O > pool, final Comparator< O > comparator )
	{
		this( pool, comparator, Constants.DEFAULT_CAPACITY );
	}

	public RefArrayPriorityQueueComparator( final RefPool< O > pool, final Comparator< O > comparator, final int initialCapacity )
	{
		this.comparator = comparator;
		heap = new RefArrayList<>( pool, initialCapacity );
		elementType = pool.getRefClass();
		ref1 = heap.createRef();
		ref2 = heap.createRef();
		ref3 = heap.createRef();
	}

	/**
	 * Removes all of the elements from this priority queue. The queue will be
	 * empty after this call returns.
	 */
	@Override
	public void clear()
	{
		heap.clear();
	}

	/**
	 * Sets the size of the queue to 0, but does not change its capacity. This
	 * method can be used as an alternative to the {@link #clear()} method if
	 * you want to recycle a queue without allocating new backing arrays.
	 *
	 * @see TIntArrayList#reset()
	 * @see TIntArrayList#resetQuick()
	 */
	public void reset()
	{
		heap.resetQuick();
	}

	@Override
	public O poll()
	{
		return poll( heap.createRef() );
	}

	public O poll( final O obj )
	{
		switch ( heap.size() )
		{
		case 0:
			return null;
		case 1:
			return heap.remove( 0, obj );
		default:
			heap.get( 0, obj );
			heap.set( 0, heap.remove( heap.size() - 1, ref1 ), ref3 );
			siftDown( 0 );
			return obj;
		}
	}

	/**
	 * Inserts the specified element into this priority queue.
	 *
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements
	 *             currently in this priority queue according to the priority
	 *             queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	@Override
	public boolean offer( final O obj )
	{
		if ( obj == null )
			throw new NullPointerException();
		heap.add( obj );
		siftUp( heap.size() - 1 );
		return true;
	}

	private void siftDown( int i )
	{
		final O parent = heap.get( i, ref1 );
		final int size = heap.size();
		for ( int j = ( i << 1 ) + 1; j < size; i = j, j = ( i << 1 ) + 1 )
		{
			O child = heap.get( j, ref2 );
			if ( j + 1 < size && comparator.compare( heap.get( j + 1, ref3 ), child ) < 0 )
				child = heap.get( ++j, ref2 );
			if ( comparator.compare( parent, child ) > 0 )
				heap.set( i, child, ref3 );
			else
				break;
		}
		heap.set( i, parent, ref3 );
	}

	private void siftUp( int i )
	{
		final O child = heap.get( i, ref1 );
		while ( i > 0 )
		{
			final int pi = ( i - 1 ) >>> 1;
			final O parent = heap.get( pi, ref2 );
			if ( comparator.compare( child, parent ) >= 0 )
				break;
			heap.set( i, parent, ref3 );
			i = pi;
		}
		heap.set( i, child, ref3 );
	}

	@Override
	public O peek()
	{
		return peek( heap.createRef() );
	}

	public O peek( final O obj )
	{
		return heap.isEmpty() ? null : heap.get( 0, obj );
	}

	@Override
	public O remove()
	{
		if ( isEmpty() )
			throw new NoSuchElementException();
		else
			return poll();
	}

	@Override
	public O element()
	{
		if ( isEmpty() )
			throw new NoSuchElementException();
		else
			return peek();
	}

	@Override
	public O createRef()
	{
		return heap.createRef();
	}

	@Override
	public void releaseRef( final O obj )
	{
		heap.releaseRef( obj );
	}

	@Override
	public TIntList getIndexCollection()
	{
		return heap.getIndexCollection();
	}

	@Override
	public RefPool< O > getRefPool()
	{
		return heap.getRefPool();
	}

	@Override
	public int size()
	{
		return heap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return heap.isEmpty();
	}

	@Override
	public boolean contains( final Object o )
	{
		return heap.contains( o );
	}

	@Override
	public Iterator< O > iterator()
	{
		return heap.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return heap.toArray();
	}

	@Override
	public < T > T[] toArray( final T[] a )
	{
		return heap.toArray( a );
	}

	@Override
	public boolean add( final O e )
	{
		return offer( e );
	}

	@Override
	public boolean containsAll( final Collection< ? > c )
	{
		return heap.containsAll( c );
	}

	@Override
	public boolean addAll( final Collection< ? extends O > c )
	{
		for ( final O o : c )
			add( o );
		return !c.isEmpty();
	}

	@Override
	public boolean remove( final Object o )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll( final Collection< ? > c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll( final Collection< ? > c )
	{
		throw new UnsupportedOperationException();
	}
}
