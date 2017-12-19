package org.mastodon.labels;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.mastodon.RefPool;
import org.mastodon.collection.RefSet;
import org.mastodon.properties.IntPropertyMap;

public class LabelSets< O, T >
{
	private final RefPool< O > pool;

	final IntPropertyMap< O > backingProperty;

	final LabelMapping< T > mapping;

	private final ConcurrentLinkedQueue< LabelSet< O, T> > tmpObjRefs;

	public LabelSets( final RefPool< O > pool )
	{
		this.pool = pool;
		this.backingProperty = new IntPropertyMap<>( pool, 0 );
		this.mapping = new LabelMapping<>();
		this.tmpObjRefs = new ConcurrentLinkedQueue<>();
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

	// TODO
	public RefSet< O > allObjectsContaining( final T element )
	{
		throw new UnsupportedOperationException( "TODO" );
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
}
