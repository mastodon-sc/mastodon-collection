package org.mastodon.pool;

import org.mastodon.properties.Property;

/**
 * Marker interface. This is used for undo/redo to mark that nothing special has
 * to be done for a {@link Property} when an object is deleted, because the
 * property value is part of the object layout.
 *
 * @author Tobias Pietzsch
 */
public interface Attribute
{}
