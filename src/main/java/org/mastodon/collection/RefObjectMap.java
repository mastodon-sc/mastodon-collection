package org.mastodon.collection;

import java.util.Map;

/**
 * Map-like interface for maps that map possibly reusable references to plain
 * objects.
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            value type.
 *
 * @author Jean-Yves Tinevez
 */
public interface RefObjectMap< K, V > extends Map< K, V >
{
	/**
	 * Generates a key object reference that can be used for retrieval.
	 * Depending on concrete implementation, the returned object can be
	 * {@code null.}
	 *
	 * @return a new, uninitialized, reference object.
	 */
	public K createKeyRef();

	/**
	 * Releases a previously created key object reference. For standard object
	 * maps, this method does nothing.
	 *
	 * @param obj
	 *            the object reference to release.
	 */
	public void releaseKeyRef( final K obj );
}
