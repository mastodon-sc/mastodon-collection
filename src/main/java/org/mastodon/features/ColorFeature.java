package org.mastodon.features;

import java.awt.Color;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.features.FeatureRegistry.DuplicateKeyException;

import gnu.trove.map.TObjectIntMap;

/**
 * Color feature backed by an {@code int} map. The map stores the RGBA code.
 * This feature then returns the corresponding {@link Color} object.
 * <p>
 * We use the {@code rgba = 0} code for {@code noEntryValue} in the map, which
 * corresponds to the completely transparent black color. So this feature cannot
 * store this color.
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <O>
 *            type of object to which feature should be attached.
 */
public class ColorFeature< O > extends Feature< TObjectIntMap< O >, O, FeatureValue< Color > >
{

	public ColorFeature( final String name ) throws DuplicateKeyException
	{
		super( name );
	}

	@Override
	protected TObjectIntMap< O > createFeatureMap( final RefCollection< O > pool )
	{
		return RefCollections.createRefIntMap( pool, 0 );
	}

	@Override
	protected FeatureCleanup< O > createFeatureCleanup( final TObjectIntMap< O > featureMap )
	{
		return new FeatureCleanup< O >()
		{
			@Override
			public void delete( final O object )
			{
				featureMap.remove( object );
			}
		};
	}

	@Override
	public UndoFeatureMap< O > createUndoFeatureMap( final TObjectIntMap< O > featureMap )
	{
		return new IntUndoFeatureMap<>( featureMap, 0 );
	}

	@Override
	public ColorFeatureValue< O > createFeatureValue( final O object, final Features< O > features )
	{
		return new ColorFeatureValue< O >( features.getFeatureMap( this ),
				object,
				new NotifyValueChange<>( features, this, object ) );
	}
}
