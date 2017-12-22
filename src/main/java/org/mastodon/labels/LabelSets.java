package org.mastodon.labels;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mastodon.RefPool;
import org.mastodon.collection.RefSet;
import org.mastodon.collection.ref.RefSetImp;
import org.mastodon.properties.IntPropertyMap;

import gnu.trove.map.hash.TObjectIntHashMap;

public class LabelSets< O, T >
{
	private final RefPool< O > pool;

	final IntPropertyMap< O > backingProperty;

	final LabelMapping< T > mapping;

	private final ConcurrentHashMap< T, RefSet< O > > labelToObjects;

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

	private LabelSet< O, T > createEmptyRef()
	{
		return new LabelSet<>( mapping, pool.createRef(), backingProperty, this );
	}

	public RefSet< O > allObjectsContaining( final T label )
	{
		return labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) );
	}

	private void beforeDeleteObject( final O obj )
	{
		for ( final T label : labelsFor( obj ) )
			labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).remove( obj );
	}

	private final TObjectIntHashMap< O > changing = new TObjectIntHashMap<>();

	private void beforePropertyChange( final O obj )
	{
		changing.put( obj, backingProperty.get( obj ) );
	}

	private void propertyChanged( final O obj )
	{
		final int fromIndex = changing.remove( obj );
		final LabelMapping< T >.Diff diff = mapping.diff( fromIndex, backingProperty.get( obj ) );
		for ( final T label : diff.getAddedLabels() )
			labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).add( obj );
		for ( final T label : diff.getRemovedLabels() )
			labelToObjects.computeIfAbsent( label, k -> new RefSetImp<>( pool ) ).remove( obj );
	}

	private Set< T > labelsFor( final O obj )
	{
		return mapping.setAtIndex( backingProperty.get( obj ) ).getSet();
	}
}
