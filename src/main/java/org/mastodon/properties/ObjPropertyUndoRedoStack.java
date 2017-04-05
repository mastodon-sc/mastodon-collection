/**
 *
 */
package org.mastodon.properties;

import java.util.ArrayList;

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

	public ObjPropertyUndoRedoStack( final ObjPropertyMap< O, T > property )
	{
		this.property = property;
		stack = new ArrayList<>();
		top = 0;
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
		if ( top >= stack.size() )
			stack.add( property.get( obj ) );
		++top;
	}

	/**
	 * Decrement {@code top} and replace and the element there with the property value
	 * of {@code obj}. Set the previously stored element as the property value
	 * of {@code obj}.
	 *
	 * @param obj
	 *            object whose property value to swap with the element at {@code top-1}.
	 */
	@Override
	public void undo( final O obj )
	{
		if (  top > 0 )
		{
			--top;
			stack.set( top, property.set( obj, stack.get( top ) ) );
		}
	}

	/**
	 * Increment {@code top} and replace and the element there with the property value of {@code obj}.
	 * Set the previously stored element as the property value of {@code obj}.
	 *
	 * @param obj
	 *            object whose property value to swap with the element at {@code top}.
	 */
	@Override
	public void redo( final O obj )
	{
		if ( top < stack.size() - 1 )
		{
			++top;
			stack.set( top, property.set( obj, stack.get( top ) ) );
		}
	}
}
