package org.mastodon.pool.attributes;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.IntField;

public class IntAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public IntAttribute( final IntField layoutField )
	{
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final int value )
	{
		access( key ).putInt( value, offset );
	}

	public void set( final O key, final int value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putInt( value, offset );
		notifyPropertyChanged( key );
	}

	public int get( final O key )
	{
		return access( key ).getInt( offset );
	}

	public IntAttributeValue createAttributeValue( final O key )
	{
		return new IntAttributeValue()
		{
			@Override
			public int get()
			{
				return IntAttribute.this.get( key );
			}

			@Override
			public void set( final int value )
			{
				IntAttribute.this.set( key, value );
			}
		};
	}

	public IntAttributeValue createQuietAttributeValue( final O key )
	{
		return new IntAttributeValue()
		{
			@Override
			public int get()
			{
				return IntAttribute.this.get( key );
			}

			@Override
			public void set( final int value )
			{
				IntAttribute.this.setQuiet( key, value );
			}
		};
	}
}
