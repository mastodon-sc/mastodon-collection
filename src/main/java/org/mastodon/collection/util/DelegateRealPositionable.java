package org.mastodon.collection.util;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * A {@link RealPositionable} backed by another {@link RealPositionable}.
 *
 * @author Curtis Rueden
 * @author Tobias Pietzsch
 */
public interface DelegateRealPositionable extends RealPositionable, DelegateEuclideanSpace
{
	@Override
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