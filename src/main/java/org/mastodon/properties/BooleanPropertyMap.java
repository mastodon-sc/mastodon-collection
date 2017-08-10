package org.mastodon.properties;

import java.util.BitSet;
import java.util.Iterator;

import org.mastodon.RefPool;
import org.mastodon.properties.undo.BooleanPropertyUndoRedoStack;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

/**
 * A boolean property map with facilities to get the number of objects set with
 * a <code>true</code> value and iterate over them.
 *
 * @param <O>
 *            the type of keys in the map.
 * @author Jean-Yves Tinevez
 */
public class BooleanPropertyMap< O > extends AbstractPropertyMap< O, Boolean >
{

	private final BitSet value;

	private final BitSet isSet;

	private RefPool< O > pool;

	public BooleanPropertyMap( final RefPool< O > pool )
	{
		this.pool = pool;
		this.value = new BitSet();
		this.isSet = new BitSet();
		tryRegisterPropertyMap( pool );
	}

	public BooleanPropertyMap( final RefPool< O > pool, final int initialCapacity )
	{
		this.pool = pool;
		this.value = new BitSet( initialCapacity );
		this.isSet = new BitSet( initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public boolean set( final O key, final boolean val )
	{
		notifyBeforePropertyChange( key );
		final int id = pool.getId( key );
		final boolean old = value.get( id );
		value.set( id, val );
		isSet.set( id );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public Boolean set( final O key, final Boolean value )
	{
		final boolean wasSet = isSet.get( pool.getId( key ) );
		return wasSet ? Boolean.valueOf( set( key, value.booleanValue() ) ) : null;
	}

	@Override
	public Boolean remove( final O key )
	{
		notifyBeforePropertyChange( key );
		final int id = pool.getId( key );
		final boolean wasSet = isSet.get( id );
		final boolean old = value.get( id );
		isSet.clear( id );
		value.clear( id );
		notifyPropertyChanged( key );
		return wasSet ? Boolean.valueOf( old ) : null;
	}

	public boolean removeBoolean( final O key )
	{
		notifyBeforePropertyChange( key );
		final int id = pool.getId( key );
		final boolean old = value.get( id );
		isSet.clear( id );
		notifyPropertyChanged( key );
		return old;
	}

	public boolean getBoolean( final O key )
	{
		return value.get( pool.getId( key ) );
	}

	@Override
	public Boolean get( final O key )
	{
		return value.get( pool.getId( key ) );
	}

	@Override
	public boolean isSet( final O key )
	{
		return isSet.get( pool.getId( key ) );
	}

	@Override
	public int size()
	{
		return isSet.cardinality();
	}

	/**
	 * Returns the number of mappings that are set with a <code>true</code>
	 * value.
	 *
	 * @return
	 */
	public int nTrue()
	{
		return value.cardinality();
	}

	/**
	 * Returns an iterator than only iterates over the keys of this map that are
	 * set and have a <code>true</code> value.
	 *
	 * @return a new iterator.
	 */
	public Iterator< O > trueValueIterator()
	{
		return new TrueValueIterator();
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		isSet.clear( pool.getId( key ) );
		value.clear( pool.getId( key ) );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return new BooleanPropertyUndoRedoStack<>( this );
	}

	@Override
	public void beforeClearPool()
	{
		clear();
	}

	@Override
	public void clear()
	{
		isSet.clear();
		value.clear();
	}

	private class TrueValueIterator implements Iterator< O >
	{

		private int id;

		private final O ref = pool.createRef();

		public TrueValueIterator()
		{
			id = value.nextSetBit( 0 );
		}

		@Override
		public boolean hasNext()
		{
			return id != -1;
		}

		@Override
		public O next()
		{
			final int next = id;
			id = value.nextSetBit( id );
			return pool.getObject( next, ref );
		}
	}
}
