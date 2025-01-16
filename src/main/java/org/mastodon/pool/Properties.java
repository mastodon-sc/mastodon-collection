/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.pool;

import java.util.ArrayList;

import org.mastodon.properties.Property;
import org.mastodon.properties.PropertyMap;

/**
 * Maintains a list of {@link Property}s for a {@link Pool}. The purpose is to
 * broadcast {@link #pauseListeners()} and {@link #resumeListeners()} events to
 * all {@link Property}s.
 * <p>
 * This is used both for the {@link AbstractAttribute}s of the pool (which are
 * registered automatically), and for {@link PropertyMap}s that are tied to the
 * pool (e.g. as member variables).
 * </p>
 * <p>
 * Temporary {@link PropertyMap}s (that are created for example by algorithms to
 * hold temporary information about graph vertices) should not be registered
 * here.
 * </p>
 *
 * @param <O>
 *            object type (key type for all {@link Property}s)
 *
 * @author Tobias Pietzsch
 */
public class Properties< O >
{
	private final ArrayList< Property< O > > properties = new ArrayList<>();

	void add( final Property< O > property )
	{
		properties.add( property );
	}

	/**
	 * Forward to {@link Property#pauseListeners()} of all registered
	 * properties.
	 */
	public void pauseListeners()
	{
		properties.forEach( a -> a.pauseListeners() );
	}

	/**
	 * Forward to {@link Property#resumeListeners()} of all registered
	 * properties.
	 */
	public void resumeListeners()
	{
		properties.forEach( a -> a.resumeListeners() );
	}
}
