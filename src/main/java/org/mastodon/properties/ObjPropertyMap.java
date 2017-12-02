package org.mastodon.properties;

import org.mastodon.RefPool;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.ref.RefObjectHashMap;
import org.mastodon.properties.undo.ObjPropertyUndoRedoStack;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

public class ObjPropertyMap< O, T > extends AbstractPropertyMap< O, T >
{
	private final RefObjectMap< O, T > map;

	public ObjPropertyMap( final RefCollection< O > pool )
	{
		map = RefMaps.createRefObjectMap( pool );
		tryRegisterPropertyMap( pool );
	}

	public ObjPropertyMap( final RefCollection< O > pool, final int initialCapacity )
	{
		map = RefMaps.createRefObjectMap( pool, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public ObjPropertyMap( final RefPool< O > pool )
	{
		map = new RefObjectHashMap<>( pool );
		tryRegisterPropertyMap( pool );
	}

	public ObjPropertyMap( final RefPool< O > pool, final int initialCapacity )
	{
		map = new RefObjectHashMap<>( pool, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	@Override
	public T set( final O key, final T value )
	{
		notifyBeforePropertyChange( key );
		final T old = map.put( key, value );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public T remove( final O key )
	{
		notifyBeforePropertyChange( key );
		final T old = map.remove( key );
		notifyPropertyChanged( key );
		return old;
	}

	@Override
	public T get( final O key )
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

	public RefObjectMap< O, T > getMap()
	{
		return map;
	}

	public void release()
	{
		map.clear();
		tryUnregisterPropertyMap();
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		map.remove( key );
	}

	@Override
	public PropertyUndoRedoStack< O > createUndoRedoStack()
	{
		return new ObjPropertyUndoRedoStack<>( this );
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

	@Override
	public boolean equals( final Object o )
	{
		if ( this == o )
			return true;
		if ( o == null || getClass() != o.getClass() )
			return false;

		final ObjPropertyMap< ?, ? > that = ( ObjPropertyMap< ?, ? > ) o;

		return map.equals( that.map );
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}
}
