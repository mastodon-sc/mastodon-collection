/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.undo.edits;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.mastodon.undo.AbstractUndoableEditType;
import org.mastodon.undo.ByteArrayUndoRedoStack;
import org.mastodon.undo.ByteArrayUndoRedoStack.ByteArrayRef;
import org.mastodon.undo.Recorder;
import org.mastodon.undo.UndoIdBimap;
import org.mastodon.undo.UndoRedoStack;
import org.mastodon.undo.UndoRedoStack.Element;

public class SetPropertyType< O > extends AbstractUndoableEditType implements Recorder< O >
{
	private final PropertyUndoRedoStack< O > propertyUndoRedoStack;

	private final UndoIdBimap< O > undoIdBimap;

	private final ByteArrayUndoRedoStack dataStack;

	private final ByteArrayRef ref;

	private final Element elmtRef;

	private final static int OBJ_ID_OFFSET = 0;
	private final static int SIZE = OBJ_ID_OFFSET + INT_SIZE;

	public SetPropertyType(
			final PropertyUndoRedoStack< O > propertyUndoRedoStack,
			final UndoIdBimap< O > undoIdBimap,
			final ByteArrayUndoRedoStack dataStack,
			final UndoRedoStack undoRedoStack )
	{
		super( undoRedoStack );
		this.propertyUndoRedoStack = propertyUndoRedoStack;
		this.undoIdBimap = undoIdBimap;
		this.dataStack = dataStack;
		ref = dataStack.createRef();
		elmtRef = undoRedoStack.createRef();
	}

	@Override
	public void record( final O obj )
	{
		final int oi = undoIdBimap.getId( obj );

		final Element peek = undoRedoStack.peek( elmtRef );
		if ( peek != null && peek.isUndoPoint() == false && peek.getType() == this )
		{
			final ByteArrayRef buffer = dataStack.peek( SIZE, ref );
			if ( buffer != null && buffer.getInt( OBJ_ID_OFFSET ) == oi )
				return; // fuse with previous edit (of same type and object)
		}

		recordType();
		final ByteArrayRef buffer = dataStack.record( SIZE, ref );
		buffer.putInt( OBJ_ID_OFFSET, oi );
		propertyUndoRedoStack.record( obj );
	}

	@Override
	public void undo()
	{
		final O oref = undoIdBimap.createRef();
		final ByteArrayRef buffer = dataStack.undo( SIZE, ref );
		final int oi = buffer.getInt( OBJ_ID_OFFSET );
		final O obj = undoIdBimap.getObject( oi, oref );
		propertyUndoRedoStack.undo( obj );
		undoIdBimap.releaseRef( oref );
	}

	@Override
	public void redo()
	{
		final O oref = undoIdBimap.createRef();
		final ByteArrayRef buffer = dataStack.redo( SIZE, ref );
		final int oi = buffer.getInt( OBJ_ID_OFFSET );
		final O obj = undoIdBimap.getObject( oi, oref );
		propertyUndoRedoStack.redo( obj );
		undoIdBimap.releaseRef( oref );
	}
}
