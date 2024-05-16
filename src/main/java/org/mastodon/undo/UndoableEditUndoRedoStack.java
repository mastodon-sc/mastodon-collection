/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.ArrayList;

/**
 * A Undo/Redo stack of {@link UndoableEdit}s. This is used to record
 * {@link UndoableEdit}s that are <em>not represented by pool objects</em>.
 * TODO: clarify
 * TODO: clarify
 * TODO: clarify
 * <p>
 * In principle it is an expandable array of elements with a {@code top}
 * pointer/index. This is not really a stack: {@code top} might point to
 * somewhere in the middle of the array, i.e., elements above the top may be
 * retained. Basically, {@code top} points to where the next action would be
 * recorded, and/or where the next (previously recorded and undone) action for
 * redo is stored.
 * </p>
 *
 * @author Tobias Pietzsch
 */
public class UndoableEditUndoRedoStack
{
	private final ArrayList< UndoableEdit > stack;

	private int top;

	private int end;

	public UndoableEditUndoRedoStack()
	{
		stack = new ArrayList<>();
		top = 0;
		end = 0;
	}

	/**
	 * Put {@code edit} at the top of the stack, expanding the stack if
	 * necessary. Increment top. Clear any elements at top and beyond.
	 *
	 * @param edit
	 *            the element to push
	 */
	public void record( final UndoableEdit edit )
	{
		if ( top < stack.size() )
			stack.set( top, edit );
		else
			stack.add( edit );
		end = ++top;
	}

	/**
	 * Decrement top. Then call {@link UndoableEdit#undo() undo()} for the edit
	 * stored there.
	 */
	public void undo()
	{
		if ( top > 0 )
			stack.get( --top ).undo();
	}

	/**
	 * Call {@link UndoableEdit#redo() redo()} for the edit
	 * stored at {@code top}. Then increment {@code top}.
	 */
	public void redo()
	{
		if ( top < end )
			stack.get( top++ ).redo();
	}

	/**
	 * Truncate entries starting from {@code end}.
	 */
	public void trim()
	{
		while ( stack.size() > end )
			stack.remove( end );
		stack.trimToSize();
	}

	public void clear()
	{
		stack.clear();
		top = 0;
		end = 0;
	}
}
