package org.mastodon.collection.ref;

import java.util.Queue;

import org.mastodon.RefPool;

import gnu.trove.impl.Constants;

/**
 * Min-heap priority queue for {@link Comparable} Ref objects
 *
 * @author Tobias Pietzsch
 */
public class RefArrayHeap< O extends Comparable< O > > //implements IntBackedRefCollection< O >, RefPoolBackedRefCollection< O >
{
	final RefPool< O > pool;

	final Class< O > elementType;

	/**
	 * min-heap
	 */
	private final RefArrayList< O > heap;

	private final O ref1;

	private final O ref2;

	private final O ref3;

	public RefArrayHeap( final RefPool< O > pool )
	{
		this( pool, Constants.DEFAULT_CAPACITY );
	}

	public RefArrayHeap( final RefPool< O > pool, final int initialCapacity )
	{
		this.pool = pool;
		heap = new RefArrayList<>( pool, initialCapacity );
		elementType = pool.getRefClass();
		ref1 = heap.createRef();
		ref2 = heap.createRef();
		ref3 = heap.createRef();
	}

    /**
     * Removes all of the elements from this priority queue.
     * The queue will be empty after this call returns.
     */
	public void clear()
	{
		heap.clear();
	}

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
     * @throws ClassCastException if the specified element cannot be
     *         compared with elements currently in this priority queue
     *         according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
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
    		if ( j + 1 < size && heap.get( j + 1, ref3 ).compareTo( child ) < 0 )
    			child = heap.get( ++j, ref2 );
    		if ( parent.compareTo( child ) > 0 )
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
			if ( child.compareTo( parent ) >= 0 )
				break;
			heap.set( i, parent, ref3 );
			i = pi;
		}
		heap.set( i, child, ref3 );
	}
}
