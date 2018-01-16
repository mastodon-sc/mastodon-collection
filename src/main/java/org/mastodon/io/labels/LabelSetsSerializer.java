package org.mastodon.io.labels;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mastodon.collection.RefIntMap;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.labels.LabelMapping;
import org.mastodon.labels.LabelMapping.SerialisationAccess;
import org.mastodon.labels.LabelSets;
import org.mastodon.properties.IntPropertyMap;

/*
 * TODO: PropertyMapSerializer should work for Property, not only PropertyMap
 */
public class LabelSetsSerializer< O, T > // TODO implements PropertyMapSerializer< O, LabelSets< O, T > >
{
	private final LabelSets< O, T > propertyMap;

	private final LabelSerializer< T > labelSerializer;

	public interface LabelSerializer< T >
	{
		void writeLabel( final T label, final ObjectOutputStream oos ) throws IOException;

		T readLabel( final ObjectInputStream ois ) throws IOException;
	}

	public LabelSetsSerializer( final LabelSets< O, T > propertyMap, LabelSerializer< T > labelSerializer )
	{
		this.propertyMap = propertyMap;
		this.labelSerializer = labelSerializer;
	}

	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
			throws IOException
	{
		final LabelMapping< T > mapping = propertyMap.getLabelMapping();
		final RefIntMap< O > pmap = propertyMap.getBackingProperty().getMap();

		TIntList used = new TIntArrayList();
		TIntIntMap mappingIndexToFileIndex = new TIntIntHashMap();
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
		for ( TIntIterator it = used.iterator(); it.hasNext(); )
		{
			Set< T > labels = mapping.labelsAtIndex( it.next() );

			// NUMBER OF LABELS IN SET
			oos.writeInt( labels.size() );

			// LABELS
			for ( T label : labels )
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

	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
			throws IOException, ClassNotFoundException
	{

		final LabelMapping< T > mapping = propertyMap.getLabelMapping();
		final RefIntMap< O > pmap = propertyMap.getBackingProperty().getMap();
		pmap.clear();

		// NUMBER OF LABEL SETS
		final int numSets = ois.readInt();

		// LABEL SETS
		ArrayList< Set< T > > labelSets = new ArrayList<>();
		for ( int i = 0; i < numSets; i++ )
		{
			// NUMBER OF LABELS IN SET
			final int numLabels = ois.readInt();

			// LABELS
			Set< T > labels = new HashSet< T >();
			for ( int j = 0; j < numLabels; j++ )
				labels.add( labelSerializer.readLabel( ois ) );

			labelSets.add( labels );
		}
		new SerialisationAccess< T >( mapping )
		{
			@Override
			protected void setLabelSets( final List< Set< T > > labelSets )
			{
				super.setLabelSets( labelSets );
			}
		}.setLabelSets( labelSets );

		// NUMBER OF ENTRIES
		final int size = ois.readInt();

		// ENTRIES
		final O ref = idmap.createRef();
		for ( int i = 0; i < size; i++ )
		{
			final int key = ois.readInt();
			final int value = ois.readInt();
			pmap.put( idmap.getObject( key, ref ), value );
		}
		idmap.releaseRef( ref );
	}
}
