package org.mastodon.pool.attributes;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.DoubleField;

public class DoubleAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public DoubleAttribute( final DoubleField layoutField )
	{
		super( layoutField );
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final double value )
	{
		access( key ).putDouble( value, offset );
	}

	public void set( final O key, final double value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putDouble( value, offset );
		notifyPropertyChanged( key );
	}

	public double get( final O key )
	{
		return access( key ).getDouble( offset );
	}

	public DoubleAttributeValue createAttributeValue( final O key )
	{
		return new DoubleAttributeValue()
		{
			@Override
			public double get()
			{
				return DoubleAttribute.this.get( key );
			}

			@Override
			public void set( final double value )
			{
				DoubleAttribute.this.set( key, value );
			}
		};
	}

	public DoubleAttributeValue createQuietAttributeValue( final O key )
	{
		return new DoubleAttributeValue()
		{
			@Override
			public double get()
			{
				return DoubleAttribute.this.get( key );
			}

			@Override
			public void set( final double value )
			{
				DoubleAttribute.this.setQuiet( key, value );
			}
		};
	}
}
