package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.ref.RefIntHashMap;
import org.mastodon.properties.undo.IntPropertyUndoRedoStack;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

import gnu.trove.map.TObjectIntMap;

public class IntPropertyMap< O > extends AbstractPropertyMap< O, Integer >
{
	private final TObjectIntMap< O > map;

	private final int noEntryValue;

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue )
	{
		map = RefCollections.createRefIntMap( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefCollection< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = RefCollections.createRefIntMap( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue )
	{
		map = new RefIntHashMap<>( pool, noEntryValue );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyMap( final RefPool< O > pool, final int noEntryValue, final int initialCapacity )
	{
		map = new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		this.noEntryValue = noEntryValue;
		tryRegisterPropertyMap( pool );
	}

	public int set( final O key, final int value )
	{
		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public Integer set( final O key, final Integer value )
	{
		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	@Override
	public Integer remove( final O key )
	{
		notifyBeforePropertyChange( key );
		final int old = map.remove( key );
		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	public int removeInt( final O key )
	{
		notifyBeforePropertyChange( key );
		final int old = map.remove( key );
		notifyPropertyChanged( key );
		return old;
	}

	public int getInt( final O key )
	{
		return map.get( key );
	}

	@Override
	public Integer get( final O key )
	{
		return map.get( key );
	}

	@Override
	public boolean isSet( final O key )
	{
		return map.containsKey( key );
	}

	@Override
	public int size()
	{
		return map.size();
	}

	public void release()
	{
		map.clear();
		tryUnregisterPropertyMap();
	}

	public int getNoEntryValue()
	{
		return noEntryValue;
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		map.remove( key );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return new IntPropertyUndoRedoStack<>( this );
	}

	@Override
	public void beforeClearPool()
	{
		map.clear();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException( "TODO" );
	}
}
