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
/**
 *
 */
package org.mastodon.pool;

import org.mastodon.properties.IntPropertyMap;
import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.mastodon.undo.ByteArrayUndoRedoStack;
import org.mastodon.undo.ByteArrayUndoRedoStack.ByteArrayRef;

/**
 * A {@link PropertyUndoRedoStack} to record {@link IntPropertyMap} changes.
 * 
 * @param <O>
 *            the type of object this attribute is defined for.
 * @author Tobias Pietzsch
 */
public class AttributeUndoRedoStack< O extends PoolObject< O, ?, ? > > implements PropertyUndoRedoStack< O >
{
	private final AbstractAttribute< O > attribute;

	private final ByteArrayUndoRedoStack stack;

	private final ByteArrayRef ref;

	private final byte[] data;

	private final byte[] swapdata;

	private final int size;

	private final int offset;

	public AttributeUndoRedoStack( final AbstractAttribute< O > attribute )
	{
		this.attribute = attribute;
		offset = attribute.field.getOffset();
		size = attribute.field.getSizeInBytes();
		stack = new ByteArrayUndoRedoStack( 1024 * size );
		ref = stack.createRef();
		data = new byte[ size ];
		swapdata = new byte[ size ];
	}

	/**
	 * Put the property value of {@code obj} at the top of the stack, expanding
	 * the stack if necessary. Increment top.
	 *
	 * @param obj
	 *            holder of the property value to push
	 */
	@Override
	public void record( final O obj )
	{
		final ByteArrayRef buffer = stack.record( size, ref );
		attribute.access( obj ).getBytes( data, offset );
		buffer.putBytes( 0, data );
	}

	/**
	 * Decrement {@code top}. Then replace the element there with the property
	 * value of {@code obj}. Set the previously stored element as the property
	 * value of {@code obj}.
	 *
	 * @param obj
	 *            object whose property value to swap with the element at
	 *            {@code top-1}.
	 */
	@Override
	public void undo( final O obj )
	{
		swap( obj, stack.undo( size, ref ) );
	}

	/**
	 * Replace the element at {@code top} with the property value of {@code obj}.
	 * Set the previously stored element as the property value of {@code obj}.
	 * Then increment {@code top}.
	 *
	 * @param obj
	 *            object whose property value to swap with the element at
	 *            {@code top}.
	 */
	@Override
	public void redo( final O obj )
	{
		swap( obj, stack.redo( size, ref ) );
	}

	@Override
	public void clear()
	{
		stack.clear();
	}

	/**
	 * Replace the element at {@code top} with the property value of {@code obj}.
	 *
	 * @param obj
	 */
	private void swap( final O obj, final ByteArrayRef buffer )
	{
		attribute.notifyBeforePropertyChange( obj );
		attribute.access( obj ).getBytes( swapdata, offset );
		buffer.getBytes( 0, data );
		buffer.putBytes( 0, swapdata );
		attribute.access( obj ).putBytes( data, offset );
		attribute.notifyPropertyChanged( obj );
	}
}
