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
 * @param <P>
 *            the type of the pool.
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
	 * The {@link Pool} into which this proxy currently refers.
	 */
	protected P pool;

	/**
	 * Creates a {@link PoolObject} referring data in the given {@link Pool}.
	 * The element that it references to can be set by
	 * {@link #updateAccess(Pool, int)}.
	 *
	 * @param pool
	 *            the pool to refer to.
	 */
	protected PoolObject( final P pool )
	{
		this.pool = pool;
		this.access = pool.getMemPool().createAccess();
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
	 * @param pool
	 * @param index
	 */
	@SuppressWarnings( "unchecked" )
	void updateAccess( final Pool< O, T > pool, final int index )
	{
		if ( this.pool != pool )
			this.pool = ( P ) pool;
		this.index = index;
		this.pool.getMemPool().updateAccess( access, index );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public O refTo( final O obj )
	{
		final PoolObject< ?, P, T > other = obj;
		updateAccess( other.pool, other.index );
		return ( O ) this;
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
