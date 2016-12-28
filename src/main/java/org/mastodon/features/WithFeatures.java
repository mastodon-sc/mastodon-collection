package org.mastodon.features;

/**
 * Interface for objects that can return a {@link FeatureValue} for a specified
 * {@link Feature} defined for such objects.
 *
 * @param <O>
 *            the type of objects to which the feature is attached.
 */
public interface WithFeatures< O >
{
	/**
	 * Returns a feature value for the specified feature.
	 *
	 * @param feature
	 *            the feature to get.
	 * @return a feature value for the specified feature.
	 */
	public < F extends FeatureValue< ? >, M > F feature( final Feature< M, O, F > feature );
}
