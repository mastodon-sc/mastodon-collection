package net.trackmate.revised.trackscheme.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.UIManager;

import bdv.viewer.TimePointListener;
import net.imglib2.ui.InteractiveDisplayCanvasComponent;
import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.PainterThread;
import net.imglib2.ui.TransformEventHandler;
import net.imglib2.ui.TransformListener;
import net.trackmate.graph.listenable.GraphChangeListener;
import net.trackmate.revised.context.Context;
import net.trackmate.revised.context.ContextListener;
import net.trackmate.revised.trackscheme.ContextLayout;
import net.trackmate.revised.trackscheme.LineageTreeLayout;
import net.trackmate.revised.trackscheme.LineageTreeLayout.LayoutListener;
import net.trackmate.revised.trackscheme.ScreenEntities;
import net.trackmate.revised.trackscheme.ScreenEntitiesInterpolator;
import net.trackmate.revised.trackscheme.ScreenTransform;
import net.trackmate.revised.trackscheme.TrackSchemeFocus;
import net.trackmate.revised.trackscheme.TrackSchemeGraph;
import net.trackmate.revised.trackscheme.TrackSchemeHighlight;
import net.trackmate.revised.trackscheme.TrackSchemeNavigation;
import net.trackmate.revised.trackscheme.TrackSchemeSelection;
import net.trackmate.revised.trackscheme.TrackSchemeVertex;
import net.trackmate.revised.trackscheme.display.TrackSchemeOptions.Values;
import net.trackmate.revised.trackscheme.display.laf.DefaultTrackSchemeOverlay;
import net.trackmate.revised.ui.selection.FocusListener;
import net.trackmate.revised.ui.selection.HighlightListener;
import net.trackmate.revised.ui.selection.NavigationEtiquette;
import net.trackmate.revised.ui.selection.NavigationListener;
import net.trackmate.revised.ui.selection.SelectionListener;
import net.trackmate.trackscheme.animate.AbstractAnimator;

public class TrackSchemePanel extends JPanel implements
		TransformListener< ScreenTransform >,
		PainterThread.Paintable,
		HighlightListener,
		FocusListener,
		TimePointListener,
		GraphChangeListener,
		SelectionListener,
		NavigationListener< TrackSchemeVertex >,
		ContextListener< TrackSchemeVertex >
{

	private static final long ANIMATION_MILLISECONDS = 250;

	private final TrackSchemeGraph< ?, ? > graph;

	/**
	 * Canvas used for displaying the trackscheme graph.
	 */
	private final InteractiveDisplayCanvasComponent< ScreenTransform > display;

	private final JScrollBar xScrollBar;

	private final JScrollBar yScrollBar;

	/**
	 * The current transform from layout to screen coordinates.
	 */
	private final ScreenTransform screenTransform;

	/**
	 * layout the {@link TrackSchemeGraph} into layout coordinates.
	 */
	private final LineageTreeLayout layout;

	/**
	 * TODO
	 */
	private final ContextLayout contextLayout;

	/**
	 * compute {@link ScreenEntities} from {@link LineageTreeLayout} using the
	 * current {@link ScreenTransform} and interpolate between
	 * {@link ScreenEntities} for animation.
	 */
	private final ScreenEntityAnimator entityAnimator;

	private final PainterThread painterThread;

	private final AbstractTrackSchemeOverlay graphOverlay;

	/**
	 * TODO
	 */
	// TODO rename
	private final Flags flags;

	/**
	 * Minimum timepoint in dataset.
	 */
	private double layoutMinY;

	/**
	 * Maximum timepoint in dataset.
	 */
	private double layoutMaxY;

	/**
	 * Minimum layoutX coordinate in current layout.
	 */
	private double layoutMinX;

	/**
	 * Maximum layoutX coordinate in current layout.
	 */
	private double layoutMaxX;

	/**
	 * Ratio of {@link #xScrollBar} values to layoutX coordinates.
	 */
	private double xScrollScale;

	/**
	 * Ratio of {@link #yScrollBar} values to layoutY (timepoint) coordinates.
	 */
	private double yScrollScale;

	/**
	 * If {@code true}, then scroll-bar {@link AdjustmentListener}s ignore
	 * events (when {@link #screenTransform} is changed by means other than the
	 * user dragging the scroll-bar).
	 */
	private boolean ignoreScrollBarChanges;

//	private final TrackSchemeSelection selection;

	private final TrackSchemeFocus focus;

	private final TrackSchemeNavigator navigator;

	private final SelectionBehaviours selectionBehaviours;

	private final OffsetHeaders offsetHeaders;

	private NavigationEtiquette navigationEtiquette;

	private NavigationBehaviour navigationBehaviour;

	public TrackSchemePanel(
			final TrackSchemeGraph< ?, ? > graph,
			final TrackSchemeHighlight highlight,
			final TrackSchemeFocus focus,
			final TrackSchemeSelection selection,
			final TrackSchemeNavigation navigation,
			final TrackSchemeOptions optional )
	{
		super( new BorderLayout(), false );
		this.graph = graph;
		this.focus = focus;

		final Values options = optional.values;

		graph.addGraphChangeListener( this );
		navigation.addNavigationListener( this );

		final int w = options.getWidth();
		final int h = options.getHeight();
		display = new InteractiveDisplayCanvasComponent< ScreenTransform >(	w, h, options.getTransformEventHandlerFactory() );
		display.addTransformListener( this );

		highlight.addHighlightListener( this );
		focus.addFocusListener( this );
		selection.addSelectionListener( this );

		graphOverlay = new DefaultTrackSchemeOverlay( graph, highlight, focus, optional );
		display.addOverlayRenderer( graphOverlay );

		// This should be the last OverlayRenderer in display.
		// It triggers repainting if there is currently an ongoing animation.
		display.addOverlayRenderer( new OverlayRenderer()
		{
			@Override
			public void setCanvasSize( final int width, final int height )
			{}

			@Override
			public void drawOverlays( final Graphics g )
			{
				checkAnimate();
			}
		} );

		screenTransform = new ScreenTransform();
		layout = new LineageTreeLayout( graph );
		contextLayout = new ContextLayout( graph, layout );
		final TransformEventHandler< ScreenTransform > tevl = display.getTransformEventHandler();
		if ( tevl instanceof LayoutListener )
		{
			final LayoutListener ll = ( LayoutListener ) tevl;
			layout.addLayoutListener( ll );
		}
		entityAnimator = new ScreenEntityAnimator();
		painterThread = new PainterThread( this );
		flags = new Flags();

		final MouseHighlightHandler highlightHandler = new MouseHighlightHandler( graphOverlay, highlight, graph );
		display.addMouseMotionListener( highlightHandler );
		display.addMouseListener( highlightHandler );
		display.addTransformListener( highlightHandler );

		navigator = new TrackSchemeNavigator( graph, layout, focus, navigation, selection );
		display.addTransformListener( navigator );

		selectionBehaviours = new SelectionBehaviours( display, graph, layout, graphOverlay, focus, navigation, selection );
		display.addTransformListener( selectionBehaviours );

		offsetHeaders = new OffsetHeaders();
		offsetHeaders.addOffsetHeadersListener( ( InertialScreenTransformEventHandler ) display.getTransformEventHandler() );
		offsetHeaders.addOffsetHeadersListener( graphOverlay );
		offsetHeaders.addOffsetHeadersListener( selectionBehaviours );
		offsetHeaders.addOffsetHeadersListener( highlightHandler );
		offsetHeaders.setHeaderVisibleX( true, 25 );
		offsetHeaders.setHeaderVisibleY( true, 20 );
//		offsetDecorations.setDecorationsVisibleY( false, 0 );

		xScrollBar = new JScrollBar( JScrollBar.HORIZONTAL );
		yScrollBar = new JScrollBar( JScrollBar.VERTICAL );
		xScrollBar.addAdjustmentListener( new AdjustmentListener()
		{
			@Override
			public void adjustmentValueChanged( final AdjustmentEvent e )
			{
				if ( ignoreScrollBarChanges )
					return;

				final double s = xScrollBar.getValue() / xScrollScale;
				synchronized ( screenTransform )
				{
					screenTransform.shiftLayoutX( s - screenTransform.getMinX() );
					display.getTransformEventHandler().setTransform( screenTransform );
				}
				flags.setTransformChanged();
				painterThread.requestRepaint();
			}
		} );
		yScrollBar.addAdjustmentListener( new AdjustmentListener()
		{
			@Override
			public void adjustmentValueChanged( final AdjustmentEvent e )
			{
				if ( ignoreScrollBarChanges )
					return;

				final double s = yScrollBar.getValue() / yScrollScale;
				synchronized ( screenTransform )
				{
					screenTransform.shiftLayoutY( s - screenTransform.getMinY() );
					display.getTransformEventHandler().setTransform( screenTransform );
				}
				flags.setTransformChanged();
				painterThread.requestRepaint();
			}
		} );


		add( display, BorderLayout.CENTER );

		add( yScrollBar, BorderLayout.EAST );
		final JPanel xScrollPanel = new JPanel( new BorderLayout() );
		xScrollPanel.add( xScrollBar, BorderLayout.CENTER );
		final int space = ( Integer ) UIManager.getDefaults().get( "ScrollBar.width" );
		xScrollPanel.add( Box.createRigidArea( new Dimension( space, 0 ) ), BorderLayout.EAST );
		add( xScrollPanel, BorderLayout.SOUTH );

		setNavigationEtiquette( options.getNavigationEtiquette() );

		painterThread.start();
	}

	/**
	 * Stop the {@link #painterThread}.
	 */
	public void stop()
	{
		painterThread.interrupt();
	}

	/**
	 * Set the timepoint range of the dataset.
	 *
	 * @param minTimepoint
	 * @param maxTimepoint
	 */
	public void setTimepointRange( final int minTimepoint, final int maxTimepoint )
	{
		layoutMinY = minTimepoint;
		layoutMaxY = maxTimepoint;
		graphOverlay.setTimepointRange( minTimepoint, maxTimepoint );

		// TODO: THIS IS FOR TESTING ONLY
		final InertialScreenTransformEventHandler teh = ( InertialScreenTransformEventHandler ) display.getTransformEventHandler();
		teh.setLayoutRangeY( minTimepoint, maxTimepoint );
	}

	/**
	 * request repainting if there is currently an ongoing animation.
	 */
	void checkAnimate()
	{
		if ( !entityAnimator.isComplete() )
			painterThread.requestRepaint();
	}

	@Override
	public void paint()
	{
		final ScreenTransform transform = new ScreenTransform();
		synchronized( screenTransform )
		{
			transform.set( screenTransform );
		}

		final Flags flags = this.flags.clear();
		if ( flags.graphChanged )
		{
//			System.out.println( "paint: graphChanged" );
			layout.layout();
			layoutMinX = layout.getCurrentLayoutMinX();
			layoutMaxX = layout.getCurrentLayoutMaxX();
			entityAnimator.startAnimation( transform, ANIMATION_MILLISECONDS );
		}
		else if ( flags.transformChanged )
		{
//			System.out.println( "paint: transformChanged" );
//			entityAnimator.startAnimation( transform, 0 );
			if ( context != null && contextLayout.buildContext( context, transform, false ) )
			{
				layoutMinX = layout.getCurrentLayoutMinX();
				layoutMaxX = layout.getCurrentLayoutMaxX();
				entityAnimator.continueAnimation( transform, ANIMATION_MILLISECONDS );
			}
			else
				entityAnimator.startAnimation( transform, 0 );
//			entityAnimator.startAnimation( transform, ANIMATION_MILLISECONDS );
		}
		else if ( flags.selectionChanged )
		{
//			System.out.println( "paint: selectionChanged" );
			entityAnimator.startAnimation( transform, ANIMATION_MILLISECONDS );
		}
		else if ( flags.contextChanged )
		{
//			System.out.println( "paint: contextChanged" );
			if ( context == null )
			{
				layout.layout();
				layoutMinX = layout.getCurrentLayoutMinX();
				layoutMaxX = layout.getCurrentLayoutMaxX();
			}
			else if ( contextLayout.buildContext( context, transform, true ) )
			{
				layoutMinX = layout.getCurrentLayoutMinX();
				layoutMaxX = layout.getCurrentLayoutMaxX();
			}
			entityAnimator.startAnimation( transform, ANIMATION_MILLISECONDS );
		}

		entityAnimator.setTime( System.currentTimeMillis() );
		entityAnimator.setPaintEntities( graphOverlay );
		display.repaint();

		// adjust scrollbars sizes
		final ScreenTransform t = new ScreenTransform();
		entityAnimator.getLastComputedScreenEntities().getScreenTransform( t );
		xScrollScale = 10000.0 / ( layoutMaxX - layoutMinX + 2 );
		final int xval = ( int ) ( xScrollScale * t.getMinX() );
		final int xext = ( int ) ( xScrollScale * ( t.getMaxX() - t.getMinX() ) );
		final int xmin = ( int ) ( xScrollScale * ( layoutMinX - 1 ) );
		final int xmax = ( int ) ( xScrollScale * ( layoutMaxX + 1 ) );
		yScrollScale = 10000.0 / ( layoutMaxY - layoutMinY + 2 );
		final int yval = ( int ) ( yScrollScale * t.getMinY() );
		final int yext = ( int ) ( yScrollScale * ( t.getMaxY() - t.getMinY() ) );
		final int ymin = ( int ) ( yScrollScale * ( layoutMinY - 1 ) );
		final int ymax = ( int ) ( yScrollScale * ( layoutMaxY + 1 ) );
		ignoreScrollBarChanges = true;
		xScrollBar.setValues( xval, xext, xmin, xmax );
		yScrollBar.setValues( yval, yext, ymin, ymax );
		ignoreScrollBarChanges = false;
	}

	@Override
	public void timePointChanged( final int timepoint )
	{
		if ( graphOverlay.getCurrentTimepoint() != timepoint )
		{
			graphOverlay.setCurrentTimepoint( timepoint );
			display.repaint();
		}
	}

	@Override
	public void transformChanged( final ScreenTransform transform )
	{
		synchronized( screenTransform )
		{
			if ( screenTransform.equals( transform ) )
				return;
//			TODO
			screenTransform.set( transform );
		}
		flags.setTransformChanged();
		painterThread.requestRepaint();
	}

	@Override
	public void graphChanged()
	{
		flags.setGraphChanged();
		painterThread.requestRepaint();
	}

	@Override
	public void highlightChanged()
	{
		display.repaint();
	}

	@Override
	public void focusChanged()
	{
		display.repaint();
	}

	@Override
	public void selectionChanged()
	{
		flags.setSelectionChanged();
		painterThread.requestRepaint();
	}



	// TODO: THIS IS FOR TESTING ONLY
	private Context< TrackSchemeVertex > context;

	// TODO: THIS IS FOR TESTING ONLY
	@Override
	public void contextChanged( final Context< TrackSchemeVertex > context )
	{
		if ( this.context == null && context == null )
			return;

		this.context = context;
		flags.setContextChanged();
		painterThread.requestRepaint();
	}

	public NavigationEtiquette getNavigationEtiquette()
	{
		return navigationEtiquette;
	}

	public void setNavigationEtiquette( final NavigationEtiquette navigationEtiquette )
	{
		this.navigationEtiquette = navigationEtiquette;

		/*
		 * TODO: This will fail if there is a different transform event
		 * handler.
		 */
		final InertialScreenTransformEventHandler transformEventHandler = ( InertialScreenTransformEventHandler ) display.getTransformEventHandler();
		switch( navigationEtiquette )
		{
		case MINIMAL:
			navigationBehaviour = new MinimalNavigationBehaviour( transformEventHandler, 100, 100 );
			break;
		case CENTER_IF_INVISIBLE:
			navigationBehaviour = new CenterIfInvisibleNavigationBehaviour( transformEventHandler );
			break;
		case CENTERING:
		default:
			navigationBehaviour = new CenteringNavigationBehaviour( transformEventHandler );
			break;
		}
	}

	@Override
	public void navigateToVertex( final TrackSchemeVertex v )
	{
		focus.focusVertex( v );
		graphOverlay.setCurrentTimepoint( v.getTimepoint() );

		final ScreenTransform transform = new ScreenTransform();
		synchronized( screenTransform )
		{
			transform.set( screenTransform );
		}
		navigationBehaviour.navigateToVertex( v, transform );
	}

	/**
	 * TODO: Let NavigationHandler.navigateToVertex return a target transform
	 * instead of talking to the TransformEventHandler directly.
	 */
	interface NavigationBehaviour
	{
		public void navigateToVertex( final TrackSchemeVertex v, final ScreenTransform currentTransform );
	}

	private static class CenteringNavigationBehaviour implements NavigationBehaviour
	{
		private final InertialScreenTransformEventHandler transformEventHandler;

		public CenteringNavigationBehaviour( final InertialScreenTransformEventHandler transformEventHandler )
		{
			this.transformEventHandler = transformEventHandler;
		}

		@Override
		public void navigateToVertex( final TrackSchemeVertex v, final ScreenTransform currentTransform )
		{
			final double lx = v.getLayoutX();
			final double ly = v.getTimepoint();
			transformEventHandler.centerOn( lx, ly );
		}
	}

	private static class CenterIfInvisibleNavigationBehaviour implements NavigationBehaviour
	{
		private final InertialScreenTransformEventHandler transformEventHandler;

		public CenterIfInvisibleNavigationBehaviour( final InertialScreenTransformEventHandler transformEventHandler )
		{
			this.transformEventHandler = transformEventHandler;
		}

		// With CENTER_IF_INVISIBLE etiquette, only navigate to the specified vertex if not
		// is currently displayed.
		@Override
		public void navigateToVertex( final TrackSchemeVertex v, final ScreenTransform currentTransform )
		{
			final double lx = v.getLayoutX();
			final double ly = v.getTimepoint();
			if ( currentTransform.getMaxX() < lx || currentTransform.getMinX() > lx
					|| currentTransform.getMaxY() < ly || currentTransform.getMinY() > ly )
			{
				transformEventHandler.centerOn( lx, ly );
			}
		}
	}

	private static class MinimalNavigationBehaviour implements NavigationBehaviour
	{
		private final InertialScreenTransformEventHandler transformEventHandler;

		private final int screenBorderX;

		private final int screenBorderY;

		public MinimalNavigationBehaviour( final InertialScreenTransformEventHandler transformEventHandler, final int screenBorderX, final int screenBorderY )
		{
			this.transformEventHandler = transformEventHandler;
			this.screenBorderX = screenBorderX;
			this.screenBorderY = screenBorderY;
		}

		@Override
		public void navigateToVertex( final TrackSchemeVertex v, final ScreenTransform currentTransform )
		{
			final double lx = v.getLayoutX();
			final double ly = v.getTimepoint();

			/*
			 * TODO: check for compatibility of screenBorder and screenWidth,
			 * screenHeight. Fall back to CENTER_IF_INVISIBLE if screen size too
			 * small.
			 */
//			final int screenWidth = currentTransform.getScreenWidth();
//			final int screenHeight = currentTransform.getScreenHeight();

			final double minX = currentTransform.getMinX();
			final double maxX = currentTransform.getMaxX();
			final double minY = currentTransform.getMinY();
			final double maxY = currentTransform.getMaxY();
			final double bx = screenBorderX / currentTransform.getScaleX();
			final double by = screenBorderY / currentTransform.getScaleY();

			double sx = 0;
			if ( lx > maxX - bx )
				sx = lx - maxX + bx;
			else if ( lx < minX + bx )
				sx = lx - minX - bx;
			double sy = 0;
			if ( ly > maxY - by )
				sy = ly - maxY + by;
			else if ( ly < minY + by )
				sy = ly - minY - by;

			if ( sx != 0 || sy != 0 )
			{
				final double cx = ( minX + maxX ) / 2 + sx;
				final double cy = ( minY + maxY ) / 2 + sy;
				transformEventHandler.centerOn( cx, cy );
			}
		}
	}

	// TODO remove??? revise TrackSchemePanel / TrackSchemeFrame construction.
	protected InteractiveDisplayCanvasComponent< ScreenTransform > getDisplay()
	{
		return display;
	}

	// TODO remove??? revise TrackSchemePanel / TrackSchemeFrame construction.
	protected OffsetHeaders getOffsetDecorations()
	{
		return offsetHeaders;
	}

	// TODO is this needed? does it have to be public?
	public LineageTreeLayout getLineageTreeLayout()
	{
		return layout;
	}

	// TODO is this needed? does it have to be public?
	public TrackSchemeGraph< ?, ? > getGraph()
	{
		return graph;
	}

	// TODO remove. revise TrackSchemePanel / TrackSchemeFrame construction
	public TrackSchemeNavigator getNavigator()
	{
		return navigator;
	}

	// TODO remove. revise TrackSchemePanel / TrackSchemeFrame construction
	public SelectionBehaviours getSelectionBehaviours()
	{
		return selectionBehaviours;
	}

	class ScreenEntityAnimator extends AbstractAnimator
	{
		private ScreenEntities screenEntities;

		private ScreenEntities screenEntities2;

		private final ScreenEntities screenEntitiesIpStart;

		private ScreenEntities screenEntitiesIpEnd;

		private ScreenEntitiesInterpolator interpolator;

		private ScreenEntities lastComputedScreenEntities;

		private final int capacity = 1000;

		public ScreenEntityAnimator()
		{
			super( 0 );
			screenEntities = new ScreenEntities( graph, capacity );
			screenEntities2 = new ScreenEntities( graph, capacity );
			screenEntitiesIpStart = new ScreenEntities( graph, capacity );
			screenEntitiesIpEnd = new ScreenEntities( graph, capacity );
			interpolator = null;
			lastComputedScreenEntities = screenEntities;
		}

		/**
		 * Swap screenEntities and screenEntities2.
		 */
		private void swapPools()
		{
			final ScreenEntities tmp = screenEntities;
			screenEntities = screenEntities2;
			screenEntities2 = tmp;
			screenEntities.clear();
		}

		/**
		 * Swap screenEntities and screenEntitiesIpStart.
		 */
		private void copyIpStart()
		{
			screenEntitiesIpStart.set( lastComputedScreenEntities );
			screenEntities.clear();
		}

		/**
		 * Swap screenEntities and screenEntitiesIpEnd.
		 */
		private void swapIpEnd()
		{
			final ScreenEntities tmp = screenEntities;
			screenEntities = screenEntitiesIpEnd;
			screenEntitiesIpEnd = tmp;
			screenEntities.clear();
		}

		/**
		 *
		 * @param transform
		 * @param duration animation duration (in time units), may be 0.
		 */
		public void startAnimation( final ScreenTransform transform, final long duration )
		{
			reset( duration );
			if (duration > 0 )
			{
				copyIpStart();
				layout.cropAndScale( transform, screenEntities, offsetHeaders.getWidth(), offsetHeaders.getHeight() );
				swapIpEnd();
				interpolator = new ScreenEntitiesInterpolator( screenEntitiesIpStart, screenEntitiesIpEnd );
			}
			else
			{
				interpolator = null;
				swapPools();
				layout.cropAndScale( transform, screenEntities, offsetHeaders.getWidth(), offsetHeaders.getHeight() );
				lastComputedScreenEntities = screenEntities;
			}
		}

		public void continueAnimation( final ScreenTransform transform, final long duration )
		{
			if ( interpolator != null )
			{
				layout.cropAndScale( transform, screenEntities, offsetHeaders.getWidth(), offsetHeaders.getHeight() );
				swapIpEnd();
				interpolator = new ScreenEntitiesInterpolator(
						screenEntitiesIpStart,
						screenEntitiesIpEnd,
						ScreenEntitiesInterpolator.getIncrementalY( screenEntitiesIpStart, screenEntitiesIpEnd ) );
			}
			else
			{
				startAnimation( transform, duration );
//				swapPools();
//				layout.cropAndScale( transform, screenEntities );
//				lastComputedScreenEntities = screenEntities;
			}
		}

		@Override
		public void setTime( final long time )
		{
			super.setTime( time );
			if ( interpolator != null )
			{
				swapPools();
				interpolator.interpolate( ratioComplete(), screenEntities );
				if ( isComplete() )
					interpolator = null;
				lastComputedScreenEntities = screenEntities;
			}
		}

		/**
		 * Set entities for painting into the specified double-buffered
		 * {@link AbstractTrackSchemeOverlay}. (This swaps
		 * {@link #screenEntities} with pending entities from the overlay.)
		 */
		public void setPaintEntities( final AbstractTrackSchemeOverlay overlay )
		{
			if ( lastComputedScreenEntities == screenEntities )
			{
				final ScreenEntities tmp = overlay.setScreenEntities( screenEntities );
				if ( tmp == null )
				{
					screenEntities = new ScreenEntities( graph, capacity );
				}
				else
					screenEntities = tmp;
				screenEntities.clear();
			}
		}

		private ScreenEntities getLastComputedScreenEntities()
		{
			return lastComputedScreenEntities;
		}
	}

	static class Flags
	{
		private boolean transformChanged;
		private boolean selectionChanged;
		private boolean graphChanged;
		private boolean contextChanged;

		public Flags()
		{
			transformChanged = false;
			selectionChanged = false;
			graphChanged = false;
			contextChanged = false;
		}

		public Flags( final Flags f )
		{
			transformChanged = f.transformChanged;
			selectionChanged = f.selectionChanged;
			graphChanged = f.graphChanged;
			contextChanged = f.contextChanged;
		}

		public synchronized void setTransformChanged()
		{
			transformChanged = true;
		}

		public synchronized void setSelectionChanged()
		{
			selectionChanged = true;
		}

		public synchronized void setGraphChanged()
		{
			graphChanged = true;
		}

		public synchronized void setContextChanged()
		{
			contextChanged = true;
		}

		public synchronized Flags clear()
		{
			final Flags copy = new Flags( this );
			transformChanged = false;
			selectionChanged = false;
			graphChanged = false;
			contextChanged = false;
			return copy;
		}

		@Override
		public String toString()
		{
			final StringBuilder str = new StringBuilder(super.toString());
			str.append( '\n' );
			str.append( "  - transformChanged: " + transformChanged + "\n" );
			str.append( "  - selectionChanged: " + selectionChanged + "\n" );
			str.append( "  - graphChanged:     " + graphChanged + "\n" );
			str.append( "  - contextChanged:   " + contextChanged + "\n" );
			return str.toString();
		}
	}
}
