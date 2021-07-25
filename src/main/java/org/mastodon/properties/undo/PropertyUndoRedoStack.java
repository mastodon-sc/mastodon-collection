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
package org.mastodon.properties.undo;

import org.mastodon.properties.PropertyMap;

/**
 * A Undo/Redo stack that can be used to record changes to {@link PropertyMap}s.
 * <p>
 * In principle it is an expandable array of elements with a {@code top}
 * pointer/index. This is not really a stack: {@code top} might point to
 * somewhere in the middle of the array, i.e., elements above the top may be
 * retained. Basically, {@code top} points to where the next action would be
 * recorded, and/or where the next (previously recorded and undone) action for
 * redo is stored.
 * </p>
 *
 * @param <O>
 *            the type of object whose property is stored on the stack.
 *
 * @author Tobias Pietzsch
 */
public interface PropertyUndoRedoStack< O >
{
	/**
	 * Put the property value of {@code obj} at the top of the stack, expanding
	 * the stack if necessary. Increment top. Clear any elements at top and
	 * beyond (optional).
	 *
	 * @param obj
	 *            holder of the property value to push
	 */
	public void record( final O obj );

	/**
	 * Decrement {@code top}. Then replace the element there with the property
	 * value of {@code obj}. Set the previously stored element as the property
	 * value of {@code obj}.
	 *
	 * @param obj
	 *            object whose property value to swap with the element at
	 *            {@code top-1}.
	 */
	public void undo( final O obj );

	/**
	 * Replace the element at {@code top} with the property value of {@code obj}.
	 * Set the previously stored element as the property value of {@code obj}.
	 * Then increment {@code top}.
	 *
	 * @param obj
	 *            object whose property value to swap with the element at
	 *            {@code top}.
	 */
	public void redo( final O obj );

	public void clear();
}
