/**
 *
 */
package org.mastodon.properties.undo;

import org.mastodon.properties.BooleanPropertyMap;

import gnu.trove.list.array.TByteArrayList;

/**
 * A {@link PropertyUndoRedoStack} to record {@link BooleanPropertyMap} changes.
 *
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 */
public class BooleanPropertyUndoRedoStack< O > implements PropertyUndoRedoStack< O >
{
	private final BooleanPropertyMap< O > property;

	/**
	 * We use a {@code byte} array to store that state of objects. It permits us
	 * to use a negative value (-1) to signal a value not set for an object, and
	 * does not consume more memory than a {@link boolean} array.
	 */
	private final TByteArrayList stack;

	private int top;

	private int end;

	public BooleanPropertyUndoRedoStack( final BooleanPropertyMap< O > property )
	{
		this.property = property;
		stack = new TByteArrayList();
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
		final byte stackValue = property.isSet( obj )
				? ( byte ) ( property.getBoolean( obj ) ? 1 : 0 )
				: ( byte ) -1;
		if ( top < stack.size() )
			stack.set( top, stackValue );
		else
			stack.add( stackValue );
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
		final byte stackValue = stack.getQuick( top );
		if ( stackValue >= 0 )
			property.set( obj, stackValue == 1 );
		else
			property.removeBoolean( obj );
		stack.setQuick( top, stackValue );
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
