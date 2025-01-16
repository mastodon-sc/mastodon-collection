/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.collection;

import org.mastodon.pool.PoolObject;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;

/**
 * Interface for maps that associate a key to a simple {@code double} value.
 * <p>
 * This interface and its implementations exist to take advantage of the compact
 * data storage offered on one side by the Trove library for standard object,
 * and of a derivative of a Trove class to deal specifically and advantageously
 * with {@link PoolObject}s. Here, we therefore decorate the Trove mother
 * interface with extra methods that accept an existing Ref object to control
 * garbage collection.
 *
 * @author Jean-Yves Tinevez&lt;jeanyves.tinevez@gmail.com&gt;
 *
 * @param <K>
 *            the type of the keys of the map.
 */
public interface RefDoubleMap< K > extends TObjectDoubleMap< K >
{
	/**
	 * Creates an object reference that can be used for processing with this
	 * map. Depending on concrete implementation, the object return can be
	 * {@code null}.
	 *
	 * @return a new object empty reference.
	 */
	public K createRef();

	/**
	 * Releases a previously created object reference. For standard object maps,
	 * this method does nothing.
	 *
	 * @param obj
	 *            the object reference to release.
	 */
	public void releaseRef( final K obj );

	/**
	 * Executes <tt>procedure</tt> for each key in the map.
	 *
	 * @param procedure
	 *            a {@code TIntProcedure} value.
	 * @param ref
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return {@code false} if the loop over the keys terminated because
	 *         the procedure returned false for some key.
	 */
	public boolean forEachKey( TObjectProcedure< ? super K > procedure, K ref );

	/**
	 * Executes <tt>procedure</tt> for each key/value entry in the map.
	 *
	 * @param procedure
	 *            a {@code TObjectDoubleProcedure} value.
	 * @param ref
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return {@code false} if the loop over the entries terminated because the
	 *         procedure returned false for some entry.
	 */
	public boolean forEachEntry( TObjectDoubleProcedure< ? super K > procedure, K ref );

	/**
	 * Retains only those entries in the map for which the procedure returns a
	 * true value.
	 *
	 * @param procedure
	 *            determines which entries to keep.
	 * @param ref
	 *            an object reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return {@code true} if the map was modified.
	 */
	public boolean retainEntries( TObjectDoubleProcedure< ? super K > procedure, K ref );
}
