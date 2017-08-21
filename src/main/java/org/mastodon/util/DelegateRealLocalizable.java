package org.mastodon.util;

import net.imglib2.RealLocalizable;
import org.mastodon.util.DelegateEuclideanSpace;

/**
 * A {@link RealLocalizable} backed by another {@link RealLocalizable}.
 *
 * @author Curtis Rueden
 * @author Tobias Pietzsch
 */
public interface DelegateRealLocalizable extends RealLocalizable, DelegateEuclideanSpace
{
	@Override
	RealLocalizable delegate();

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
