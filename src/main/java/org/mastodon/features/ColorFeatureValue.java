package org.mastodon.features;

import java.awt.Color;

import org.mastodon.util.ColorStore;

import gnu.trove.map.TObjectIntMap;


/**
 * Feature value for colors, based on storing their <code>int</code> RGBA code
 * in a map.
 *
 * @param <O>
 *            type of object to which the feature should be attached.
 *
 * @author Jean-Yves Tinevez
 */
public class ColorFeatureValue< O > implements FeatureValue< Color >
{
	private final TObjectIntMap< O > featureMap;

	private final O object;

	private final NotifyFeatureValueChange notify;

	protected ColorFeatureValue( final TObjectIntMap< O > featureMap, final O object, final NotifyFeatureValueChange notify )
	{
		this.featureMap = featureMap;
		this.object = object;
		this.notify = notify;
	}

	@Override
	public void set( final Color value )
	{
		notify.notifyBeforeFeatureChange();
		if ( value == null )
			featureMap.remove( object );
		else
			featureMap.put( object, value.getRGB() );
	}

	/**
	 * Sets the color of this item via the color's RGBA. This {@code int} value
	 * is such that {@code color = new Color(rgba, true)}.
	 * <p>
	 * This value consists of the alpha component in bits 24-31, the red
	 * component in bits 16-23, the green component in bits 8-15, and the blue
	 * component in bits 0-7.
	 * 
	 * @param value
	 *            the RGBA {@code int} value.
	 */
	public void set( final int value )
	{
		notify.notifyBeforeFeatureChange();
		featureMap.put( object, value );
	}

	@Override
	public void remove()
	{
		notify.notifyBeforeFeatureChange();
		featureMap.remove( object );
	}

	@Override
	public Color get()
	{
		final int rgba = featureMap.get( object );
		if ( rgba == featureMap.getNoEntryValue() )
			return null;

		return ColorStore.get( rgba );
	}

	/**
	 * Returns the RGBA code for the color feature of this item. This
	 * {@code int} value is such that {@code color = new Color(rgba, true)}.
	 * <p>
	 * This value consists of the alpha component in bits 24-31, the red
	 * component in bits 16-23, the green component in bits 8-15, and the blue
	 * component in bits 0-7.
	 * 
	 * @return the RGBA {@code int} value.
	 */
	public int getRGBA()
	{
		return featureMap.get( object );
	}

	@Override
	public boolean isSet()
	{
		return featureMap.containsKey( object );
	}

}
