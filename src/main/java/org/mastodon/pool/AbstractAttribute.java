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
package org.mastodon.pool;

import org.mastodon.pool.PoolObjectLayout.PrimitiveField;
import org.mastodon.properties.AbstractProperty;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

/**
 * Makes {@link PoolObject#access} visible to subclasses.
 *
 * @param <O>
 *            the type of object this attribute is defined for.
 * @author Tobias Pietzsch
 */
public class AbstractAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractProperty< O >
	implements Attribute
{
	final PrimitiveField field;

	protected MappedElement access( final O obj )
	{
		return obj.access;
	}

	protected AbstractAttribute( final PrimitiveField field, final Pool< O, ? > pool )
	{
		this.field = field;
		pool.getProperties().add( this );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return new AttributeUndoRedoStack<>( this );
	}

	@Override
	public boolean isSet( final O key )
	{
		return true;
	}

	@Override
	protected void notifyBeforePropertyChange( final O object )
	{
		super.notifyBeforePropertyChange( object );
	}

	@Override
	protected void notifyPropertyChanged( final O object )
	{
		super.notifyPropertyChanged( object );
	}
}
