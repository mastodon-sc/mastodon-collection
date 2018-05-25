package org.mastodon;

public class Options
{
	/**
	 * Whether the following checks should be made:
	 * <ul>
	 *     <li>Check whether {@code ByteUtils} methods access out of bounds array values, and throw ArrayIndexOutOfBoundsException.</li>
	 *     <li>Check whether {@code Pool.getObject} tries to retrieve a freed element, and throw NoSuchElementException.</li>
	 *     <li>Check whether {@code Pool.delete} tries to free an already freed element, and throw IllegalArgumentException.</li>
	 * </ul>
	 */
	public static final boolean DEBUG = true;

//	static {
//		String debug = System.getProperty( "mastodon-collection.debug", "false" ).trim();
//		DEBUG = debug.isEmpty() || debug.equals( "true" );
//	}
}
