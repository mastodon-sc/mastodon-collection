package org.mastodon.pool;

import org.mastodon.BigRef;

/**
 * A proxy object that uses a {@link MappedElement} access to store its data in
 * a {@link MemPool}. The data block that it references to can be set by
 * <code>updateAccess(MemPool, int)</code>. Methods to modify the data itself
 * are defined in subclasses.
 *
 * <p>
 * In principle, this could extend {@link MappedElement}, but we rather use
 * composition to hide {@link MappedElement} methods from users.
 *
 * @param <O>
 *            recursive type of this {@link BigPoolObject}.
 * @param <T>
 *            the MappedElement type, for example {@link ByteMappedElement}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public abstract class BigPoolObject< O extends BigPoolObject< O, T >, T extends MappedElement > implements BigRef< O >
{
	/**
	 * Access to the data.
	 */
	protected final T access;

	/**
	 * Current index (of the access) in the {@link MemPool}.
	 */
	private long index;

	/**
	 * The {@link MemPool} into which this proxy currently refers.
	 */
	private MemPool< T > memPool;

	/**
	 * The {@link Pool} that created this {@link BigPoolObject}.
	 * This is used only to forward {@link #releaseRef()} to the creating {@link Pool}.
	 */
	final BigPool< O, T > creatingPool;

	/**
	 * Create a {@link BigPoolObject} referring data in the given {@link MemPool}.
	 * The element that it references to can be set by
	 * {@link #updateAccess(MemPool, int)}.
	 *
	 * @param pool
	 *            the {@link MemPool} where derived classes store their data.
	 */
	protected BigPoolObject( final BigPool< O, T > pool )
	{
		this.creatingPool = pool;
		this.memPool = pool.getMemPool();
		this.access = memPool.createAccess();
	}

	@Override
	public long getInternalPoolIndex()
	{
		return index;
	}

	/**
	 * When creating new elements (see {@link Pool#create(BigPoolObject)}, they
	 * might reuse storage that was occupied by previously freed elements. This
	 * is overridden by subclasses to set (some, important) fields to their
	 * initial state. An example are indices used to start linked lists in
	 * {@code AbstractEdge}.
	 */
	protected abstract void setToUninitializedState();

	/**
	 * Make this proxy refer the element at the specified {@code index} in the
	 * specified {@code pool}.
	 *
	 * @param pool
	 * @param index
	 */
	void updateAccess( final MemPool< T > pool, final long index )
	{
		this.memPool = pool;
		this.index = index;
		pool.updateAccess( access, index );
	}

	/**
	 * Make this proxy refer the element at the specified {@code index} in the
	 * current {@link #memPool}.
	 *
	 * @param index
	 */
	// TODO: go through updateAccess( pool, index ) uses an find out, when this one can be used instead.
	void updateAccess( final long index )
	{
		this.index = index;
		memPool.updateAccess( access, index );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public O refTo( final O obj )
	{
		updateAccess( ( ( BigPoolObject< ?, T > ) obj ).memPool, ( ( BigPoolObject< ?, T > ) obj ).index );
		return ( O ) this;
	}

	/**
	 * Make the {@link Pool} that created this proxy {@link Pool#releaseRef(BigPoolObject) release} it.
	 */
	@SuppressWarnings( "unchecked" )
	void releaseRef()
	{
		creatingPool.releaseRef( ( O ) this );
	}

	/**
	 * A factory for {@link BigPoolObject}s of type {@code O}.
	 *
	 * @param <O>
	 *            a {@link BigPoolObject} type.
	 * @param <T>
	 *            the MappedElement type of the {@link BigPoolObject}, for example
	 *            {@link ByteMappedElement}.
	 */
	public static interface Factory< O extends BigPoolObject< O, T >, T extends MappedElement >
	{
		public int getSizeInBytes();

		// TODO: rename to createRef()?
		public O createEmptyRef();

		public MemPool.Factory< T > getMemPoolFactory();

		public Class< O > getRefClass();
	}

	@Override
	public boolean equals( final Object obj )
	{
		return obj instanceof BigPoolObject< ?, ? > &&
				access.equals( ( ( BigPoolObject< ?, ? > ) obj ).access );
	}

	@Override
	public int hashCode()
	{
		return access.hashCode();
	}
}
