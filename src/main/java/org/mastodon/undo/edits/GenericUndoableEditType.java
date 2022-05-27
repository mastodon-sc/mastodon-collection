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
package org.mastodon.undo.edits;

import org.mastodon.undo.AbstractUndoableEditType;
import org.mastodon.undo.Recorder;
import org.mastodon.undo.UndoRedoStack;
import org.mastodon.undo.UndoableEdit;
import org.mastodon.undo.UndoableEditUndoRedoStack;

public class GenericUndoableEditType< E extends UndoableEdit > extends AbstractUndoableEditType implements Recorder< E >
{
	private final UndoableEditUndoRedoStack edits;

	public GenericUndoableEditType( final UndoRedoStack undoRedoStack )
	{
		this( new UndoableEditUndoRedoStack(), undoRedoStack );
	}

	public GenericUndoableEditType( final UndoableEditUndoRedoStack edits, final UndoRedoStack undoRedoStack )
	{
		super( undoRedoStack );
		this.edits = edits;
	}

	@Override
	public void record( final E edit )
	{
		recordType();
		edits.record( edit );
	}

	@Override
	public void undo()
	{
		edits.undo();
	}

	@Override
	public void redo()
	{
		edits.redo();
	}
}
