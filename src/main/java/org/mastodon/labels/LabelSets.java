package org.mastodon.labels;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefSet;
import org.mastodon.collection.ref.RefIntHashMap;
import org.mastodon.collection.ref.RefSetImp;
import org.mastodon.properties.AbstractProperty;
import org.mastodon.properties.IntPropertyMap;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

public class LabelSets< O, T > extends AbstractProperty< O >
{
	private final RefPool< O > pool;

	final IntPropertyMap< O > backingProperty;

	final LabelMapping< T > mapping;

	/**
	 * Maps label to set of objects currently having the label.
	 */
	private final ConcurrentHashMap< T, RefSet< O > > labelToObjects;

	/**
	 * Maps object whose label set is changing to the index of the label set before the change.
	 * This is used in maintaining {@code labelToObjects} maps.
	 * Mappings are added in {@code beforePropertyChange} and removed in {@code propertyChanged}.
	 */
	private final RefIntMap< O > changingObjToOldSetIndex;

	/**
	 * Reusable {@code LabelSet} ref objects.
	 */
	private final ConcurrentLinkedQueue< LabelSet< O, T> > tmpObjRefs;

	public LabelSets( final RefPool< O > pool )
	{
		this.pool = pool;
		backingProperty = new IntPropertyMap< O >( pool, 0 )
		{
			@Override
			public void beforeDeleteObject( final O obj )
			{
				LabelSets.this.beforeDeleteObject( obj );
				super.beforeDeleteObject( obj );
			}
		};
		backingProperty.addBeforePropertyChangeListener( this::beforePropertyChange );
		backingProperty.addPropertyChangeListener( this::propertyChanged );
		mapping = new LabelMapping<>();
		labelToObjects = new ConcurrentHashMap<>();
		tmpObjRefs = new ConcurrentLinkedQueue<>();
		changingObjToOldSetIndex = new RefIntHashMap<>( pool, -1 );
	}

	public LabelSet< O, T > getLabels( final O obj )
	{
		return getLabels( obj, createRef() );
	}

	public LabelSet< O, T > getLabels( final O obj, final LabelSet< O, T > ref )
	{
		ref.update( this, pool.getObject( pool.getId( obj ), ref.ref ) );
		return ref;
	}

	public LabelSet< O, T > createRef()
	{
		return createRef( true );
	}

	public LabelSet< O, T > createRef( final boolean recycle )
	{
		if ( recycle )
		{
			final LabelSet< O, T > obj = tmpObjRefs.poll();
			return obj == null ? createEmptyRef() : obj;
		}
		else
			return createEmptyRef();
	}

	public void releaseRef( final LabelSet< O, T > obj )
	{
		tmpObjRefs.add( obj );
	}

	@Override
	public boolean isSet( final O key )
	{
		return backingProperty.isSet( key );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return backingProperty.createUndoRedoStack();
	}

	private LabelSet< O, T > createEmptyRef()
	{
		return new LabelSet<>( mapping, pool.createRef(), backingProperty, this );
	}

	public RefSet< O > getLabeledWith( final T label )
	{
		return labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) );
	}

	/**
	 * For internal use. Needed for serialization.
	 */
	public IntPropertyMap< O > getBackingProperty()
	{
		return backingProperty;
	}

	/**
	 * For internal use. Needed for serialization.
	 */
	public LabelMapping< T > getLabelMapping()
	{
		return mapping;
	}

	/**
	 * For internal use. Needed for serialization.
	 */
	public RefPool< O > getPool()
	{
		return pool;
	}

	/**
	 * For internal use. Needed for serialization.
	 */
	public void recomputeLabelToObjects( final RefCollection< O > objects )
	{
		labelToObjects.clear();
		for ( final O obj : objects )
		{
			final Set< T > labels = mapping.setAtIndex( backingProperty.get( obj ) ).getSet();
			for ( final T label : labels )
				labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).add( obj );
		}
	}

	private void beforeDeleteObject( final O obj )
	{
		final Set< T > labels = mapping.setAtIndex( backingProperty.get( obj ) ).getSet();
		for ( final T label : labels )
			labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).remove( obj );
	}

	private void beforePropertyChange( final O obj )
	{
		changingObjToOldSetIndex.put( obj, backingProperty.get( obj ) );
		notifyBeforePropertyChange( obj );
	}

	private void propertyChanged( final O obj )
	{
		final int fromIndex = changingObjToOldSetIndex.remove( obj );
		final LabelMapping< T >.Diff diff = mapping.diff( fromIndex, backingProperty.get( obj ) );
		for ( final T label : diff.getAddedLabels() )
			labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).add( obj );
		for ( final T label : diff.getRemovedLabels() )
			labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).remove( obj );
		notifyPropertyChanged( obj );
	}
}
