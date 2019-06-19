/**
 *
 */
package org.mastodon.properties.undo;

import org.mastodon.properties.DoublePropertyMap;
import org.mastodon.properties.IntPropertyMap;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * A {@link PropertyUndoRedoStack} to record {@link IntPropertyMap} changes.
 *
 * @author Tobias Pietzsch
 */
public class DoublePropertyUndoRedoStack< O > implements PropertyUndoRedoStack< O >
{
	private final DoublePropertyMap< O > property;

	private final double noEntryValue;

	private final TDoubleArrayList stack;

	private int top;

	private int end;

	public DoublePropertyUndoRedoStack( final DoublePropertyMap< O > property )
	{
		this.property = property;
		noEntryValue = property.getNoEntryValue();
		stack = new TDoubleArrayList();
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
			stack.set( top, property.getDouble( obj ) );
		else
			stack.add( property.getDouble( obj ) );
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

	@Override
	public void clear()
	{
		stack.clear();
		top = 0;
		end = 0;
	}

	/**
	 * Replace the element at {@code top} with the property value of {@code obj}.
	 *
	 * @param obj
	 */
	private void swap( final O obj )
	{
		final double stackValue = stack.getQuick( top );
		final double value = ( stackValue != noEntryValue )
			? property.set( obj, stackValue )
			: property.removeDouble( obj );
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
