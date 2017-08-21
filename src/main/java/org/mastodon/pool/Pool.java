package org.mastodon.pool;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public abstract class Pool< O extends PoolObject< O, ?, T >, T extends MappedElement > implements RefPool< O >, Iterable< O >, HasPropertyMaps< O >
{
	private final Class< O > poolObjectClass;

	private final MemPool< T > memPool;

	private final ConcurrentLinkedQueue< O > tmpObjRefs;

	private final PoolCollectionWrapper< O > asRefCollection;

	private final PropertyMaps< O > propertyMaps;

	protected final Properties< O > properties;

	public Pool(
			final int initialCapacity,
			final PoolObjectLayout poolObjectLayout,
			final Class< O > poolObjectClass,
			final MemPool.Factory< T > memPoolFactory )
	{
		this.poolObjectClass = poolObjectClass;
		this.memPool = memPoolFactory.createPool( initialCapacity, poolObjectLayout.getSizeInBytes() );
		this.tmpObjRefs = new ConcurrentLinkedQueue<>();
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
			final O obj = tmpObjRefs.poll();
			return obj == null ? createEmptyRef() : obj;
		}
		else
			return createEmptyRef();
	}

	protected abstract O createEmptyRef();

	@Override
	public void releaseRef( final O obj )
	{
		if ( obj.pool == this )
			tmpObjRefs.add( obj );
		else
			obj.releaseRef();
	}

	// TODO: find instances where releaseRefs( PoolObject<?> ... objs ) can be used instead of separately releasing refs (Then probably don't use it because it creates an Object array).
	public static void releaseRefs( final PoolObject< ?, ?, ? >... objs )
	{
		for ( final PoolObject< ?, ?, ? > obj : objs )
			obj.releaseRef();
	}

	@Override
	public O getObject( final int index, final O obj )
	{
		obj.updateAccess( memPool, index );
		return obj;
	}

	@Override
	public O getObjectIfExists( final int index, final O obj )
	{
		if ( index < 0 || index >= memPool.capacity )
			return null;

		obj.updateAccess( memPool, index );

		final boolean isFree = obj.access.getInt( 0 ) == MemPool.FREE_ELEMENT_MAGIC_NUMBER;
		if (isFree)
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
				obj.updateAccess( memPool, index );
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
	 */
	protected Properties< O > getProperties()
	{
		return properties;
	}

	/**
	 * Add a {@link PropertyMap} to list of {@link Properties}. (See
	 * {@link #getProperties()}).
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
		obj.updateAccess( memPool, index );
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
