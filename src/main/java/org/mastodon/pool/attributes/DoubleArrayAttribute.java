package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.DoubleArrayField;

public class DoubleArrayAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public DoubleArrayAttribute( final DoubleArrayField layoutField )
	{
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final int index, final double value )
	{
		access( key ).putDouble( value, offset + index * DOUBLE_SIZE );
	}

	public void set( final O key, final int index, final double value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putDouble( value, offset + index * DOUBLE_SIZE );
		notifyPropertyChanged( key );
	}

	public double get( final O key, final int index )
	{
		return access( key ).getDouble( offset + index * DOUBLE_SIZE );
	}
}
