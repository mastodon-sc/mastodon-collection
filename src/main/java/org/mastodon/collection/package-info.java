/**
 * This package contains {@link org.mastodon.collection.RefCollection}
 * interfaces. A {@link org.mastodon.collection.RefCollection} is a
 * {@link java.util.Collection} whose element type is possibly
 * {@link org.mastodon.Ref}. In this case, collections may be implemented by
 * storing the {@link org.mastodon.Ref#getInternalPoolIndex() pool indices} of
 * the elements instead of Object references.
 * 
 * <p>
 * Note that (despite its name) {@link org.mastodon.collection.RefCollection}
 * interfaces are <em>not</em> generically typed on {@link org.mastodon.Ref}.
 * The actual implementations for {@link org.mastodon.Ref} objects are provided
 * in {@link org.mastodon.collection.ref}, while wrappers for standard
 * {@link java.util.Collection}s are provided in
 * {@link org.mastodon.collection.wrap}.
 */
package org.mastodon.collection;
