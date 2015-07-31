package net.trackmate.bdv.wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.imglib2.algorithm.kdtree.ConvexPolytope;
import net.trackmate.graph.Edge;
import net.trackmate.graph.Graph;
import net.trackmate.graph.PoolObjectSet;
import net.trackmate.graph.RefPool;
import net.trackmate.graph.Vertex;
import net.trackmate.graph.collection.RefSet;
import net.trackmate.graph.mempool.DoubleMappedElement;
import net.trackmate.kdtree.ClipConvexPolytopeKDTree;
import net.trackmate.kdtree.KDTree;
import net.trackmate.trackscheme.GraphIdBimap;
import net.trackmate.trackscheme.TrackSchemeGraph;
import net.trackmate.trackscheme.TrackSchemeVertex;

/**
 * TODO: implement remaining ReadOnlyGraph methods
 * TODO: implement CollectionCreator
 *
 * @param <V>
 * @param <E>
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class OverlayGraphWrapper< V extends Vertex< E >, E extends Edge< V > >
	implements OverlayGraph< OverlayVertexWrapper< V, E >, OverlayEdgeWrapper< V, E > >
{
	final TrackSchemeGraph trackSchemeGraph;

	final Graph< V, E > modelGraph;

	final GraphIdBimap< V, E > idmap;

	final OverlayProperties< V > overlayProperties;

	private final Map< Integer, RefSet< OverlayVertexWrapper< V, E > > > timepointToSpots;

	private final Map< Integer, MySpatialSearch > timepointToSpatialSearch;

	private final ConcurrentLinkedQueue< OverlayVertexWrapper< V, E > > tmpVertexRefs =
			new ConcurrentLinkedQueue< OverlayVertexWrapper< V, E > >();

	private final ConcurrentLinkedQueue< OverlayEdgeWrapper< V, E > > tmpEdgeRefs =
			new ConcurrentLinkedQueue< OverlayEdgeWrapper< V, E > >();

	public OverlayGraphWrapper(
			final TrackSchemeGraph trackSchemeGraph,
			final Graph< V, E > modelGraph,
			final GraphIdBimap< V, E > idmap,
			final OverlayProperties< V > overlayProperties )
	{
		this.trackSchemeGraph = trackSchemeGraph;
		this.modelGraph = modelGraph;
		this.idmap = idmap;
		this.overlayProperties = overlayProperties;
		timepointToSpots = new HashMap< Integer, RefSet< OverlayVertexWrapper< V, E > > >();
		timepointToSpatialSearch = new HashMap< Integer, MySpatialSearch >();
	}

	@Override
	public RefSet< OverlayVertexWrapper< V, E > > getSpots( final int timepoint )
	{
		RefSet< OverlayVertexWrapper< V, E > > spots = timepointToSpots.get( timepoint );
		if ( null == spots )
		{
			spots = new PoolObjectSet< OverlayVertexWrapper< V, E > >( vertexPool );
			timepointToSpots.put( timepoint, spots );
		}
		return spots;
	}

	public void HACK_updateTimepointSets()
	{
		timepointToSpots.clear();
		final OverlayVertexWrapper< V, E > v = vertexRef();
		for ( final TrackSchemeVertex tsv : trackSchemeGraph.vertices() )
		{
			vertexPool.getByInternalPoolIndex( tsv.getInternalPoolIndex(), v );
			getSpots( v.getTimePoint() ).add( v );
		}
		releaseRef( v );

		timepointToSpatialSearch.clear();
		for ( final int timepoint : timepointToSpots.keySet() )
			timepointToSpatialSearch.put( timepoint, new MySpatialSearch( timepoint ) );
	}

	@Override
	public OverlayVertexWrapper< V, E > vertexRef()
	{
		final OverlayVertexWrapper< V, E > ref = tmpVertexRefs.poll();
		return ref == null ? new OverlayVertexWrapper< V, E >( this ) : ref;
	}

	@Override
	public OverlayEdgeWrapper< V, E > edgeRef()
	{
		final OverlayEdgeWrapper< V, E > ref = tmpEdgeRefs.poll();
		return ref == null ? new OverlayEdgeWrapper< V, E >( this ) : ref;
	}

	@Override
	public void releaseRef( final OverlayVertexWrapper< V, E > ref )
	{
		tmpVertexRefs.add( ref );
	}

	@Override
	public void releaseRef( final OverlayEdgeWrapper< V, E > ref )
	{
		tmpEdgeRefs.add( ref );
	}

	@Override
	public void releaseRef( final OverlayVertexWrapper< V, E >... refs )
	{
		for ( final OverlayVertexWrapper< V, E > ref : refs )
			tmpVertexRefs.add( ref );
	}

	@Override
	public void releaseRef( final OverlayEdgeWrapper< V, E >... refs )
	{
		for ( final OverlayEdgeWrapper< V, E > ref : refs )
			tmpEdgeRefs.add( ref );
	}

	private final RefPool< OverlayVertexWrapper< V, E > > vertexPool = new RefPool< OverlayVertexWrapper< V, E > >()
	{
		@Override
		public OverlayVertexWrapper< V, E > createRef()
		{
			return vertexRef();
		}

		@Override
		public void releaseRef( final OverlayVertexWrapper< V, E > obj )
		{
			OverlayGraphWrapper.this.releaseRef( obj );
		}

		@Override
		public void getByInternalPoolIndex( final int index, final OverlayVertexWrapper< V, E > obj )
		{
			trackSchemeGraph.getVertexPool().getByInternalPoolIndex( index, obj.tsv );
			obj.updateModelVertexRef();
		}
	};

	private final RefPool< OverlayEdgeWrapper< V, E > > edgePool = new RefPool< OverlayEdgeWrapper< V, E > >()
	{
		@Override
		public OverlayEdgeWrapper< V, E > createRef()
		{
			return edgeRef();
		}

		@Override
		public void releaseRef( final OverlayEdgeWrapper< V, E > obj )
		{
			OverlayGraphWrapper.this.releaseRef( obj );
		}

		@Override
		public void getByInternalPoolIndex( final int index, final OverlayEdgeWrapper< V, E > obj )
		{
			trackSchemeGraph.getEdgePool().getByInternalPoolIndex( index, obj.tse );
		}
	};

	@Override
	public SpatialSearch< OverlayVertexWrapper< V, E > > getSpatialSearch( final int timepoint )
	{
		return timepointToSpatialSearch.get( timepoint );
	}

	// TODO: should comprise KDTree and recent additions; KDTree should be rebuild asynchronously
	private class MySpatialSearch implements SpatialSearch< OverlayVertexWrapper< V, E > >
	{
		private final int timepoint;

		private final KDTree< OverlayVertexWrapper< V, E >, DoubleMappedElement > kdtree;

		private final ClipConvexPolytopeKDTree< OverlayVertexWrapper< V, E >, DoubleMappedElement > clip;

		private MySpatialSearch( final int timepoint )
		{
			this.timepoint = timepoint;
			kdtree = KDTree.kdtree( getSpots( timepoint ), vertexPool );
			clip = kdtree == null ? null : new ClipConvexPolytopeKDTree< OverlayVertexWrapper< V, E >, DoubleMappedElement >( kdtree );
		}

		@Override
		public void clip( final ConvexPolytope polytope )
		{
			if ( clip != null )
				clip.clip( polytope );
		}

		@Override
		public Iterable< OverlayVertexWrapper< V, E > > getInsideVertices()
		{
			if ( clip != null )
				return clip.getInsideValues();
			else
				return null;
		}
	}

	@Override
	public OverlayEdgeWrapper< V, E > getEdge( final OverlayVertexWrapper< V, E > source, final OverlayVertexWrapper< V, E > target )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OverlayEdgeWrapper< V, E > getEdge( final OverlayVertexWrapper< V, E > source, final OverlayVertexWrapper< V, E > target, final OverlayEdgeWrapper< V, E > edge )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator< OverlayVertexWrapper< V, E > > vertexIterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator< OverlayEdgeWrapper< V, E > > edgeIterator()
	{
		// TODO Auto-generated method stub
		return null;
	}
}