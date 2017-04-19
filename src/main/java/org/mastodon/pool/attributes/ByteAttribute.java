package org.mastodon.pool.attributes;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.ByteField;

public class ByteAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	public ByteAttribute( final ByteField layoutField )
	{
		super( layoutField );
		this.offset = layoutField.getOffset();
	}

	public void setQuiet( final O key, final byte value )
	{
		access( key ).putByte( value, offset );
	}

	public void set( final O key, final byte value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putByte( value, offset );
		notifyPropertyChanged( key );
	}

	public byte get( final O key )
	{
		return access( key ).getByte( offset );
	}

	public ByteAttributeValue createAttributeValue( final O key )
	{
		return new ByteAttributeValue()
		{
			@Override
			public byte get()
			{
				return ByteAttribute.this.get( key );
			}

			@Override
			public void set( final byte value )
			{
				ByteAttribute.this.set( key, value );
			}
		};
	}

	public ByteAttributeValue createQuietAttributeValue( final O key )
	{
		return new ByteAttributeValue()
		{
			@Override
			public byte get()
			{
				return ByteAttribute.this.get( key );
			}

			@Override
			public void set( final byte value )
			{
				ByteAttribute.this.setQuiet( key, value );
			}
		};
	}
}
