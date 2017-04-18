package org.mastodon.pool.attributes;

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;

import org.mastodon.pool.AbstractAttribute;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout.DoubleArrayField;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

public class RealPointAttribute< O extends PoolObject< O, ?, ? > >
		extends AbstractAttribute< O >
{
	private final int offset;

	private final int n;

	public RealPointAttribute( final DoubleArrayField layoutField )
	{
		this.offset = layoutField.getOffset();
		this.n = layoutField.numElements();
	}

	public abstract class AbstractRealPointAccess implements RealLocalizable, RealPositionable
	{
		final O obj;

		public AbstractRealPointAccess( final O obj )
		{
			this.obj = obj;
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void localize( final float[] position )
		{
			RealPointAttribute.this.localize( obj, position );
		}

		@Override
		public void localize( final double[] position )
		{
			RealPointAttribute.this.localize( obj, position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return RealPointAttribute.this.getFloatPosition( obj, d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return RealPointAttribute.this.getDoublePosition( obj, d );
		}
	}

	public class RealPointAccess extends AbstractRealPointAccess
	{
		public RealPointAccess( final O obj )
		{
			super( obj );
		}

		@Override
		public void move( final float distance, final int d )
		{
			RealPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final double distance, final int d )
		{
			RealPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final RealLocalizable localizable )
		{
			RealPointAttribute.this.move( obj, localizable );
		}

		@Override
		public void move( final float[] distance )
		{
			RealPointAttribute.this.move( obj, distance );
		}

		@Override
		public void move( final double[] distance )
		{
			RealPointAttribute.this.move( obj, distance );
		}

		@Override
		public void setPosition( final RealLocalizable localizable )
		{
			RealPointAttribute.this.setPosition( obj, localizable );
		}

		@Override
		public void setPosition( final float[] position )
		{
			RealPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final double[] position )
		{
			RealPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final float position, final int d )
		{
			RealPointAttribute.this.setPosition( obj, position, d );
		}

		@Override
		public void setPosition( final double position, final int d )
		{
			RealPointAttribute.this.setPosition( obj, position, d );
		}

		@Override
		public void fwd( final int d )
		{
			RealPointAttribute.this.fwd( obj, d );
		}

		@Override
		public void bck( final int d )
		{
			RealPointAttribute.this.bck( obj, d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			RealPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			RealPointAttribute.this.move( obj, distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			RealPointAttribute.this.move( obj, localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			RealPointAttribute.this.move( obj, distance );
		}

		@Override
		public void move( final long[] distance )
		{
			RealPointAttribute.this.move( obj, distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			RealPointAttribute.this.setPosition( obj, localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			RealPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			RealPointAttribute.this.setPosition( obj, position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			RealPointAttribute.this.setPosition( obj, position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			RealPointAttribute.this.setPosition( obj, position, d );
		}
	}

	public class QuietRealPointAccess extends AbstractRealPointAccess
	{
		public QuietRealPointAccess( final O obj )
		{
			super( obj );
		}

		@Override
		public void move( final float distance, final int d )
		{
			RealPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final double distance, final int d )
		{
			RealPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final RealLocalizable localizable )
		{
			RealPointAttribute.this.moveQuiet( obj, localizable );
		}

		@Override
		public void move( final float[] distance )
		{
			RealPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void move( final double[] distance )
		{
			RealPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void setPosition( final RealLocalizable localizable )
		{
			RealPointAttribute.this.setPositionQuiet( obj, localizable );
		}

		@Override
		public void setPosition( final float[] position )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final double[] position )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final float position, final int d )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position, d );
		}

		@Override
		public void setPosition( final double position, final int d )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position, d );
		}

		@Override
		public void fwd( final int d )
		{
			RealPointAttribute.this.fwdQuiet( obj, d );
		}

		@Override
		public void bck( final int d )
		{
			RealPointAttribute.this.bckQuiet( obj, d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			RealPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			RealPointAttribute.this.moveQuiet( obj, distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			RealPointAttribute.this.moveQuiet( obj, localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			RealPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void move( final long[] distance )
		{
			RealPointAttribute.this.moveQuiet( obj, distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			RealPointAttribute.this.setPositionQuiet( obj, localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			RealPointAttribute.this.setPositionQuiet( obj, position, d );
		}
	}

	public int numDimensions()
	{
		return n;
	}

	/*
	 * RealLocalizable methods with additional key argument.
	 */

	public void localize( final O key, final float[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) a.getDouble( offset + d * DOUBLE_SIZE );
	}

	public void localize( final O key, final double[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			position[ d ] = a.getDouble( offset + d * DOUBLE_SIZE );
	}

	public float getFloatPosition( final O key, final int d )
	{
		return ( float ) access( key ).getDouble( offset + d * DOUBLE_SIZE );
	}

	public double getDoublePosition( final O key, final int d )
	{
		return access( key ).getDouble( offset + d * DOUBLE_SIZE );
	}

	/*
	 * RealPositionable methods with additional key argument.
	 */

	public void fwd( final O key, final int d )
	{
		notifyBeforePropertyChange( key );
		fwdQuiet( key, d );
		notifyPropertyChanged( key );
	}

	public void bck( final O key, final int d )
	{
		notifyBeforePropertyChange( key );
		bckQuiet( key, d );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final double position, final int d )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position, d );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final int[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final long[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final float[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final double[] position )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, position );
		notifyPropertyChanged( key );
	}

	public void setPosition( final O key, final RealLocalizable localizable )
	{
		notifyBeforePropertyChange( key );
		setPositionQuiet( key, localizable );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final double distance, final int d )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance, d );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final int[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final long[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final float[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final double[] distance )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, distance );
		notifyPropertyChanged( key );
	}

	public void move( final O key, final RealLocalizable localizable )
	{
		notifyBeforePropertyChange( key );
		moveQuiet( key, localizable );
		notifyPropertyChanged( key );
	}

	/*
	 * RealPositionable methods with additional key argument. "Quiet" versions
	 * that do not sent property change events.
	 */

	private static void addInPlace( final MappedElement a, final int index, final double increment )
	{
		a.putDouble( a.getDouble( index ) + increment, index );
	}

	public void fwdQuiet( final O key, final int d )
	{
		addInPlace( access( key ), offset + d * DOUBLE_SIZE, 1 );
	}

	public void bckQuiet( final O key, final int d )
	{
		addInPlace( access( key ), offset + d * DOUBLE_SIZE, -1 );
	}

	public void setPositionQuiet( final O key, final double position, final int d )
	{
		access( key ).putDouble( position, offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final int[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final long[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final float[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final double[] position )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( position[ d ], offset + d * DOUBLE_SIZE );
	}

	public void setPositionQuiet( final O key, final RealLocalizable localizable )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			a.putDouble( localizable.getDoublePosition( d ), offset + d * DOUBLE_SIZE );
	}

	public void moveQuiet( final O key, final double distance, final int d )
	{
		addInPlace( access( key ), offset + d * DOUBLE_SIZE, distance );
	}

	public void moveQuiet( final O key, final int[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final long[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final float[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final double[] distance )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, distance[ d ] );
	}

	public void moveQuiet( final O key, final RealLocalizable localizable )
	{
		final MappedElement a = access( key );
		for ( int d = 0; d < n; ++d )
			addInPlace( a, offset + d * DOUBLE_SIZE, localizable.getDoublePosition( d ) );
	}

	/**
	 * A {@link RealLocalizable} backed by another {@link RealLocalizable}.
	 *
	 * @author Curtis Rueden
	 * @author Tobias Pietzsch
	 */
	public interface DelegateRealLocalizable extends RealLocalizable
	{
		RealLocalizable delegate();

		@Override
		default int numDimensions()
		{
			return delegate().numDimensions();
		}

		@Override
		default void localize( final float[] position )
		{
			delegate().localize( position );
		}

		@Override
		default void localize( final double[] position )
		{
			delegate().localize( position );
		}

		@Override
		default float getFloatPosition( final int d )
		{
			return delegate().getFloatPosition( d );
		}

		@Override
		default double getDoublePosition( final int d )
		{
			return delegate().getDoublePosition( d );
		}
	}

	/**
	 * A {@link RealPositionable} backed by another {@link RealPositionable}.
	 *
	 * @author Curtis Rueden
	 * @author Tobias Pietzsch
	 */
	public interface DelegateRealPositionable extends RealPositionable
	{
		RealPositionable delegate();

		@Override
		default void fwd( final int d )
		{
			delegate().fwd( d );
		}

		@Override
		default void bck( final int d )
		{
			delegate().bck( d );
		}

		@Override
		default void move( final int distance, final int d )
		{
			delegate().move( distance, d );
		}

		@Override
		default void move( final long distance, final int d )
		{
			delegate().move( distance, d );
		}

		@Override
		default void move( final Localizable localizable )
		{
			delegate().move( localizable );
		}

		@Override
		default void move( final int[] distance )
		{
			delegate().move( distance );
		}

		@Override
		default void move( final long[] distance )
		{
			delegate().move( distance );
		}

		@Override
		default void setPosition( final Localizable localizable )
		{
			delegate().setPosition( localizable );
		}

		@Override
		default void setPosition( final int[] position )
		{
			delegate().setPosition( position );
		}

		@Override
		default void setPosition( final long[] position )
		{
			delegate().setPosition( position );
		}

		@Override
		default void setPosition( final int position, final int d )
		{
			delegate().setPosition( position, d );
		}

		@Override
		default void setPosition( final long position, final int d )
		{
			delegate().setPosition( position, d );
		}

		@Override
		default void move( final float distance, final int d )
		{
			delegate().move( distance, d );
		}

		@Override
		default void move( final double distance, final int d )
		{
			delegate().move( distance, d );
		}

		@Override
		default void move( final RealLocalizable localizable )
		{
			delegate().move( localizable );
		}

		@Override
		default void move( final float[] distance )
		{
			delegate().move( distance );
		}

		@Override
		default void move( final double[] distance )
		{
			delegate().move( distance );
		}

		@Override
		default void setPosition( final RealLocalizable localizable )
		{
			delegate().setPosition( localizable );
		}

		@Override
		default void setPosition( final float position[] )
		{
			delegate().setPosition( position );
		}

		@Override
		default void setPosition( final double position[] )
		{
			delegate().setPosition( position );
		}

		@Override
		default void setPosition( final float position, final int d )
		{
			delegate().setPosition( position, d );
		}

		@Override
		default void setPosition( final double position, final int d )
		{
			delegate().setPosition( position, d );
		}
	}
}
