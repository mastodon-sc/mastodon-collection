package org.mastodon.pool;


/**
 * A {@link MemPool} that keeps data in a single {@link MappedElementArray}.
 *
 * @param <T>
 *            the {@link MappedElement} type stored in this pool.
 * @param <A>
 *            the type of the primitive array used in the
 *            {@link MappedElementArray}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class SingleArrayMemPool< A extends MappedElementArray< A, T >, T extends MappedElement > extends MemPool< T >
{
	private final A data;

	public SingleArrayMemPool( final MappedElementArray.Factory< A > arrayFactory, final long capacity, final int bytesPerElement )
	{
		super( capacity, bytesPerElement );
		data = arrayFactory.createArray( capacity, this.bytesPerElement );
		dataAccess = data.createAccess();
	}

	@Override
	protected long append()
	{
		final long index = allocatedSize++;
		if ( allocatedSize > capacity )
		{
			capacity = Math.min( capacity << 1, data.maxSize() );
			if ( allocatedSize > capacity )
				throw new IllegalArgumentException( "cannot store more than " + data.maxSize() + " elements" );
			data.resize( capacity );
		}
		return index;
	}

	@Override
	public T createAccess()
	{
		return data.createAccess();
	}

	@Override
	public void updateAccess( final T access, final long index )
	{
		data.updateAccess( access, index );
	}

	@Override
	public void swap( final long index0, final long index1 )
	{
		data.swapElement( index0, data, index1 );
	}

	/**
	 * <b>For internal use only!</b>
	 *
	 * @return the data array used in this class.
	 */
	public A getDataArray()
	{
		return data;
	}

	/**
	 * Creates a factory for {@link SingleArrayMemPool}s that use the specified
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
			public MemPool< T > createPool( final long capacity, final int bytesPerElement )
			{
				return new SingleArrayMemPool<>( arrayFactory, capacity, bytesPerElement );
			}
		};
	}
}
