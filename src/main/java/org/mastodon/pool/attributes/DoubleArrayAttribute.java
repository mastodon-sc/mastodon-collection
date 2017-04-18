package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.DoubleArrayField;

public class DoubleArrayAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	private final int length;

	public DoubleArrayAttribute( final DoubleArrayField layoutField )
	{
		this.offset = layoutField.getOffset();
		this.length = layoutField.numElements();
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

	public int length()
	{
		return length;
	}

	public DoubleArrayAttributeValue createAttributeValue( final O key )
	{
		return new DoubleArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return DoubleArrayAttribute.this.length();
			}

			@Override
			public double get( final int index )
			{
				return DoubleArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final double value )
			{
				DoubleArrayAttribute.this.set( key, index, value );
			}
		};
	}

	public DoubleArrayAttributeValue createQuietAttributeValue( final O key )
	{
		return new DoubleArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return DoubleArrayAttribute.this.length();
			}

			@Override
			public double get( final int index )
			{
				return DoubleArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final double value )
			{
				DoubleArrayAttribute.this.setQuiet( key, index, value );
			}
		};
	}
}
