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
package org.mastodon.pool;

import java.util.Iterator;

import org.mastodon.Options;

import gnu.trove.list.array.TIntArrayList;

/**
 * A pool of {@link MappedElement MappedElements}. This is realized on top of
 * one or more {@link MappedElementArray}. It has a current size() and capacity,
 * and provides the possibility to {@link #create()} and {@link #free(int)}
 * elements.
 *
 * <p>
 * If elements are {@link #create() added} beyond the current capacity, the pool
 * grows the underlying storage. If elements are {@link #free(int) removed}, the
 * capacity is not decreased. Instead, free elements are added to a linked list
 * and reused for creating new elements. This ensures the crucial property, that
 * the internal index of any existing element remains fixed.
 *
 * <p>
 * <em>Note that this class is not thread-safe!</em>
 *
 * @param <T>
 *            the {@link MappedElement} type stored in this pool.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public abstract class MemPool< T extends MappedElement >
{
	/**
	 * How to check whether an element is free. This is relevant for
	 * {@link MemPool#iterator()} because the iterator should skip over free
	 * elements, and for {@link MemPool#free(int)} to guard against freeing
	 * already freed elements.
	 */
	public enum FreeElementPolicy
	{
		/**
		 * Don't check whether an element is free. Adding/freeing elements works
		 * as expected, but free elements will not be skipped when iterating!
		 */
		UNCHECKED,

		/**
		 * Use a magic number (first 4 bytes == -2) to check whether an element
		 * is free. This works, as long as this magic number never occurs in
		 * valid element data.
		 */
		CHECK_MAGIC_NUMBER,

		/**
		 * When iterating the MemPool, explicitly build a list of free indices.
		 * This is the safest choice, but potentially expensive.
		 */
		CHECK_FREE_ELEMENT_LIST
	}

	/**
	 * Magic number used to indicate a free element slot. Allocated elements
	 * must never use this number as the first 4 bytes of their data.
	 */
	public static final int FREE_ELEMENT_MAGIC_NUMBER = -2;

	private final FreeElementPolicy freeElementPolicy;

	private final TIntArrayList tmpFreeList;

	/**
	 * How many bytes each T occupies.
	 */
	protected final int bytesPerElement;

	/**
	 * One proxy access into the underlying {@link MappedElementArray}. This is
	 * used to manipulate the linked list of free elements
	 */
	protected T dataAccess;

	/**
	 * Current capacity of the pool. The underlying storage can hold this many
	 * elements before it must be resized.
	 */
	protected int capacity;

	/**
	 * The number of elements currently allocated in this pool.
	 */
	protected int size;

	/**
	 * The max size this pool ever had. This equals {@link #size} plus the number of
	 * elements in the free-element linked list.
	 */
	protected int allocatedSize;

	/**
	 * The element index of the first free element, that is, the start of the
	 * free-element linked list.
	 */
	protected int firstFreeIndex;

	/**
	 * Creates an empty pool which can hold {@code capacity} elements of
	 * {@code ByteMappedElement} bytes each.
	 *
	 * @param capacity
	 *            how many elements this pool should hold.
	 * @param bytesPerElement
	 *            how many bytes each element occupies.
	 * @param freeElementPolicy
	 *            how to check for free elements.
	 */
	public MemPool( final int capacity, final int bytesPerElement, final FreeElementPolicy freeElementPolicy )
	{
		this.freeElementPolicy = freeElementPolicy;
		this.tmpFreeList = new TIntArrayList( 10, -1 );
		this.bytesPerElement = Math.max( bytesPerElement, 8 );
		this.capacity = capacity;
		clear();
	}

	/**
	 * Frees all allocated elements.
	 */
	public void clear()
	{
		size = 0;
		allocatedSize = 0;
		firstFreeIndex = -1;
	}

	/**
	 * Gets the number of elements currently allocated in this pool.
	 *
	 * @return number of elements.
	 */
	public int size()
	{
		return size;
	}

	/**
	 * Allocates a new element. This is either taken from the free-element list
	 * or appended to the end of the pool.
	 *
	 * @return element index of the new element.
	 */
	public int create()
	{
		++size;
		if ( firstFreeIndex < 0 )
			return append();
		else
		{
			final int index = firstFreeIndex;
			updateAccess( dataAccess, firstFreeIndex );
			// Clear FREE_ELEMENT_MAGIC_NUMBER to protect against objects that do nothing in setToUninitializedState()
			dataAccess.putIndex( 0, 0 );
			firstFreeIndex = dataAccess.getIndex( 4 );
			return index;
		}
	}

	/**
	 * Frees the element at the given element index.
	 *
	 * @param index
	 *            element index.
	 */
	public void free( final int index )
	{
		if ( index >= 0 && index < allocatedSize )
		{
			updateAccess( dataAccess, index );

			if ( Options.DEBUG )
			{
				if ( isFree( dataAccess, index ) )
					throw new IllegalArgumentException( "Element at index " + index + " is already free." );
			}

			--size;
			dataAccess.putIndex( FREE_ELEMENT_MAGIC_NUMBER, 0 );
			dataAccess.putIndex( firstFreeIndex, 4 );
			firstFreeIndex = index;
		}
	}

	boolean isFree( final T access, final int index )
	{
		switch ( freeElementPolicy )
		{
		default:
			return false;
		case CHECK_MAGIC_NUMBER:
			return access.getInt( 0 ) == FREE_ELEMENT_MAGIC_NUMBER;
		case CHECK_FREE_ELEMENT_LIST:
			return ordererFreeElementsList( tmpFreeList ).contains( index );
		}
	}

	/**
	 * Put ordered list of indices of free elements into {@code free}.
	 */
	private TIntArrayList ordererFreeElementsList( final TIntArrayList free )
	{
		final int nFree = allocatedSize - size;
		free.ensureCapacity( nFree );
		free.clear();
		int i = firstFreeIndex;
		while ( i >= 0 )
		{
			free.add( i );
			updateAccess( dataAccess, i );
			i = dataAccess.getIndex( 4 );
		}
		free.sort();
		return free;
	}

	/**
	 * Creates a new proxy access. This can be made to refer the element at a
	 * given index in this pool by {@link #updateAccess(MappedElement, int)}.
	 *
	 * @return a new proxy access.
	 */
	public abstract T createAccess();

	/**
	 * Makes {@code access} refer to the element at {@code index}.
	 *
	 * @param access
	 *            the proxy to update.
	 * @param index
	 *            the element index.
	 */
	public abstract void updateAccess( final T access, final int index );

	/**
	 * Swaps the element at {@code index0} with the element at {@code index1}.
	 *
	 * @param index0
	 *            the index of the first element.
	 * @param index1
	 *            the index of the second element.
	 */
	public abstract void swap( final int index0, final int index1 );

	/**
	 * Appends a new element at the end of the list. Must be implemented in
	 * subclasses. It is called when allocating an element and the free-element
	 * list is empty.
	 *
	 * @return the index of the appended new element.
	 */
	protected abstract int append();

	/**
	 * Gets a {@link PoolIterator} of this pool.
	 * <p>
	 * A {@link PoolIterator} is not an {@link Iterator Iterator&lt;T&gt;} of
	 * the allocated elements themselves, but rather an iterator of their
	 * element indices.
	 *
	 * @return a new iterator.
	 */
	public PoolIterator< T > iterator()
	{
		switch ( freeElementPolicy )
		{
		default:
		case UNCHECKED:
			return new UncheckedPoolIterator<>( this );
		case CHECK_MAGIC_NUMBER:
			return new CheckMagicNumberPoolIterator<>( this );
		case CHECK_FREE_ELEMENT_LIST:
			return new CheckFreeListPoolIterator<>( this );
		}
	}

	/**
	 * Iterator of the indices of allocated elements.
	 * 
	 * @param <T>
	 *            the type mapping of objects.
	 */
	public interface PoolIterator< T extends MappedElement >
	{
		public void reset();

		public boolean hasNext();

		public int next();

		public void remove();
	}

	/**
	 * Iterator base class for all {@link FreeElementPolicy
	 * FreeElementPolicies}.
	 */
	private static abstract class AbstractPoolIterator< T extends MappedElement > implements PoolIterator< T >
	{
		protected final MemPool< T > pool;

		protected int nextIndex;

		protected int currentIndex;

		protected AbstractPoolIterator( final MemPool< T > pool )
		{
			this.pool = pool;
		}

		@Override
		public void reset()
		{
			nextIndex = ( pool.allocatedSize == 0 ) ? 1 : -1;
			currentIndex = -1;
			prepareNextElement();
		}

		protected void prepareNextElement()
		{
			if ( hasNext() )
				++nextIndex;
		}

		@Override
		public boolean hasNext()
		{
			return nextIndex < pool.allocatedSize;
		}

		@Override
		public int next()
		{
			currentIndex = nextIndex;
			prepareNextElement();
			return currentIndex;
		}

		@Override
		public void remove()
		{
			if ( currentIndex >= 0 )
				pool.free( currentIndex );
		}
	}

	/**
	 * Iterator for {@code FreeElementPolicy.UNCHECKED}.
	 */
	private static class UncheckedPoolIterator< T extends MappedElement > extends AbstractPoolIterator< T >
	{
		private UncheckedPoolIterator( final MemPool< T > pool )
		{
			super( pool );
			reset();
		}
	}

	/**
	 * Iterator for {@code FreeElementPolicy.CHECK_MAGIC_NUMBER}.
	 */
	private static class CheckMagicNumberPoolIterator< T extends MappedElement > extends AbstractPoolIterator< T >
	{
		private final T element;

		private CheckMagicNumberPoolIterator( final MemPool< T > pool )
		{
			super( pool );
			element = pool.createAccess();
			reset();
		}

		@Override
		protected void prepareNextElement()
		{
			if ( hasNext() )
			{
				while ( ++nextIndex < pool.allocatedSize )
				{
					pool.updateAccess( element, nextIndex );
					final boolean isFree = element.getInt( 0 ) == FREE_ELEMENT_MAGIC_NUMBER;
					if ( !isFree )
						break;
				}
			}
		}
	}

	/**
	 * Iterator for {@code FreeElementPolicy.CHECK_FREE_ELEMENT_LIST}.
	 */
	private static class CheckFreeListPoolIterator< T extends MappedElement > extends AbstractPoolIterator< T >
	{
		private final TIntArrayList freeElements;

		private int nextFreeElementsIndex;

		private int nextFree;

		private CheckFreeListPoolIterator( final MemPool< T > pool )
		{
			super( pool );
			freeElements = new TIntArrayList();
			reset();
		}

		@Override
		public void reset()
		{
			pool.ordererFreeElementsList( freeElements );
			nextFreeElementsIndex = 0;
			nextFree = nextFreeElementsIndex < freeElements.size() ? freeElements.getQuick( nextFreeElementsIndex++ ) : pool.allocatedSize;
			super.reset();
		}

		@Override
		protected void prepareNextElement()
		{
			if ( hasNext() )
			{
				while ( ++nextIndex < pool.allocatedSize )
				{
					if ( nextIndex != nextFree )
						break;
					nextFree = nextFreeElementsIndex < freeElements.size() ? freeElements.getQuick( nextFreeElementsIndex++ ) : pool.allocatedSize;
				}
			}
		}
	}

	/**
	 * A factory for {@link MemPool}.
	 *
	 * @param <T>
	 *            the {@link MappedElement} type of the created pool.
	 */
	public interface Factory< T extends MappedElement >
	{
		public MemPool< T > createPool( final int capacity, final int bytesPerElement, final FreeElementPolicy freeElementPolicy );
	}
}
