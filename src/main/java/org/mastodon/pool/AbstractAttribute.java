package org.mastodon.pool;

import org.mastodon.pool.PoolObjectLayout.PrimitiveField;
import org.mastodon.properties.AbstractProperty;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

/**
 * Makes {@link PoolObject#access} visible to subclasses.
 *
 * @author Tobias Pietzsch
 */
public class AbstractAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractProperty< O >
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
}
