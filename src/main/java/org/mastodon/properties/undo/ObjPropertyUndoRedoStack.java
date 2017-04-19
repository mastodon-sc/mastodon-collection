/**
 *
 */
package org.mastodon.properties.undo;

import java.util.ArrayList;

import org.mastodon.properties.ObjPropertyMap;

/**
 * A {@link PropertyUndoRedoStack} to record {@link ObjPropertyMap} changes.
 *
 * @author Tobias Pietzsch
 */
public class ObjPropertyUndoRedoStack< O, T > implements PropertyUndoRedoStack< O >
{
	private final ObjPropertyMap< O, T > property;

	private final ArrayList< T > stack;

	private int top;

	private int end;

	public ObjPropertyUndoRedoStack( final ObjPropertyMap< O, T > property )
	{
		this.property = property;
		stack = new ArrayList<>();
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
			stack.set( top, property.get( obj ) );
		else
			stack.add( property.get( obj ) );
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
		final T stackValue = stack.get( top );
		final T value = ( stackValue != null )
				? property.set( obj, stackValue )
				: property.remove( obj );
		stack.set( top, value );
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
}
