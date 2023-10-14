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
package org.mastodon.io.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Map from {@code String} keys to {@link PropertyMapSerializer}. See
 * {@link RawPropertyIO}.
 *
 * @param <O>
 *            type of object which properties are attached to.
 *
 * @author Tobias Pietzsch
 */
public class PropertyMapSerializers< O >
{
	private final HashMap< String, PropertyMapSerializer< O, ? > > serializers = new HashMap<>();

	private final ArrayList< String > keys = new ArrayList<>();

	public static final class DuplicateKeyException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public DuplicateKeyException()
		{
			super();
		}

		public DuplicateKeyException( final String message )
		{
			super( message );
		}
	}

	public void put( final String key, final PropertyMapSerializer< O, ? > serializer )
	{
		if ( serializers.containsKey( key ) )
			throw new DuplicateKeyException( String.format( "property key \"%s\" already exists", key ) );
		serializers.put( key, serializer );
		keys.add( key );
	}

	public PropertyMapSerializer< O, ? > getPropertyMap( final String key )
	{
		return serializers.get( key );
	}

	public List< String > getKeys()
	{
		return Collections.unmodifiableList( keys );
	}
}
