package org.mastodon.labels;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.mastodon.properties.IntPropertyMap;

/**
 * The {@link LabelSet} represents a labeling of an object with zero or more
 * labels of type T.
 * 
 * @param <O>
 *            the type of objects.
 * @param <T>
 *            the desired type of the labels, for instance {@link Integer} or
 *            {@link String}.
 *
 * @author Lee Kamentsky
 * @author Tobias Pietzsch
 */
public class LabelSet< O, T > implements Set< T >
{
	final O ref;

	private O obj;

	private LabelSets< O, T > pool;

	public LabelSet( final LabelMapping< T > mapping, final O ref, final IntPropertyMap< O > backingProperty, final LabelSets< O, T > pool )
	{
		this.ref = ref;
		this.obj = ref;
		this.pool = pool;
	}

	public void set( final LabelSet< O, T > c )
	{
		if ( c.pool == pool )
			setIndex( c.getIndex() );
		else
			setIndex( pool.mapping.intern( c ).index );
	}

	@Override
	public String toString()
	{
		return pool.mapping.setAtIndex( getIndex() ).set.toString();
	}

	@Override
	public boolean add( final T label )
	{
		final int index = getIndex();
		final int newindex = pool.mapping.addLabelToSetAtIndex( label, index ).index;
		if ( newindex == index )
			return false;
		setIndex( newindex );
		return true;
	}

	@Override
	public boolean addAll( final Collection< ? extends T > c )
	{
		final int index = getIndex();
		int newindex = index;
		for ( final T label : c )
			newindex = pool.mapping.addLabelToSetAtIndex( label, newindex ).index;
		if ( newindex == index )
			return false;
		setIndex( newindex );
		return true;
	}

	@Override
	public void clear()
	{
		final int index = getIndex();
		final int newindex = pool.mapping.emptySet().index;
		if ( newindex != index )
			setIndex( newindex );
	}

	@Override
	public boolean contains( final Object label )
	{
		return pool.mapping.setAtIndex( getIndex() ).set.contains( label );
	}

	@Override
	public boolean containsAll( final Collection< ? > labels )
	{
		return pool.mapping.setAtIndex( getIndex() ).set.containsAll( labels );
	}

	@Override
	public boolean isEmpty()
	{
		return pool.mapping.setAtIndex( getIndex() ).set.isEmpty();
	}

	/**
	 * Note: the returned iterator reflects the label set at the time this
	 * method was called. Subsequent changes to the position of the
	 * {@code LabelSet} or the label set are not reflected!
	 */
	@Override
	public Iterator< T > iterator()
	{
		final Iterator< T > iter = pool.mapping.setAtIndex( getIndex() ).set.iterator();
		return new Iterator< T >()
		{
			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}

			@Override
			public T next()
			{
				return iter.next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean remove( final Object label )
	{
		final int index = getIndex();
		final int newindex = pool.mapping.removeLabelFromSetAtIndex( ( T ) label, index ).index;
		if ( newindex == index )
			return false;
		setIndex( newindex );
		return true;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean removeAll( final Collection< ? > c )
	{
		final int index = getIndex();
		int newindex = index;
		for ( final T label : ( Collection< ? extends T > ) c )
			newindex = pool.mapping.removeLabelFromSetAtIndex( label, newindex ).index;
		if ( newindex == index )
			return false;
		setIndex( newindex );
		return true;
	}

	@Override
	public boolean retainAll( final Collection< ? > c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		return pool.mapping.setAtIndex( getIndex() ).set.size();
	}

	@Override
	public Object[] toArray()
	{
		return pool.mapping.setAtIndex( getIndex() ).set.toArray();
	}

	@Override
	public < T1 > T1[] toArray( final T1[] a )
	{
		return pool.mapping.setAtIndex( getIndex() ).set.toArray( a );
	}

	@Override
	public int hashCode()
	{
		return pool.mapping.setAtIndex( getIndex() ).hashCode;
	}

	@Override
	public boolean equals( final Object object )
	{
		if ( object instanceof LabelSet )
		{
			@SuppressWarnings( "unchecked" )
			final LabelSet< O, T > c = ( LabelSet< O, T > ) object;
			if ( c.pool == pool )
				return c.getIndex() == getIndex();
		}
		return pool.mapping.setAtIndex( getIndex() ).set.equals( object );
	}

	/**
	 * Set the object whose labels this {@code LabelSet} currently represents.
	 *
	 * @param pool
	 * @param obj
	 */
	void update( final LabelSets< O, T > pool, final O obj )
	{
		this.pool = pool;
		this.obj = obj;
	}

	/**
	 * Get the mapping index of {@code obj} from the {@code backingProperty}.
	 *
	 * @return the mapping index of the set currently associated with {@code obj}
	 */
	private Integer getIndex()
	{
		return pool.backingProperty.get( obj );
	}

	/**
	 * Set the mapping index of {@code obj} in the {@code backingProperty}.
	 */
	private void setIndex( final int index )
	{
		pool.backingProperty.set( obj, index );
	}
}
