package org.mastodon.collection;

import java.util.Map;

/**
 * A {@link Map} whose keys are object references. In practice, this means that
 * the key-set of the map is a {@link RefSet} providing methods that take object
 * references. Depending on concrete implementation, these object references can
 * be cleared, ignored or re-used.
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            value type.
 *
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 */
public interface RefObjectMap< K, V > extends Map< K, V >
{
	@Override
	public RefSet< K > keySet();
}
