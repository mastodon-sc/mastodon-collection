package org.mastodon.collection.util;

import java.util.NoSuchElementException;

import org.mastodon.RefPool;

import gnu.trove.impl.Constants;
import gnu.trove.map.TIntObjectArrayMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * A {@link RefPool} implementation for object graphs that maintains a mapping
 * between objects and {@code int} IDs.
 * <p>
 * IDs are assigned to objects when first requested ({@link #getId(Object)}).
 *
 * @param <O>
 *            the type of objects for which the map is defined.
 *
 * @author Tobias Pietzsch
 */
public class HashBimap< O > implements RefPool< O >
{
	private static final int NO_ENTRY_VALUE = -1;

	private final Class< O > klass;

	private final TIntObjectMap< O > idToObj;

	private final TObjectIntMap< O > objToId;

	private int idgen;

	public HashBimap( final Class< O > klass )
	{
		this.klass = klass;
		idToObj = new TIntObjectArrayMap<>();
		objToId = new TObjectIntHashMap<>( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, NO_ENTRY_VALUE );
		idgen = 0;
	}

	@Override
	public O getObject( final int id, final O obj )
	{
		final O o = idToObj.get( id );
		if ( o == null )
			throw new NoSuchElementException();
		return o;
	}

	@Override public O getObjectIfExists( final int id, final O obj )
	{
		final O o = idToObj.get( id );
		return o;
	}

	@Override
	public int getId( final O o )
	{
		int id = objToId.get( o );
		if ( id == NO_ENTRY_VALUE )
		{
			id = idgen++;
			objToId.put( o, id );
			idToObj.put( id, o );
		}
		return id;
	}

	@Override
	public O createRef()
	{
		return null;
	}

	@Override
	public void releaseRef( final O obj )
	{}

	@Override
	public Class< O > getRefClass()
	{
		return klass;
	}
}
