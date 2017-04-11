/**
 *
 */
package org.mastodon.properties.undo;

import org.mastodon.properties.IntPropertyMap;

import gnu.trove.list.array.TIntArrayList;

/**
 * A {@link PropertyUndoRedoStack} to record {@link IntPropertyMap} changes.
 *
 * @author Tobias Pietzsch
 */
public class IntPropertyUndoRedoStack< O > implements PropertyUndoRedoStack< O >
{
	private final IntPropertyMap< O > property;

	private final int noEntryValue;

	private final TIntArrayList stack;

	private int top;

	private int end;

	public IntPropertyUndoRedoStack( final IntPropertyMap< O > property )
	{
		this.property = property;
		noEntryValue = property.getNoEntryValue();
		stack = new TIntArrayList();
		top = 0;
		end = 0;
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
		if ( top < stack.size() )
			stack.set( top, property.getInt( obj ) );
		else
			stack.add( property.getInt( obj ) );
		end = ++top;
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
		if ( top > 0 )
		{
			--top;
			swap( obj );
		}
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
		if ( top < end )
		{
			swap( obj );
			++top;
		}
	}

	/**
	 * Replace the element at {@code top} with the property value of {@code obj}.
	 *
	 * @param obj
	 */
	private void swap( final O obj )
	{
		final int stackValue = stack.getQuick( top );
		int value = noEntryValue;
		if ( stackValue != noEntryValue )
			value = property.set( obj, stackValue );
		else
			property.remove( obj );
		stack.setQuick( top, value );
	}

	/**
	 * Truncate entries starting from {@code end}.
	 */
	public void trim()
	{
		stack.remove( end, stack.size() - end );
		stack.trimToSize();
	}
}
