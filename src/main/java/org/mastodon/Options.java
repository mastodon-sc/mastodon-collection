package org.mastodon;

public class Options
{
	/**
	 * Whether the following checks should be made:
	 * <ul>
	 *     <li>Check whether {@code ByteUtils} methods access out of bounds array values, and throw ArrayIndexOutOfBoundsException.</li>
	 *     <li>Check whether {@code Pool.getObject} tries to retrieve a freed element, and throw NoSuchElementException.</li>
	 * </ul>
	 */
	public static final boolean DEBUG = false;

//	static {
//		String debug = System.getProperty( "mastodon-collection.debug", "false" ).trim();
//		DEBUG = debug.isEmpty() || debug.equals( "true" );
//	}
}
