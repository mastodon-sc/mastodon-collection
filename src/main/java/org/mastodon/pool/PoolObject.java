package org.mastodon.pool;

import org.mastodon.Ref;

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
 *            recursive type of this {@link PoolObject}.
 * @param <T>
 *            the MappedElement type, for example {@link ByteMappedElement}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public abstract class PoolObject< O extends PoolObject< O, P, T >, P extends Pool< O, T >, T extends MappedElement > implements Ref< O >
{
	/**
	 * Access to the data.
	 */
	protected final T access;

	/**
	 * Current index (of the access) in the {@link MemPool}.
	 */
	private int index;

	/**
	 * The {@link MemPool} into which this proxy currently refers.
	 */
	private MemPool< T > memPool;

	/**
	 * The {@link Pool} that created this {@link PoolObject}.
	 * This is used only to forward {@link #releaseRef()} to the creating {@link Pool}.
	 */
	protected final P pool;

	/**
	 * Create a {@link PoolObject} referring data in the given {@link Pool}.
	 * The element that it references to can be set by
	 * {@link #updateAccess(MemPool, int)}.
	 */
	protected PoolObject( final P pool )
	{
		this.pool = pool;
		this.memPool = pool.getMemPool();
		this.access = memPool.createAccess();
	}

	@Override
	public int getInternalPoolIndex()
	{
		return index;
	}

	/**
	 * When creating new elements (see {@link Pool#create(PoolObject)}, they
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
	 * @param memPool
	 * @param index
	 */
	void updateAccess( final MemPool< T > memPool, final int index )
	{
		this.memPool = memPool;
		this.index = index;
		memPool.updateAccess( access, index );
	}

	/**
	 * Make this proxy refer the element at the specified {@code index} in the
	 * current {@link #memPool}.
	 *
	 * @param index
	 */
	// TODO: go through updateAccess( pool, index ) uses an find out, when this one can be used instead.
	void updateAccess( final int index )
	{
		this.index = index;
		memPool.updateAccess( access, index );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public O refTo( final O obj )
	{
		final PoolObject< ?, ?, T > other = obj;
		if ( other.pool != pool )
			throw new IllegalArgumentException( "Cannot point a proxy to an object from a different pool" );
		updateAccess( other.memPool, other.index );
		return ( O ) this;
	}

	/**
	 * Make the {@link Pool} that created this proxy {@link Pool#releaseRef(PoolObject) release} it.
	 */
	@SuppressWarnings( "unchecked" )
	void releaseRef()
	{
		pool.releaseRef( ( O ) this );
	}

	/**
	 * A factory for {@link PoolObject}s of type {@code O}.
	 *
	 * @param <O>
	 *            a {@link PoolObject} type.
	 * @param <T>
	 *            the MappedElement type of the {@link PoolObject}, for example
	 *            {@link ByteMappedElement}.
	 */
	public static interface Factory< O extends PoolObject< O, ?, T >, T extends MappedElement >
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
		return obj instanceof PoolObject< ?, ?, ? > &&
				access.equals( ( ( PoolObject< ?, ?, ? > ) obj ).access );
	}

	@Override
	public int hashCode()
	{
		return access.hashCode();
	}
}
