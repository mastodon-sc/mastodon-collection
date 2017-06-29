package org.mastodon.collection;

import java.util.Map;

/**
 * A {@link Map} whose values are object references. It provides variants of
 * {@link Map} methods that take object references that can be used for
 * retrieval. Depending on concrete implementation, these object references can
 * be cleared, ignored or re-used.
 *
 * Similar to {@link RefCollection}, this class can create and release object
 * references (of type {@code V}).
 *
 * @param <K>
 *            key type.
 * @param <V>
 *            value type.
 *
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 */
public interface ObjectRefMap< K, V > extends Map< K, V >
{
	/**
	 * Generates a value object reference that can be used for retrieval.
	 * Depending on concrete implementation, the returned object can be
	 * {@code null.}
	 *
	 * @return a new, uninitialized, reference object.
	 */
	public V createValueRef();

	/**
	 * Releases a previously created value object reference. For standard object
	 * maps, this method does nothing.
	 *
	 * @param obj
	 *            the object reference to release.
	 */
	public void releaseValueRef( final V obj );

	@Override
	public RefCollection< V > values();

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map <tt>m</tt>
	 * is said to contain a mapping for a key <tt>k</tt> if and only if
	 * {@link #containsKey(Object) m.containsKey(k)} would return <tt>true</tt>.)
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key.
	 * @param ref
	 *            a value object reference that can be used for retrieval.
	 *            Depending on concrete implementation, this object can be
	 *            cleared, ignored or re-used.
	 * @return the previous value associated with the specified key, or
	 *         {@code null} if the map contained no mapping for the key. The
	 *         object actually returned might be the one specified as parameter
	 *         {@code replacedObj}, depending on concrete implementation.
	 * @throws UnsupportedOperationException
	 *             if the <tt>put</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the class of the specified key or value prevents it from
	 *             being stored in this map
	 * @throws NullPointerException
	 *             if the specified key or value is null and this map does not
	 *             permit null keys or values
	 * @throws IllegalArgumentException
	 *             if some property of the specified key or value prevents it
	 *             from being stored in this map
	 */
	public V put( K key, V value, V ref );

	/**
	 * Removes the mapping for a key from this map if it is present. Returns the
	 * value to which this map previously associated the key. or {@code null} if
	 * the map contained no mapping for the key.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #remove(Object)}.
	 *
	 * @param key
	 *            the key whose associated value is to be returned.
	 * @param ref
	 *            a value object reference that can be used for retrieval.
	 *            Depending on concrete implementation, this object can be
	 *            cleared, ignored or re-used.
	 * @return the previous value associated with the specified key, or
	 *         {@code null} if the map contained no mapping for the key. The
	 *         object actually returned might be the one specified as parameter
	 *         {@code ref}, depending on concrete implementation.
	 */
	public V removeWithRef( Object key, V ref );

	/**
	 * Returns the value to which the specified key is mapped, or {@code null}
	 * if this map contains no mapping for the key.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #get(Object)}.
	 *
	 * @param key
	 *            the key whose associated value is to be returned.
	 * @param ref
	 *            a value object reference that can be used for retrieval.
	 *            Depending on concrete implementation, this object can be
	 *            cleared, ignored or re-used.
	 * @return value to which the specified key is mapped, or {@code null} if
	 *         this map contains no mapping for the key. The object actually
	 *         returned might be the one specified as parameter {@code ref},
	 *         depending on concrete implementation.
	 */
	public V get( Object key, V ref );
}
