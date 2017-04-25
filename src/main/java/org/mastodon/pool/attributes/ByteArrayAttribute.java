package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.BYTE_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.ByteArrayField;
import org.mastodon.properties.BeforePropertyChangeListener;
import org.mastodon.properties.PropertyChangeListener;

public class ByteArrayAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	private final int length;

	public ByteArrayAttribute( final ByteArrayField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
		this.length = layoutField.numElements();
	}

	public void setQuiet( final O key, final int index, final byte value )
	{
		access( key ).putByte( value, offset + index * BYTE_SIZE );
	}

	public void set( final O key, final int index, final byte value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putByte( value, offset + index * BYTE_SIZE );
		notifyPropertyChanged( key );
	}

	public byte get( final O key, final int index )
	{
		return access( key ).getByte( offset + index * BYTE_SIZE );
	}

	public int length()
	{
		return length;
	}

	/**
	 * Notify {@link BeforePropertyChangeListener}s that the value of this
	 * property is about to change.
	 * <p>
	 * This is exposed publicly to be able to change a few elements with
	 * {@link #setQuiet(PoolObject, int, double)} without sending notification
	 * for all of them.
	 * </p>
	 */
	@Override
	public void notifyBeforePropertyChange( final O object )
	{
		super.notifyBeforePropertyChange( object );
	}

	/**
	 * Notify {@link PropertyChangeListener}s that the value of this property
	 * has changed.
	 * <p>
	 * This is exposed publicly to be able to change a few elements with
	 * {@link #setQuiet(PoolObject, int, double)} without sending notification
	 * for all of them.
	 * </p>
	 */
	@Override
	public void notifyPropertyChanged( final O object )
	{
		super.notifyPropertyChanged( object );
	}

	public ByteArrayAttributeValue createAttributeValue( final O key )
	{
		return new ByteArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return ByteArrayAttribute.this.length();
			}

			@Override
			public byte get( final int index )
			{
				return ByteArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final byte value )
			{
				ByteArrayAttribute.this.set( key, index, value );
			}
		};
	}

	public ByteArrayAttributeValue createQuietAttributeValue( final O key )
	{
		return new ByteArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return ByteArrayAttribute.this.length();
			}

			@Override
			public byte get( final int index )
			{
				return ByteArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final byte value )
			{
				ByteArrayAttribute.this.setQuiet( key, index, value );
			}
		};
	}
}
