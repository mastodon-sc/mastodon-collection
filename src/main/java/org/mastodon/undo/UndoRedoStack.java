/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.undo;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

/**
 * A undo/redo stack to record undoable edits. Basically, this is an expandable
 * array of elements with a {@code top} pointer. (Not really a stack because
 * elements above the {@code top} may be retained.)
 * <p>
 * {@link UndoRedoStack} simply records the type of each new edit, and
 * whether it is an <em>undo point</em> The data required to undo/redo the new
 * edit is stored in separate undo/redo stacks that are members of the specific
 * {@link AbstractUndoableEditType} instances.
 * </p>
 *
 * @author Tobias Pietzsch
 */
public class UndoRedoStack implements UndoPointMarker
{
	private final ArrayList< AbstractUndoableEditType > editTypes = new ArrayList<>();

	private final Stack stack;

	private final Access ref;

	private int top;

	private int end;

	private final ConcurrentLinkedQueue< Element > tmpObjRefs;

	private int savePointIndex = -1;

	public UndoRedoStack( final int initialCapacity )
	{
		stack = new ByteStack( initialCapacity );
		ref = stack.createRef();
		top = 0;
		end = 0;
		tmpObjRefs = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Put a new element of the specified {@code type} at the top of the stack,
	 * expanding the stack if necessary. Then increment top.
	 *
	 * @param type
	 *            type of the new element to push
	 */
	public void record( final AbstractUndoableEditType type )
	{
//		System.out.println( "UndoRedoStack.record( " + type + " )" );
//		final Access ref = stack.createRef();
		if ( top >= stack.size() )
			stack.addElement();
		ref.setIndex( top++ );
		ref.setValue( type.typeIndex(), false );
		end = top;
//		stack.releaseRef( ref );
	}

	@Override
	public void setUndoPoint()
	{
//		final Access ref = stack.createRef();
		if ( top > 0 )
		{
			ref.setIndex( top - 1 );
			ref.setUndoPoint( true );
		}
//		stack.releaseRef( ref );
	}

	public void setSavePoint()
	{
		savePointIndex = top;
	}

	public boolean isSavePoint()
	{
		return savePointIndex == top;
	}

	/**
	 * Undo until the next undo-point.
	 * <p>
	 * Decrement top. Then undo the element at top. Repeat while the element
	 * below top is not marked as an undo-point.
	 * </p>
	 */
	public void undo()
	{
		/*
		 * Kill save-point. If the user saves, make 1 undo, then make one
		 * change, the 'top' value will be at the savePointIndex, but the model
		 * would have changed. This prevents retrieving the save state in case
		 * we do undo/redo in succession, but this is life.
		 */
		savePointIndex = -1;

//		final Access ref = stack.createRef();
		boolean first = true;
		for ( int i = top - 1; i >= 0; --i )
		{
			ref.setIndex( i );
			if ( ref.isUndoPoint() && !first )
				break;
			ref.getType().undo();
			--top;
			first = false;
		}
//		stack.releaseRef( ref );
	}

	/**
	 * Redo until the next undo-point.
	 * <p>
	 * Redo the element at top. Then increment top. Repeat while the element at
	 * the top is not marked as an undo-point.
	 * </p>
	 */
	public void redo()
	{
//		final Access ref = stack.createRef();
		for ( int i = top; i < end; ++i )
		{
			ref.setIndex( i );
			ref.getType().redo();
			++top;
			if ( ref.isUndoPoint() )
				break;
		}
//		stack.releaseRef( ref );
	}

	/**
	 * Truncate entries starting from {@code end}.
	 */
	public void trim()
	{
		stack.remove( end, stack.size() - end );
		stack.trimToSize();
	}

	/**
	 * Remove all entries.
	 */
	public void clear()
	{
		stack.clear();
	}

	/**
	 * Read-only access to an element on this {@link UndoRedoStack}.
	 */
	public class Element
	{
		private final Access ref;

		Element( final Access ref )
		{
			this.ref = ref;
		}

		public AbstractUndoableEditType getType()
		{
			return ref.getType();
		}

		public boolean isUndoPoint()
		{
			return ref.isUndoPoint();
		}
	}

	public Element createRef()
	{
		final Element ref = tmpObjRefs.poll();
		return ref == null ? new Element( stack.createRef() ) : ref;
	}

	public void releaseRef( final Element ref )
	{
		tmpObjRefs.add( ref );
	}

	/**
	 * Return the element at {@code top - 1}, i.e., the last recorded element.
	 *
	 * @param ref
	 *            a reusable {@link Element} ref (will be used as return value).
	 * @return the element at {@code top - 1} or {@code null} if the stack is empty.
	 */
	public Element peek( final Element ref )
	{
		if ( top <= 0 )
			return null;
		ref.ref.setIndex( top - 1 );
		return ref;
	}

//	/**
//	 * Decrement top. Then return the element at top.
//	 */
//	protected Access undo( final Access ref )
//	{
//		if ( top <= 0 )
//			return null;
//		ref.setIndex( --top );
//		return ref;
//	}
//
//	/**
//	 * Return the element at top. Then increment top.
//	 */
//	protected Access redo( final Access ref )
//	{
//		if ( top >= end )
//			return null;
//		ref.setIndex( top++ );
//		return ref;
//	}

	/**
	 * This is called automatically when instantiating derived {@link AbstractUndoableEditType}s.
	 */
	int addEditType( final AbstractUndoableEditType editType )
	{
		int index;
		synchronized ( editTypes )
		{
			index = editTypes.size();
			editTypes.add( editType );
		}
		if ( index > stack.maxEditTypeIndex() )
			throw new IllegalStateException( "Too many edit types. Time to implement ShortAccess..." );
		return index;
	}

	interface Access
	{
		/**
		 * Point this {@link Access} to the specified index of the
		 * {@link Stack}. Do not confuse this with {@link #setTypeIndex(int)}!
		 * 
		 * @param index
		 *            the index.
		 */
		void setIndex( int index );

		void setValue( final int typeIndex, final boolean isUndoPoint );

		AbstractUndoableEditType getType();

		int getTypeIndex();

		void setTypeIndex( final int typeIndex );

		boolean isUndoPoint();

		void setUndoPoint( final boolean isUndoPoint );
	}

	interface Stack
	{
		Access createRef();

		void releaseRef( Access ref );

		int size();

		void addElement();

		void remove( int offset, int length );

		void trimToSize();

		void clear();

		int maxEditTypeIndex();
	}

	class ByteAccess implements Access
	{
		private int index;

		private final TByteList buf;

		ByteAccess( final TByteList buf )
		{
			this.buf = buf;
		}

		@Override
		public void setIndex( final int index )
		{
			this.index = index;
		}

		@Override
		public void setValue( final int typeIndex, final boolean isUndoPoint )
		{
			buf.set( index, ( byte ) ( isUndoPoint ? ( typeIndex | 0x80 ) : typeIndex ) );
		}

		@Override
		public AbstractUndoableEditType getType()
		{
			return editTypes.get( getTypeIndex() );
		}

		@Override
		public int getTypeIndex()
		{
			return buf.get( index ) & 0x7F;
		}

		@Override
		public void setTypeIndex( final int typeIndex )
		{
			setValue( typeIndex, isUndoPoint() );
		}

		@Override
		public boolean isUndoPoint()
		{
			return buf.get( index ) < 0;
		}

		@Override
		public void setUndoPoint( final boolean isUndoPoint )
		{
			setValue( getTypeIndex(), isUndoPoint );
		}
	}

	class ByteStack implements Stack
	{
		private final TByteArrayList buf;

		private final ConcurrentLinkedQueue< Access > tmpObjRefs;

		ByteStack( final int initialCapacity )
		{
			buf = new TByteArrayList( initialCapacity );
			tmpObjRefs = new ConcurrentLinkedQueue<>();
		}

		@Override
		public Access createRef()
		{
			final Access access = tmpObjRefs.poll();
			return access == null ? new ByteAccess( buf ) : access;
		}

		@Override
		public void releaseRef( final Access access )
		{
			tmpObjRefs.add( access );
		}

		@Override
		public int size()
		{
			return buf.size();
		}

		@Override
		public void addElement()
		{
			buf.add( ( byte ) 0 );
		}

		@Override
		public void trimToSize()
		{
			buf.trimToSize();
		}

		@Override
		public void clear()
		{
			buf.clear();
		}

		@Override
		public void remove( final int offset, final int length )
		{
			buf.remove( offset, length );
		}

		@Override
		public int maxEditTypeIndex()
		{
			return 127;
		}
	}

//class ShortAccess implements Access
//{
//	TODO
//}

//class ShortStack implements Stack
//{
//	TODO
//}
}
