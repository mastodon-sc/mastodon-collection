package org.mastodon.io.labels;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefList;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.labels.LabelMapping;
import org.mastodon.labels.LabelMapping.SerialisationAccess;
import org.mastodon.labels.LabelSets;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

/*
 * TODO: PropertyMapSerializer should work for Property, not only PropertyMap
 */
public class LabelSetsSerializer< O, T > // TODO implements PropertyMapSerializer< O, LabelSets< O, T > >
{
	public interface LabelSerializer< T >
	{
		void writeLabel( final T label, final ObjectOutputStream oos ) throws IOException;

		T readLabel( final ObjectInputStream ois ) throws IOException;
	}

	public static < O, T > void writePropertyMap(
			final LabelSets< O, T > propertyMap,
			final LabelSerializer< T > labelSerializer,
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
			throws IOException
	{
		final LabelMapping< T > mapping = propertyMap.getLabelMapping();
		final RefIntMap< O > pmap = propertyMap.getBackingProperty().getMap();

		final TIntList used = new TIntArrayList();
		final TIntIntMap mappingIndexToFileIndex = new TIntIntHashMap();
		used.add( 0 );
		mappingIndexToFileIndex.put( 0, 0 );
		pmap.forEachValue( index -> {
			if ( !used.contains( index ) )
			{
				mappingIndexToFileIndex.put( index, used.size() );
				used.add( index );
			}
			return true;
		} );

		// NUMBER OF LABEL SETS
		oos.writeInt( used.size() );

		// LABEL SETS
		for ( final TIntIterator it = used.iterator(); it.hasNext(); )
		{
			final Set< T > labels = mapping.labelsAtIndex( it.next() );

			// NUMBER OF LABELS IN SET
			oos.writeInt( labels.size() );

			// LABELS
			for ( final T label : labels )
				labelSerializer.writeLabel( label, oos );
		}

		// NUMBER OF ENTRIES
		oos.writeInt( pmap.size() );

		// ENTRIES
		try
		{
			pmap.forEachEntry( ( final O key, final int value ) -> {
				try
				{
					oos.writeInt( idmap.getId( key ) );
					oos.writeInt( mappingIndexToFileIndex.get( value ) );
				}
				catch ( final IOException e )
				{
					throw new UncheckedIOException( e );
				}
				return true;
			} );
		}
		catch ( final UncheckedIOException e )
		{
			throw e.getCause();
		}
	}

	public static < O, T > void readPropertyMap(
			final LabelSets< O, T > propertyMap,
			final LabelSerializer< T > labelSerializer,
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
			throws IOException
	{
		propertyMap.clear();

		final LabelMapping< T > mapping = propertyMap.getLabelMapping();
		final RefIntMap< O > pmap = propertyMap.getBackingProperty().getMap();

		// NUMBER OF LABEL SETS
		final int numSets = ois.readInt();

		// LABEL SETS
		final ArrayList< Set< T > > labelSets = new ArrayList<>();
		for ( int i = 0; i < numSets; i++ )
		{
			// NUMBER OF LABELS IN SET
			final int numLabels = ois.readInt();

			// LABELS
			final Set< T > labels = new HashSet< T >();
			for ( int j = 0; j < numLabels; j++ )
				labels.add( labelSerializer.readLabel( ois ) );

			labelSets.add( labels );
		}

		new SerialisationAccess< T >( mapping ) {{setLabelSets( labelSets );}};

		// NUMBER OF ENTRIES
		final int size = ois.readInt();

		// ENTRIES
		final O ref = idmap.createRef();
		final RefList< O > labeled = new RefArrayList<>( propertyMap.getPool() );
		for ( int i = 0; i < size; i++ )
		{
			final int key = ois.readInt();
			final int value = ois.readInt();
			final O object = idmap.getObject( key, ref );
			labeled.add( object );
			pmap.put( object, value );
		}
		propertyMap.recomputeLabelToObjects( labeled );
		idmap.releaseRef( ref );
	}

	private final LabelSets< O, T > propertyMap;

	private final LabelSerializer< T > labelSerializer;

	public LabelSetsSerializer( final LabelSets< O, T > propertyMap, final LabelSerializer< T > labelSerializer )
	{
		this.propertyMap = propertyMap;
		this.labelSerializer = labelSerializer;
	}

	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
			throws IOException
	{
		writePropertyMap( propertyMap, labelSerializer, idmap, oos );
	}

	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
			throws IOException, ClassNotFoundException
	{
		readPropertyMap( propertyMap, labelSerializer, idmap, ois );
	}
}
