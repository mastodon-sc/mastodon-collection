package org.mastodon.util;

import java.awt.Color;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * A static color store.
 * <p>
 * Color objects are immutable but need creation every time a color is required.
 * This static utility aims at avoiding recreating color objects by storing
 * existing colors in a map. When queried for a color, if it exists in the map,
 * the instance is returned. If not, it is created and stored in the map for
 * later use.
 * 
 * @author Jean-Yves Tinevez
 *
 */
public class ColorStore
{
	private static final TIntObjectMap< Color > colors = new TIntObjectHashMap<>();

	/**
	 * Returns a sRGB color with the specified combined RGBA value consisting of
	 * the alpha component in bits 24-31, the red component in bits 16-23, the
	 * green component in bits 8-15, and the blue component in bits 0-7.
	 * 
	 * @param rgba
	 *            the RGBA code.
	 * @return a color.
	 */
	public static final Color get( final int rgba )
	{
		Color color = colors.get( rgba );
		if ( null == color )
		{
			color = new Color( rgba, true);
			colors.put( rgba, color);
		}
		return color;
	}

	private ColorStore()
	{}
}
