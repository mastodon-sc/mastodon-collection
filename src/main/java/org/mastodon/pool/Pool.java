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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.mastodon.Options;
import org.mastodon.RefPool;
import org.mastodon.pool.MemPool.PoolIterator;
import org.mastodon.properties.HasPropertyMaps;
import org.mastodon.properties.PropertyMap;
import org.mastodon.properties.PropertyMaps;

/**
 * A pool of {@link PoolObject PoolObjects} all stored in a common
 * {@link MemPool}. Provides methods to {@link #createRef() create} and
 * {@link #releaseRef(PoolObject) release} proxy objects.
 * The pool can be {@link #iterator() iterated}.
 *
 * @param <O>
 *            type of {@link PoolObject} stored in this {@link Pool}.
 * @param <T>
 *            the MappedElement type of the {@link PoolObject}, for example
 *            {@link ByteMappedElement}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
@SuppressWarnings( "unused" )
public abstract class Pool< O extends PoolObject< O, ?, T >, T extends MappedElement > implements RefPool< O >, Iterable< O >, HasPropertyMaps< O >
{
	private final Class< O > poolObjectClass;

	private final MemPool< T > memPool;

	private final ThreadLocalSoftReferencePool< O > tmpObjRefs;

	private final PoolCollectionWrapper< O > asRefCollection;

	private final PropertyMaps< O > propertyMaps;

	protected final Properties< O > properties;

	public Pool(
			final int initialCapacity,
			final PoolObjectLayout poolObjectLayout,
			final Class< O > poolObjectClass,
			final MemPool.Factory< T > memPoolFactory )
	{
		this( initialCapacity, poolObjectLayout, poolObjectClass, memPoolFactory, MemPool.FreeElementPolicy.CHECK_MAGIC_NUMBER );
	}

	public Pool(
			final int initialCapacity,
			final PoolObjectLayout poolObjectLayout,
			final Class< O > poolObjectClass,
			final MemPool.Factory< T > memPoolFactory,
			final MemPool.FreeElementPolicy freeElementPolicy )
	{
		this.poolObjectClass = poolObjectClass;
		this.memPool = memPoolFactory.createPool( initialCapacity, poolObjectLayout.getSizeInBytes(), freeElementPolicy );
		this.tmpObjRefs = new ThreadLocalSoftReferencePool<>();
		this.asRefCollection = new PoolCollectionWrapper<>( this );
		this.propertyMaps = new PropertyMaps<>();
		this.properties = new Properties<>();
	}

	/**
	 * Remove all objects from the pool.
	 *
	 * <p>
	 * Note, that existing proxies refer to invalid data after calling this method!
	 */
	public void clear()
	{
		propertyMaps.beforeClearPool();
		memPool.clear();
	}

	/**
	 * Returns the pool size, that is, how many objects the pool currently
	 * contains.
	 *
	 * @return the pool size.
	 */
	public int size()
	{
		return memPool.size();
	}

	@Override
	public O createRef()
	{
		return createRef( true );
	}

	public O createRef( final boolean recycle )
	{
		if ( recycle )
		{
			final O obj = tmpObjRefs.get();
			return obj == null ? createEmptyRef() : obj;
		}
		else
			return createEmptyRef();
	}

	protected abstract O createEmptyRef();

	@Override
	public void releaseRef( final O obj )
	{
		tmpObjRefs.put( obj );
	}

	@Override
	public O getObject( final int index, final O obj )
	{
		if ( Options.DEBUG )
		{
			if ( index < 0 || index >= memPool.capacity )
				throw new NoSuchElementException( "index=" + index + " capacity=" + memPool.capacity + ", refClass=" + getRefClass().getSimpleName() );
		}

		obj.updateAccess( this, index );

		if ( Options.DEBUG )
		{
			if ( memPool.isFree( obj.access, index ) )
				throw new NoSuchElementException( "index=" + index + " is free, refClass=" + getRefClass().getSimpleName() );
		}

		return obj;
	}

	@Override
	public O getObjectIfExists( final int index, final O obj )
	{
		if ( index < 0 || index >= memPool.capacity )
			return null;

		obj.updateAccess( this, index );

		if ( memPool.isFree( obj.access, index ) )
			return null;

		return obj;
	}

	@Override
	public int getId( final O o )
	{
		return o.getInternalPoolIndex();
	}

	@Override
	public Class< O > getRefClass()
	{
		return poolObjectClass;
	}

	@Override
	public Iterator< O > iterator()
	{
		return iterator( createRef() );
	}

	// garbage-free version
	public Iterator< O > iterator( final O obj )
	{
		final PoolIterator< T > pi = memPool.iterator();
		return new Iterator< O >()
		{
			@Override
			public boolean hasNext()
			{
				return pi.hasNext();
			}

			@Override
			public O next()
			{
				final int index = pi.next();
				if ( Options.DEBUG )
				{
					if ( index >= memPool.allocatedSize )
						throw new NoSuchElementException();
				}

				obj.updateAccess( Pool.this, index );
				return obj;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public PropertyMaps< O > getPropertyMaps()
	{
		return propertyMaps;
	}

	/**
	 * Attributes and "permanent" {@link PropertyMap}s of this pool.
	 *
	 * @return the properties.
	 */
	protected Properties< O > getProperties()
	{
		return properties;
	}

	/**
	 * Add a {@link PropertyMap} to list of {@link Properties}.
	 *
	 * @param propertyMap
	 *            the map to register.
	 * @see #getProperties()
	 */
	protected void registerPropertyMap( final PropertyMap< O, ? > propertyMap )
	{
		properties.add( propertyMap );
	}

	protected MemPool< T > getMemPool()
	{
		return memPool;
	}

	protected O create( final O obj )
	{
		final int index = memPool.create();
		obj.updateAccess( this, index );
		obj.setToUninitializedState();
		propertyMaps.objectCreated( obj );
		return obj;
	}

	protected void delete( final O obj )
	{
		propertyMaps.beforeDeleteObject( obj );
		memPool.free( obj.getInternalPoolIndex() );
	}

	public PoolCollectionWrapper< O > asRefCollection()
	{
		return asRefCollection;
	}
}
