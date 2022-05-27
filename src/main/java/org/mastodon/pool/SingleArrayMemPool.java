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

	public SingleArrayMemPool( final MappedElementArray.Factory< A > arrayFactory, final int capacity, final int bytesPerElement, final FreeElementPolicy freeElementPolicy )
	{
		super( capacity, bytesPerElement, freeElementPolicy );
		data = arrayFactory.createArray( capacity, this.bytesPerElement );
		dataAccess = data.createAccess();
	}

	@Override
	protected int append()
	{
		final int index = allocatedSize++;
		if ( allocatedSize > capacity )
		{
			capacity = Math.max( 1, Math.min( capacity << 1, data.maxSize() ) );
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
	public void updateAccess( final T access, final int index )
	{
		data.updateAccess( access, index );
	}

	@Override
	public void swap( final int index0, final int index1 )
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
			public MemPool< T > createPool( final int capacity, final int bytesPerElement, final FreeElementPolicy freeElementPolicy )
			{
				return new SingleArrayMemPool<>( arrayFactory, capacity, bytesPerElement, freeElementPolicy );
			}
		};
	}
}
