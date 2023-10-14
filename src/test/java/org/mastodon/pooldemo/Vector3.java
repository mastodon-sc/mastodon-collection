/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.pooldemo;

import org.mastodon.util.DelegateRealLocalizable;
import org.mastodon.util.DelegateRealPositionable;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttributeValue;

public class Vector3 extends PoolObject< Vector3, Vector3Pool, ByteMappedElement >
	implements DelegateRealLocalizable, DelegateRealPositionable
{
	private final RealPointAttributeValue position;

	Vector3( final Vector3Pool pool )
	{
		super( pool );

		/*
		 * doesn't send property change events
		 */
//		position = pool.position.createQuietAttributeValue( this );

		/*
		 * sends property change events
		 */
		position = pool.position.createAttributeValue( this );
	}

	public Vector3 init( final double... pos )
	{
		pool.position.setPositionQuiet( this, pos );
		return this;
	}

	@Override
	protected void setToUninitializedState()
	{}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		char c = '(';
		for ( int i = 0; i < numDimensions(); i++ )
		{
			sb.append( c );
			sb.append( getDoublePosition( i ) );
			c = ',';
		}
		sb.append( ")" );
		return sb.toString();
	}

	@Override
	public RealPointAttributeValue delegate()
	{
		return position;
	}
}
