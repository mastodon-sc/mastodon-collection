package org.mastodon.properties;

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
}
