package org.mastodon.collection;

import java.util.Map;

/**
 * A {@link Map} whose keys and values both are object references. Map-like
 * interface for maps that map possibly reusable references to another possibly
 * reusable reference.
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            value type.
 *
 * @author Jean-Yves Tinevez
 */
public interface RefRefMap< K, V > extends RefObjectMap< K, V >, ObjectRefMap< K, V >
{}
