/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
		backingProperty.beforePropertyChangeListeners().add( this::beforePropertyChange );
		backingProperty.propertyChangeListeners().add( this::propertyChanged );
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
	 * Removes all labels from all objects and clears the mapping
	 */
	public void clear()
	{
		backingProperty.clear();
		mapping.clear();
		labelToObjects.clear();
	}

	/**
	 * For internal use. Needed for serialization.
	 * 
	 * @return the backing property.
	 */
	public IntPropertyMap< O > getBackingProperty()
	{
		return backingProperty;
	}

	/**
	 * For internal use. Needed for serialization.
	 * 
	 * @return the label mapping.
	 */
	public LabelMapping< T > getLabelMapping()
	{
		return mapping;
	}

	/**
	 * For internal use. Needed for serialization.
	 * 
	 * @return the pool of objects for which this label set is defined.
	 */
	public RefPool< O > getPool()
	{
		return pool;
	}

	/**
	 * For internal use. Needed for serialization.
	 * 
	 * @param objects
	 *            the collection of objects for which we need to recompute
	 *            labels.
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
