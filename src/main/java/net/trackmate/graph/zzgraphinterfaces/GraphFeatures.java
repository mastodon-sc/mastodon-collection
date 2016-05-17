package net.trackmate.graph.zzgraphinterfaces;

import java.util.ArrayList;
import java.util.Map;

import net.trackmate.graph.util.UniqueHashcodeArrayMap;
import net.trackmate.graph.zzgraphinterfaces.VertexFeature.FeatureCleanup;
import net.trackmate.io.RawFeatureIO;

public class GraphFeatures< V extends Vertex< E >, E extends Edge< V > >
{
	private final ReadOnlyGraph< V, E > graph;

	/**
	 * Maps {@link VertexFeature} to feature-map objects, that are usually
	 * {@code Map<V,T>} with the type {@code T} of feature values. The reason
	 * this is not fixed to {@code Map<V,?>} is that for example primitive
	 * features might want to use Trove maps instead.
	 */
	private final Map< VertexFeature< ?, V, ? >, Object > vertexFeatureMaps;

	private final ArrayList< FeatureCleanup< V > > vertexFeatureCleanups;

	private final ArrayList< CreateFeatureMapListener< V, E > > createFeatureMapListeners;

	private final ArrayList< FeatureChangeListener< V, E > > featureChangeListeners;

	public GraphFeatures( final ReadOnlyGraph< V, E > graph )
	{
		this.graph = graph;
		vertexFeatureMaps = new UniqueHashcodeArrayMap<>();
		vertexFeatureCleanups = new ArrayList<>();
		createFeatureMapListeners = new ArrayList<>();
		featureChangeListeners = new ArrayList<>();
	}

	/**
	 * FOR INTERNAL USE ONLY.
	 * TODO
	 * TODO
	 * TODO
	 *
	 * This is only public because it needs to be accessed from {@link RawFeatureIO}.
	 */
	@SuppressWarnings( "unchecked" )
	public < M > M getVertexFeature( final VertexFeature< M, V, ? > feature )
	{
		M fmap = ( M ) vertexFeatureMaps.get( feature );
		if ( fmap == null )
		{
			fmap = feature.createFeatureMap( graph );
			vertexFeatureMaps.put( feature, fmap );
			vertexFeatureCleanups.add( feature.createFeatureCleanup( fmap ) );
			for ( final CreateFeatureMapListener< V, E > l : createFeatureMapListeners )
				l.createFeatureMap( feature, fmap );
		}
		return fmap;
	}

	public void clear()
	{
		vertexFeatureMaps.clear();
		vertexFeatureCleanups.clear();
	}

	/**
	 * for internal use.
	 *
	 * @param vertex
	 */
	public void delete( final V vertex )
	{
		for ( final FeatureCleanup< V > cleanup : vertexFeatureCleanups )
			cleanup.delete( vertex );
	}

	public interface CreateFeatureMapListener< V extends Vertex< E >, E extends Edge< V > >
	{
		public < M > void createFeatureMap( final VertexFeature< M, V, ? > feature, M featureMap );
	}

	/**
	 * Register a {@link CreateFeatureMapListener} that will be notified when
	 * new feature maps are created (this happens once per newly occurring
	 * feature in a graph).
	 *
	 * @param listener
	 *            the listener to register.
	 * @return {@code true} if the listener was successfully registered.
	 *         {@code false} if it was already registered.
	 */
	public boolean addCreateFeatureMapListener( final CreateFeatureMapListener< V, E > listener )
	{
		if ( ! createFeatureMapListeners.contains( listener ) )
		{
			createFeatureMapListeners.add( listener );
			return true;
		}
		return false;
	}

	/**
	 * Removes the specified {@link CreateFeatureMapListener} from the set of
	 * listeners.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was present in the listeners of this
	 *         model and was successfully removed.
	 */
	public boolean removeCreateFeatureMapListener( final CreateFeatureMapListener< V, E > listener )
	{
		return createFeatureMapListeners.remove( listener );
	}

	/**
	 * Register a {@link FeatureChangeListener} that will be notified when
	 * feature values are changed.
	 *
	 * @param listener
	 *            the listener to register.
	 * @return {@code true} if the listener was successfully registered.
	 *         {@code false} if it was already registered.
	 */
	public boolean addFeatureChangeListener( final FeatureChangeListener< V, E > listener )
	{
		if ( ! featureChangeListeners.contains( listener ) )
		{
			featureChangeListeners.add( listener );
			return true;
		}
		return false;
	}

	/**
	 * Removes the specified {@link FeatureChangeListener} from the set of
	 * listeners.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was present in the listeners of this
	 *         model and was successfully removed.
	 */
	public boolean removeFeatureChangeListener( final FeatureChangeListener< V, E > listener )
	{
		return featureChangeListeners.remove( listener );
	}

	void notifyBeforeFeatureChange( final VertexFeature< ?, V, ? > feature, final V vertex )
	{
		for ( final FeatureChangeListener< V, E > l : featureChangeListeners )
			l.beforeFeatureChange( feature, vertex );
	}
}