package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.IndexArrayField;
import org.mastodon.properties.BeforePropertyChangeListener;
import org.mastodon.properties.PropertyChangeListener;

public class IndexArrayAttribute< O extends PoolObject< O, ?, ? > >
	extends AbstractAttribute< O >
{
	private final int offset;

	private final int length;

	public IndexArrayAttribute( final IndexArrayField layoutField, final Pool< O, ? > pool )
	{
		super( layoutField, pool );
		this.offset = layoutField.getOffset();
		this.length = layoutField.numElements();
	}

	public void setQuiet( final O key, final int index, final int value )
	{
		access( key ).putIndex( value, offset + index * INDEX_SIZE );
	}

	public void set( final O key, final int index, final int value )
	{
		notifyBeforePropertyChange( key );
		access( key ).putIndex( value, offset + index * INDEX_SIZE );
		notifyPropertyChanged( key );
	}

	public int get( final O key, final int index )
	{
		return access( key ).getIndex( offset + index * INDEX_SIZE );
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
	 * {@link #setQuiet(PoolObject, int, int)} without sending notification
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
	 * {@link #setQuiet(PoolObject, int, int)} without sending notification
	 * for all of them.
	 * </p>
	 */
	@Override
	public void notifyPropertyChanged( final O object )
	{
		super.notifyPropertyChanged( object );
	}

	public IndexArrayAttributeValue createAttributeValue( final O key )
	{
		return new IndexArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return IndexArrayAttribute.this.length();
			}

			@Override
			public int get( final int index )
			{
				return IndexArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final int value )
			{
				IndexArrayAttribute.this.set( key, index, value );
			}
		};
	}

	public IndexArrayAttributeValue createQuietAttributeValue( final O key )
	{
		return new IndexArrayAttributeValue()
		{
			@Override
			public int length()
			{
				return IndexArrayAttribute.this.length();
			}

			@Override
			public int get( final int index )
			{
				return IndexArrayAttribute.this.get( key, index );
			}

			@Override
			public void set( final int index, final int value )
			{
				IndexArrayAttribute.this.setQuiet( key, index, value );
			}
		};
	}
}
