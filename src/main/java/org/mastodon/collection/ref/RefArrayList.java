/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.mastodon.Ref;
import org.mastodon.RefPool;
import org.mastodon.collection.RefList;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * A {@link RefList} implementation for {@link Ref} objects, based on a Trove
 * {@link TIntArrayList}.
 *
 * @param <O>
 *            the type of elements maintained by this list.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class RefArrayList< O > implements IntBackedRefCollection< O >, RefPoolBackedRefCollection< O >, RefList< O >
{
	private final TIntArrayList indices;

	final RefPool< O > pool;

	final Class< O > elementType;

	public RefArrayList( final RefPool< O > pool )
	{
		this.pool = pool;
		indices = new TIntArrayList();
		elementType = pool.getRefClass();
	}

	public RefArrayList( final RefPool< O > pool, final int initialCapacity )
	{
		this.pool = pool;
		indices = new TIntArrayList( initialCapacity );
		elementType = pool.getRefClass();
	}

	protected RefArrayList( final RefArrayList< O > list, final TIntArrayList indexSubList )
	{
		pool = list.pool;
		indices = indexSubList;
		elementType = pool.getRefClass();
	}

	@Override
	public O createRef()
	{
		return pool.createRef();
	}

	@Override
	public void releaseRef( final O obj )
	{
		pool.releaseRef( obj );
	}

	@Override
	public TIntArrayList getIndexCollection()
	{
		return indices;
	}

	@Override
	public RefPool< O > getRefPool()
	{
		return pool;
	}

	@Override
	public boolean add( final O obj )
	{
		return indices.add( pool.getId( obj ) );
	}

	@Override
	public void add( final int index, final O obj )
	{
		indices.insert( index, pool.getId( obj ) );
	}

	// TODO: consider throwing exception in addAll if objs are not from same pool
	@Override
	public boolean addAll( final Collection< ? extends O > objs )
	{
		if ( objs instanceof IntBackedRefCollection )
			return indices.addAll( ( ( IntBackedRefCollection< ? > ) objs ).getIndexCollection() );
		else
		{
			for ( final O obj : objs )
				indices.add( pool.getId( obj ) );
			return !objs.isEmpty();
		}
	}

	@Override
	public boolean addAll( final int index, final Collection< ? extends O > objs )
	{
		if ( objs instanceof IntBackedRefCollection )
		{
			final TIntCollection objIndices = ( ( IntBackedRefCollection< ? > ) objs ).getIndexCollection();
			indices.insert( index, objIndices.toArray() );
		}
		else
		{
			final int[] indicesToInsert = new int[ objs.size() ];
			int i = 0;
			for ( final O obj : objs )
				indicesToInsert[ i++ ] = pool.getId( obj );
			indices.insert( index, indicesToInsert );
		}
		return !objs.isEmpty();
	}

	@Override
	public void clear()
	{
		indices.clear();
	}

	/**
	 * Sets the size of the list to 0, but does not change its capacity. This
	 * method can be used as an alternative to the {@link #clear()} method if
	 * you want to recycle a list without allocating new backing arrays.
	 *
	 * @see TIntArrayList#reset()
	 */
	public void reset()
	{
		indices.reset();
	}

	/**
	 * Sets the size of the list to 0, but does not change its capacity. This
	 * method can be used as an alternative to the {@link #clear()} method if
	 * you want to recycle a list without allocating new backing arrays. This
	 * method differs from {@link #reset()} in that it does not clear the old
	 * values in the backing array. Thus, it is possible for getQuick to return
	 * stale data if this method is used and the caller is careless about bounds
	 * checking.
	 *
	 * @see TIntArrayList#resetQuick()
	 */
	public void resetQuick()
	{
		indices.resetQuick();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean contains( final Object obj )
	{
		return ( elementType.isInstance( obj ) )
				? indices.contains( pool.getId( ( O ) obj ) )
				: false;
	}

	@Override
	public boolean containsAll( final Collection< ? > objs )
	{
		if ( objs instanceof IntBackedRefCollection )
			return indices.containsAll( ( ( IntBackedRefCollection< ? > ) objs ).getIndexCollection() );
		else
		{
			for ( final Object obj : objs )
				if ( !contains( obj ) )
					return false;
			return true;
		}
	}

	public O getQuick( final int index, final O obj )
	{
		return pool.getObject( indices.getQuick( index ), obj );
	}

	@Override
	public O get( final int index, final O obj )
	{
		return pool.getObject( indices.get( index ), obj );
	}

	@Override
	public O get( final int index )
	{
		return get( index, pool.createRef() );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public int indexOf( final Object obj )
	{
		return ( elementType.isInstance( obj ) )
				? indices.indexOf( pool.getId( ( O ) obj ) )
				: -1;
	}

	@Override
	public boolean isEmpty()
	{
		return indices.isEmpty();
	}

	@Override
	public Iterator< O > iterator()
	{
		return new Iterator< O >()
		{
			final TIntIterator ii = indices.iterator();

			final O obj = pool.createRef();

			@Override
			public boolean hasNext()
			{
				return ii.hasNext();
			}

			@Override
			public O next()
			{
				return pool.getObject( ii.next(), obj );
			}

			@Override
			public void remove()
			{
				ii.remove();
			}
		};
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public int lastIndexOf( final Object obj )
	{
		return ( elementType.isInstance( obj ) )
				? indices.lastIndexOf( pool.getId( ( O ) obj ) )
				: -1;
	}

	// TODO: modCount is not updated currently.
	private final int modCount = 0;

	/**
	 * Shamelessly stolen from java.util.AbstractList
	 */
	private class ListItr implements ListIterator< O >
	{
		final O obj = pool.createRef();

		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		int cursor;

		/**
		 * Index of element returned by most recent call to next or previous.
		 * Reset to -1 if this element is deleted by a call to remove.
		 */
		int lastRet = -1;

		/**
		 * The modCount value that the iterator believes that the backing List
		 * should have. If this expectation is violated, the iterator has
		 * detected concurrent modification.
		 */
		int expectedModCount = modCount;

		ListItr( final int index )
		{
			cursor = index;
		}

		@Override
		public boolean hasNext()
		{
			return cursor != size();
		}

		@Override
		public O next()
		{
			checkForComodification();
			try
			{
				final int i = cursor;
				final O next = get( i, obj );
				lastRet = i;
				cursor = i + 1;
				return next;
			}
			catch ( final IndexOutOfBoundsException e )
			{
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove()
		{
			if ( lastRet < 0 )
				throw new IllegalStateException();
			checkForComodification();

			try
			{
				RefArrayList.this.remove( lastRet );
				if ( lastRet < cursor )
					cursor--;
				lastRet = -1;
				expectedModCount = modCount;
			}
			catch ( final IndexOutOfBoundsException e )
			{
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public boolean hasPrevious()
		{
			return cursor != 0;
		}

		@Override
		public O previous()
		{
			checkForComodification();
			try
			{
				final int i = cursor - 1;
				final O previous = get( i, obj );
				lastRet = cursor = i;
				return previous;
			}
			catch ( final IndexOutOfBoundsException e )
			{
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		@Override
		public int nextIndex()
		{
			return cursor;
		}

		@Override
		public int previousIndex()
		{
			return cursor - 1;
		}

		@Override
		public void set( final O o )
		{
			if ( lastRet < 0 )
				throw new IllegalStateException();
			checkForComodification();

			try
			{
				RefArrayList.this.set( lastRet, o );
				expectedModCount = modCount;
			}
			catch ( final IndexOutOfBoundsException ex )
			{
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void add( final O o )
		{
			checkForComodification();

			try
			{
				final int i = cursor;
				RefArrayList.this.add( i, o );
				lastRet = -1;
				cursor = i + 1;
				expectedModCount = modCount;
			}
			catch ( final IndexOutOfBoundsException ex )
			{
				throw new ConcurrentModificationException();
			}
		}

		final void checkForComodification()
		{
			if ( modCount != expectedModCount )
				throw new ConcurrentModificationException();
		}
	}

    @Override
	public ListIterator< O > listIterator()
	{
		return new ListItr( 0 );
	}

	@Override
	public ListIterator< O > listIterator( final int index )
	{
		return new ListItr( index );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean remove( final Object obj )
	{
		return ( elementType.isInstance( obj ) )
				? indices.remove( pool.getId( ( O ) obj ) )
				: false;
	}

	@Override
	public O remove( final int index, final O obj )
	{
		return pool.getObject( indices.removeAt( index ), obj );
	}

	@Override
	public O remove( final int index )
	{
		return remove( index, pool.createRef() );
	}

	@Override
	public boolean removeAll( final Collection< ? > objs )
	{
		if ( objs instanceof IntBackedRefCollection )
			return indices.removeAll( ( ( IntBackedRefCollection< ? > ) objs ).getIndexCollection() );
		else
		{
			boolean changed = false;
			for ( final Object obj : objs )
				if ( remove( obj ) )
					changed = true;
			return changed;
		}
	}

	@Override
	public boolean retainAll( final Collection< ? > objs )
	{
		if ( objs instanceof IntBackedRefCollection )
			return indices.retainAll( ( ( IntBackedRefCollection< ? > ) objs ).getIndexCollection() );
		else
		{
			// TODO
			throw new UnsupportedOperationException( "not yet implemented" );
		}
	}

	@Override
	public O set( final int index, final O obj, final O replacedObj )
	{
		return pool.getObject(
				indices.set( index, pool.getId( obj ) ),
				replacedObj );
	}

	@Override
	public O set( final int index, final O obj )
	{
		return set( index, obj, pool.createRef() );
	}

	@Override
	public int size()
	{
		return indices.size();
	}

	@Override
	public List< O > subList( final int fromIndex, final int toIndex )
	{
		return new RefArrayList<>( this, ( TIntArrayList ) indices.subList( fromIndex, toIndex ) );
	}

	@Override
	public void shuffle( final Random rand )
	{
		indices.shuffle( rand );
	}

	@Override
	public void sort( final Comparator< ? super O > comparator )
	{
		if ( indices.size() < 2 )
			return;
		quicksort( 0, size() - 1, comparator, createRef(), createRef() );
	}

	private void quicksort( final int low, final int high, final Comparator< ? super O > comparator, final O tmpRef1, final O tmpRef2 )
	{
		final O pivot = get( ( low + high ) / 2, tmpRef1 );

		int i = low;
		int j = high;

		do
		{
			while ( comparator.compare( get( i, tmpRef2 ), pivot ) < 0 )
				i++;
			while ( comparator.compare( pivot, get( j, tmpRef2 ) ) < 0 )
				j--;
			if ( i <= j )
			{
				swap( i, j );
				i++;
				j--;
			}
		}
		while ( i <= j );

		if ( low < j )
			quicksort( low, j, comparator, tmpRef1, tmpRef2 );
		if ( i < high )
			quicksort( i, high, comparator, tmpRef1, tmpRef2 );
	}

	@Override
	public void swap( final int i, final int j )
	{
		final int tmp = indices.get( i );
		indices.set( i, indices.get( j ) );
		indices.set( j, tmp );
	}

	@Override
	public Object[] toArray()
	{
		final Object[] obj = new Object[ indices.size() ];
		for ( int i = 0; i < obj.length; i++ )
		{
			obj[ i ] = get( i );
		}
		return obj;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public < A > A[] toArray( final A[] a )
	{
		return ( A[] ) toArray();
	}

	@Override
	public String toString()
	{
		if ( isEmpty() ) { return "[]"; }
		final StringBuffer sb = new StringBuffer();
		final Iterator< ? > it = iterator();
		sb.append( "[" + it.next().toString() );
		while ( it.hasNext() )
		{
			sb.append( ", " + it.next().toString() );
		}
		sb.append( "]" );
		return sb.toString();
	}
}
