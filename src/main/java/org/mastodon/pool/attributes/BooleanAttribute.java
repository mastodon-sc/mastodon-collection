package org.mastodon.pool.attributes;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.BooleanField;

public class BooleanAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public BooleanAttribute( final BooleanField layoutField )
	{
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final boolean value )
	{
		access( key ).putBoolean( value, offset );
	}

	public void set( final O key, final boolean value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putBoolean( value, offset );
		notifyPropertyChanged( key );
	}

	public boolean get( final O key )
	{
		return access( key ).getBoolean( offset );
	}

	public BooleanAttributeValue createAttributeValue( final O key )
	{
		return new BooleanAttributeValue()
		{
			@Override
			public boolean get()
			{
				return BooleanAttribute.this.get( key );
			}

			@Override
			public void set( final boolean value )
			{
				BooleanAttribute.this.set( key, value );
			}
		};
	}

	public BooleanAttributeValue createQuietAttributeValue( final O key )
	{
		return new BooleanAttributeValue()
		{
			@Override
			public boolean get()
			{
				return BooleanAttribute.this.get( key );
			}

			@Override
			public void set( final boolean value )
			{
				BooleanAttribute.this.setQuiet( key, value );
			}
		};
	}
}
