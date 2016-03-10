package net.trackmate.revised.bdv.overlay;

import java.util.concurrent.locks.Lock;

import net.imglib2.algorithm.kdtree.ConvexPolytope;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.TransformListener;
import net.trackmate.revised.context.Context;
import net.trackmate.revised.context.ContextListener;
import net.trackmate.spatial.ClipConvexPolytope;
import net.trackmate.spatial.SpatioTemporalIndex;

public class OverlayContext< V extends OverlayVertex< V, ? > > implements
		Context< V >,
		TransformListener< AffineTransform3D >
{
	private final OverlayGraph< V, ? > graph;

	private final SpatioTemporalIndex< V > index;

	private final OverlayGraphRenderer< V, ? > renderer;

	private ContextListener< V > contextListener = null;

	public OverlayContext(
			final OverlayGraph< V, ? > overlayGraph,
			final OverlayGraphRenderer< V, ? > renderer )
	{
		this.graph = overlayGraph;
		this.index = graph.getIndex();
		this.renderer = renderer;
	}

	@Override
	public Lock readLock()
	{
		return index.readLock();
	}

	@Override
	public Iterable< V > getInsideVertices( final int timepoint )
	{
		final ConvexPolytope visiblePolytope = renderer.getVisiblePolytopeGlobal( transform, timepoint );
		final ClipConvexPolytope< V > ccp = index.getSpatialIndex( timepoint ).getClipConvexPolytope();
		ccp.clip( visiblePolytope );
		return ccp.getInsideValues();
	}

	private final AffineTransform3D transform = new AffineTransform3D();

	@Override
	public void transformChanged( final AffineTransform3D t )
	{
		transform.set( t );
		if ( contextListener != null )
			contextListener.contextChanged( this );
	}

	public void setContextListener( final ContextListener< V > contextListener )
	{
		this.contextListener = contextListener;
	}
}
