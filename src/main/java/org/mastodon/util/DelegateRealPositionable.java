/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.util;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import org.mastodon.util.DelegateEuclideanSpace;

/**
 * A {@link RealPositionable} backed by another {@link RealPositionable}.
 *
 * @author Curtis Rueden
 * @author Tobias Pietzsch
 */
// TODO: this should probably be in imglib?
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
