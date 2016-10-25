package org.mastodon.pool;

import java.util.ArrayList;

/**
 * A {@link MemPool} that keeps data in multiple {@link MappedElementArray}s to
 * allow for large pools.
 *
 * @param <T>
 *            the {@link MappedElement} type stored in this pool.
 * @param <A>
 *            the type of the primitive array used in the
 *            {@link MappedElementArray}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class MultiArrayMemPool< A extends MappedElementArray< A, T >, T extends MappedElement > extends MemPool< T >
{
	private final MappedElementArray.Factory< A > arrayFactory;

	private final int elementsPerArray;

	private final ArrayList< A > data;

	public MultiArrayMemPool( final MappedElementArray.Factory< A > arrayFactory, final int capacity, final int bytesPerElement )
	{
		super( capacity, bytesPerElement );
		this.arrayFactory = arrayFactory;

		elementsPerArray = arrayFactory.createArray( 0, this.bytesPerElement ).maxSize();

		final int numFullArrays = ( int ) ( capacity / elementsPerArray );
		data = new ArrayList<>( numFullArrays + 1 );
		for ( int i = 0; i < numFullArrays - 1; ++i )
			data.add( arrayFactory.createArray( elementsPerArray, this.bytesPerElement ) );
		data.add( arrayFactory.createArray( capacity - numFullArrays * elementsPerArray, this.bytesPerElement ) );

		dataAccess = data.get( 0 ).createAccess();
	}

	@Override
	protected int append()
	{
		final int index = allocatedSize++;
		if ( allocatedSize > capacity )
		{
			// does the last array have maximum size?
			final int last = data.size() - 1;
			final A lastArray = data.get( last );
			if ( lastArray.size() == elementsPerArray )
			{
				// add another (maximum size) array
				data.add( arrayFactory.createArray( elementsPerArray, this.bytesPerElement ) );
				capacity += elementsPerArray;
			}
			else
			{
				// grow the final array
				final int lastCapacity = lastArray.size();
				if ( last > 0 )
					// if there is more than one array, grow it to max size
					lastArray.resize( lastCapacity );
				else
					// if there is only one array, double its size
					lastArray.resize( Math.min( lastCapacity << 1, elementsPerArray ) );
				capacity += lastArray.size() - lastCapacity;
			}
		}
		return index;
	}

	@Override
	public T createAccess()
	{
		return data.get( 0 ).createAccess();
	}

	@Override
	public void updateAccess( final T access, final int index )
	{
		final int i = ( int ) ( index / elementsPerArray );
		final int j = index - i * elementsPerArray;
		data.get( i ).updateAccess( access, j );
	}

	@Override
	public void swap( final int index0, final int index1 )
	{
		final int i0 = ( int ) ( index0 / elementsPerArray );
		final int j0 = index0 - i0 * elementsPerArray;
		final int i1 = ( int ) ( index1 / elementsPerArray );
		final int j1 = index1 - i1 * elementsPerArray;
		data.get( i0 ).swapElement( j0, data.get( i1 ), j1 );
	}

	/**
	 * Creates a factory for {@link MultiArrayMemPool}s that use the specified
	 * {@code arrayFactory} for creating their storage
	 * {@link MappedElementArray}.
	 *
	 * @param arrayFactory
	 *            the array factory.
	 * @return a new factory that can create {@link MemPool}.
	 *
	 * @param <T>
	 *            the {@link MappedElement} type stored in the pool.
	 * @param <A>
	 *            the type of the primitive array used in the
	 *            {@link MappedElementArray}.
	 */
	public static < A extends MappedElementArray< A, T >, T extends MappedElement >
			MemPool.Factory< T > factory( final MappedElementArray.Factory< A > arrayFactory )
	{
		return new MemPool.Factory< T >()
		{
			@Override
			public MemPool< T > createPool( final int capacity, final int bytesPerElement )
			{
				return new MultiArrayMemPool<>( arrayFactory, capacity, bytesPerElement );
			}
		};
	}
}
