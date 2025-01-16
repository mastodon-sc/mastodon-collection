/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * The LabelMapping maps a set of labels of an object to an index value which
 * can be more compactly stored than the set of labels. It provides an
 * {@link #intern(Set)} function that supplies a canonical object for each set
 * of labels, and functions {@link #addLabelToSetAtIndex(Object, int)},
 * {@link #removeLabelFromSetAtIndex(Object, int)} for efficiently adding and
 * removing labels to the set at a given index value.
 *
 * @param <T>
 *            the desired type of the labels, for instance {@link Integer} or
 *            {@link String}.
 *
 * @author Lee Kamentsky
 * @author Tobias Pietzsch
 */
public class LabelMapping< T >
{
	private static final int INT_NO_ENTRY_VALUE = -1;

	/**
	 * Maximum number of distinct label sets that can be represented by this
	 * mapping.
	 */
	private static final int MAX_NUM_LABEL_SETS = Integer.MAX_VALUE;

	/**
	 * TODO
	 */
	private final HashMap< Set< T >, InternedSet< T > > internedSets;

	/**
	 * Maps indices to {@link InternedSet} (canonical label sets).
	 * {@code setsByIndex.get( i ).index == i} holds.
	 */
	private final ArrayList< InternedSet< T > > setsByIndex;

	/**
	 * Lookup tables for adding labels. Assume that by adding label <em>L</em>
	 * to label set <em>S</em> we obtain <em>S' = S &cup; {L}</em>.
	 * {@code addMapsByIndex} contains at index of set <em>S</em> a map from
	 * <em>L</em> to index of <em>S'</em>.
	 *
	 * <p>
	 * When a new <em>(L,S)</em> combination occurs for the first time in
	 * {@link #addLabelToSetAtIndex(Object, int)}, it is added to the lookup
	 * table.
	 */
	private final ArrayList< TObjectIntMap< T > > addMapsByIndex;

	/**
	 * Lookup tables for removing labels. Assume that by removing label
	 * <em>L</em> from label set <em>S</em> we obtain <em>S' = S &setminus;
	 * {L}</em>. {@code subMapsByIndex} contains at index of set <em>S</em> a
	 * map from <em>L</em> to index of <em>S'</em>.
	 *
	 * <p>
	 * When a new <em>(L,S)</em> combination occurs for the first time in
	 * {@link #removeLabelFromSetAtIndex(Object, int)}, it is added to the
	 * lookup table.
	 */
	private final ArrayList< TObjectIntMap< T > > subMapsByIndex;

	/**
	 * the empty label set.
	 */
	private final InternedSet< T > theEmptySet;

	/**
	 * Create a new {@code LabelMapping} that maps label sets to {@code int}s.
	 */
	LabelMapping()
	{
		internedSets = new HashMap<>();
		setsByIndex = new ArrayList<>();
		addMapsByIndex = new ArrayList<>();
		subMapsByIndex = new ArrayList<>();

		final HashSet< T > background = new HashSet<>( 0 );
		theEmptySet = intern( background );

		cachedDiffs = new TIntObjectHashMap<>();
	}

	void clear()
	{
		// clear everything
		internedSets.clear();
		setsByIndex.clear();
		addMapsByIndex.clear();
		subMapsByIndex.clear();
		cachedDiffs.clear();

		// add back the empty set
		setsByIndex.add( theEmptySet );
		addMapsByIndex.add( new TObjectIntHashMap< T >( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, INT_NO_ENTRY_VALUE ) );
		subMapsByIndex.add( new TObjectIntHashMap< T >( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, INT_NO_ENTRY_VALUE ) );
		internedSets.put( theEmptySet.getSet(), theEmptySet );
	}

	/**
	 * Canonical representative for a label set. Contains a label set and the
	 * index to which it is mapped.
	 */
	static class InternedSet< T >
	{
		final Set< T > set;

		final int hashCode;

		final int index;

		public InternedSet( final Set< T > set, final int index )
		{
			this.set = set;
			this.hashCode = set.hashCode();
			this.index = index;
		}

		public Set< T > getSet()
		{
			return set;
		}

		@Override
		public int hashCode()
		{
			return hashCode;
		}

		@Override
		public boolean equals( final Object obj )
		{
			return obj == this;
		}
	}

	InternedSet< T > emptySet()
	{
		return theEmptySet;
	}

	/**
	 * Return the index value of the given set.
	 */
	int indexOf( final Set< T > key )
	{
		return intern( key ).index;
	}

	/**
	 * Return the canonical set for the given index value.
	 */
	InternedSet< T > setAtIndex( final int index )
	{
		return setsByIndex.get( index );
	}

	/**
	 * Return the canonical set for the given label set.
	 */
	InternedSet< T > intern( final Set< T > src )
	{
		InternedSet< T > interned = internedSets.get( src );
		if ( interned != null )
			return interned;

		synchronized ( this )
		{
			interned = internedSets.get( src );
			if ( interned != null )
				return interned;

			final int intIndex = setsByIndex.size();
			if ( intIndex > MAX_NUM_LABEL_SETS )
				throw new AssertionError( String.format( "Too many labels (or types of multiply-labeled pixels): %d maximum", intIndex ) );

			final HashSet< T > srcCopy = new HashSet<>( src );
			interned = new InternedSet<>( srcCopy, intIndex );
			setsByIndex.add( interned );
			addMapsByIndex.add( new TObjectIntHashMap< T >( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, INT_NO_ENTRY_VALUE ) );
			subMapsByIndex.add( new TObjectIntHashMap< T >( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, INT_NO_ENTRY_VALUE ) );
			internedSets.put( srcCopy, interned );
			return interned;
		}
	}

	/**
	 * Get the canonical set obtained by adding {@code label} to the
	 * {@link #setAtIndex(int) set at index} {@code index}.
	 */
	InternedSet< T > addLabelToSetAtIndex( final T label, final int index )
	{
		final TObjectIntMap< T > addMap = addMapsByIndex.get( index );
		int i = addMap.get( label );
		if ( i != INT_NO_ENTRY_VALUE )
			return setsByIndex.get( i );

		synchronized ( this )
		{
			i = addMap.get( label );
			if ( i != INT_NO_ENTRY_VALUE )
				return setsByIndex.get( i );

			final HashSet< T > set = new HashSet<>( setsByIndex.get( index ).set );
			set.add( label );
			final InternedSet< T > interned = intern( set );
			addMap.put( label, interned.index );
			return interned;
		}
	}

	/**
	 * Get the canonical set obtained by removing {@code label} from the
	 * {@link #setAtIndex(int) set at index} {@code index}.
	 */
	InternedSet< T > removeLabelFromSetAtIndex( final T label, final int index )
	{
		final TObjectIntMap< T > subMap = subMapsByIndex.get( index );
		int i = subMap.get( label );
		if ( i != INT_NO_ENTRY_VALUE )
			return setsByIndex.get( i );

		synchronized ( this )
		{
			i = subMap.get( label );
			if ( i != INT_NO_ENTRY_VALUE )
				return setsByIndex.get( i );

			final HashSet< T > set = new HashSet<>( setsByIndex.get( index ).set );
			set.remove( label );
			final InternedSet< T > interned = intern( set );
			subMap.put( label, interned.index );
			return interned;
		}
	}

	/**
	 * Returns the number of indexed labeling sets.
	 * 
	 * @return the number of indexed labeling sets.
	 */
	public int numSets()
	{
		return setsByIndex.size();
	}

	/**
	 * Returns the (unmodifiable) set of labels for the given index value.
	 * 
	 * @param index
	 *            the index.
	 * @return the set of labels.
	 */
	// TODO: cache unmodifiable sets (in InternedSet)?
	public Set< T > labelsAtIndex( final int index )
	{
		return Collections.unmodifiableSet( setsByIndex.get( index ).set );
	}

	/**
	 * Returns the set of all labels defined in this {@code LabelMapping}.
	 * 
	 * @return the set of all labels.
	 */
	// TODO: build only once (while adding labels).
	public Set< T > getLabels()
	{
		final HashSet< T > result = new HashSet<>();
		for ( final InternedSet< T > instance : setsByIndex )
		{
			for ( final T label : instance.set )
			{
				result.add( label );
			}
		}
		return result;
	}

	/**
	 * Get the diff (elements added and elements removed) between the label set
	 * at index {@code fromIndex} and the label set at index {@code toIndex}.
	 *
	 * @param fromIndex
	 * @param toIndex
	 * @return diff between set at {@code fromIndex} and set at {@code toIndex}.
	 */
	Diff diff( final int fromIndex, final int toIndex )
	{
		TIntObjectHashMap< Diff > toIndexToDiff = cachedDiffs.get( fromIndex );
		if ( toIndexToDiff == null )
		{
			toIndexToDiff = new TIntObjectHashMap<>();
			cachedDiffs.put( fromIndex, toIndexToDiff );
		}
		Diff diff = toIndexToDiff.get( toIndex );
		if ( diff == null )
		{
			diff = new Diff( fromIndex, toIndex );
			toIndexToDiff.put( toIndex, diff );
		}
		return diff;
	}

	private final TIntObjectHashMap< TIntObjectHashMap< Diff > > cachedDiffs;

	class Diff
	{
		private final Set< T > addedLabels;

		private final Set< T > removedLabels;

		public Diff( final int fromIndex, final int toIndex )
		{
			final Set< T > fromSet = setsByIndex.get( fromIndex ).getSet();
			final Set< T > toSet = setsByIndex.get( toIndex ).getSet();
			addedLabels = new HashSet<>( toSet );
			addedLabels.removeAll( fromSet );
			removedLabels = new HashSet<>( fromSet );
			removedLabels.removeAll( toSet );
		}

		public Set< T > getAddedLabels()
		{
			return addedLabels;
		}

		public Set< T > getRemovedLabels()
		{
			return removedLabels;
		}
	}

	/**
	 * Internals. Can be derived for implementing de/serialisation of the
	 * {@link LabelMapping}.
	 * 
	 * @param <T>
	 *            the desired type of the labels, for instance {@link Integer}
	 *            or {@link String}.
	 */
	public static class SerialisationAccess< T >
	{
		private final LabelMapping< T > labelMapping;

		protected SerialisationAccess( final LabelMapping< T > labelMapping )
		{
			this.labelMapping = labelMapping;
		}

		protected List< Set< T > > getLabelSets()
		{
			final ArrayList< Set< T > > labelSets = new ArrayList<>( labelMapping.numSets() );
			for ( final InternedSet< T > interned : labelMapping.setsByIndex )
				labelSets.add( interned.getSet() );
			return labelSets;
		}

		protected void setLabelSets( final List< Set< T > > labelSets )
		{
			if ( labelSets.isEmpty() )
				throw new IllegalArgumentException( "expected non-empty list of label-sets" );

			if ( !labelSets.get( 0 ).isEmpty() )
				throw new IllegalArgumentException( "label-set at index 0 expected to be the empty label set" );

			labelMapping.clear();

			// add remaining label sets
			for ( int i = 1; i < labelSets.size(); ++i )
			{
				final Set< T > set = labelSets.get( i );
				final InternedSet< T > interned = labelMapping.intern( set );
				if ( interned.index != i )
					throw new IllegalArgumentException( "no duplicates allowed in list of label-sets" );
			}
		}
	}
}
