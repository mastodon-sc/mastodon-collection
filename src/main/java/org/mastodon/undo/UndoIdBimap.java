/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.undo;

import org.mastodon.RefPool;

/**
 * Bidirectional map that links objects and undo IDs.
 *
 * @param <O>
 *            the object type
 *
 * @author Tobias Pietzsch
 */
public class UndoIdBimap< O > implements RefPool< O >
{
	/** Value used to declare that the requested value is not in the map. */
	public static final int NO_ENTRY_VALUE = -1;

	private final RefPool< O > pool;

	private final IntPropertyBimap< O > objUndoIdBimap;

	private int idgen;

	/**
	 * Create a bidirectional object - int map that links objects of the
	 * specified object pool to undo IDs.
	 *
	 * @param pool
	 *            the object pool.
	 */
	public UndoIdBimap( final RefPool< O > pool )
	{
		this.pool = pool;
		objUndoIdBimap = new IntPropertyBimap<>( pool, NO_ENTRY_VALUE );
		idgen = 0;
	}

	/**
	 * Removes all mappings.
	 * Resets the undo ID generator.
	 */
	public synchronized void clear()
	{
		objUndoIdBimap.clear();
		idgen = 0;
	}

	/**
	 * Returns the undo ID for the specified object,
	 * <p>
	 * Creates new undo ID if {@code o} is not in map yet.
	 *
	 * @param o
	 *            the object.
	 * @return its undo ID.
	 */
	@Override
	public synchronized int getId( final O o )
	{
		int id = objUndoIdBimap.getValue( o );
		if ( id == NO_ENTRY_VALUE )
		{
			id = idgen++;
			objUndoIdBimap.set( o, id );
		}
		return id;
	}

	/**
	 * Stores the specified undo ID for the specified object.
	 *
	 * @param o
	 *            the object.
	 * @param id
	 *            the undo ID.
	 */
	public synchronized void put( final O o, final int id )
	{
		objUndoIdBimap.set( o, id );
	}

	/**
	 * Returns the object mapped to the specified undo ID.
	 *
	 * @param undoId
	 *            the undo ID.
	 * @param ref
	 *            a pool reference that might be used for object retrieval.
	 * @return the object mapped to the specified undo ID, or <code>null</code>
	 *         is there are no such undo ID stored in this map.
	 */
	@Override
	public O getObject( final int undoId, final O ref )
	{
		return objUndoIdBimap.getKey( undoId, ref );
	}

	/**
	 * Returns the object mapped to the specified undo ID.
	 *
	 * @param undoId
	 *            the undo ID.
	 * @param ref
	 *            a pool reference that might be used for object retrieval.
	 * @return the object mapped to the specified undo ID, or <code>null</code>
	 *         is there are no such undo ID stored in this map.
	 */
	@Override
	public O getObjectIfExists( final int undoId, final O ref )
	{
		return objUndoIdBimap.getKey( undoId, ref );
	}

	@Override
	public O createRef()
	{
		return pool.createRef();
	}

	@Override
	public void releaseRef( final O ref )
	{
		pool.releaseRef( ref );
	}

	@Override
	public Class< O > getRefClass()
	{
		return pool.getRefClass();
	}
}
