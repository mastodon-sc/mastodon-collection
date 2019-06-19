package org.mastodon.undo;

/**
 * Elements in a {@link UndoRedoStack} can be marked as being
 * <em>undo-points</em>. Each high-level undo/redo step triggered by the user
 * proceeds to undo/redo low-level elements until the next undo-point is
 * reached.
 * <p>
 * Undo-points mark “stable states” in the order elements were recorded.
 * Therefore, undo will stop immediately before undoing the undo-point, and redo
 * will stop after redoing the undo-point.
 *
 * @author Tobias Pietzsch
 */
public interface UndoPointMarker
{
	public void setUndoPoint();
}
