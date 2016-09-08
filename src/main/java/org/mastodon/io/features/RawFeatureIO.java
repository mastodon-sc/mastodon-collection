package org.mastodon.io.features;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.mastodon.features.Feature;
import org.mastodon.features.FeatureRegistry;
import org.mastodon.features.Features;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;

public class RawFeatureIO
{
	/**
	 * De/serialize a feature map of type {@code M}.
	 *
	 * TODO Create its counterpart for edge features.
	 *
	 * @param <M>
	 *            the feature map type
	 * @param <O>
	 *            the vertex type
	 *
	 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
	 */
	public static interface Serializer< M, O >
	{
		public void writeFeatureMap(
				final ObjectToFileIdMap< O > idmap,
				final M featureMap,
				final ObjectOutputStream oos )
						throws IOException;

		public void readFeatureMap(
				final FileIdToObjectMap< O > idmap,
				final M featureMap,
				final ObjectInputStream ois )
						throws IOException, ClassNotFoundException;
	}

	/**
	 * Writes the specified list of features to an object output stream.
	 * 
	 * @param idmap
	 *            the object-to-file id map.
	 * @param features
	 *            the collection of features.
	 * @param featuresToSerialize
	 *            the list of features to serialize.
	 * @param oos
	 *            the output stream for serializing.
	 * @param <O>
	 *            type of object to which features are attached.
	 * @throws IOException
	 *             if a serializer cannot be found for the specified feature, or
	 *             for usual I/O errors.
	 */
	public static < O > void writeFeatureMaps(
			final ObjectToFileIdMap< O > idmap,
			final Features< O > features,
			final List< Feature< ?, O, ? > > featuresToSerialize,
			final ObjectOutputStream oos )
					throws IOException
	{
		final String[] keys = new String[ featuresToSerialize.size() ];
		int i = 0;
		for ( final Feature< ?, O, ? > feature : featuresToSerialize )
			keys[ i++ ] = feature.getKey();
		oos.writeObject( keys );

		for ( final Feature< ?, O, ? > feature : featuresToSerialize )
			serializeFeatureMap( idmap, feature, features.getFeatureMap( feature ), oos );
	}

	/**
	 * Serializes the specified feature to an object output stream.
	 * 
	 * @param idmap
	 *            the object-to-file id map.
	 * @param feature
	 *            the feature to serialize.
	 * @param featureMap
	 *            the feature map.
	 * @param oos
	 *            the output stream for serializing.
	 * @param <M>
	 *            the type of the feature.
	 * @param <V>
	 *            the type of object to which the feature is attached.
	 * @throws IOException
	 *             if a serializer cannot be found for the specified feature, or
	 *             for usual I/O errors.
	 */
	@SuppressWarnings( "unchecked" )
	private static < M, V > void serializeFeatureMap(
			final ObjectToFileIdMap< V > idmap,
			final Feature< M, V, ? > feature,
			final Object featureMap,
			final ObjectOutputStream oos )
					throws IOException
	{
		final Serializer< M, V > serializer = FeatureSerializers.get( feature );
		if ( serializer == null )
			throw new IOException( "No Serializer registered for " + feature );
		serializer.writeFeatureMap( idmap, ( M ) featureMap, oos );
	}

	/**
	 * Read a collection of features with data read from an object input stream.
	 * 
	 * @param idmap
	 *            the file id-to-object map.
	 * @param features
	 *            the feature collection to read. Will be filled with the
	 *            feature maps read from the input stream.
	 * @param ois
	 *            the object input stream to read from.
	 * @param <O>
	 *            the type of object to which features are attached.
	 * @throws IOException
	 *             if a serializer cannot be found for the specified feature, or
	 *             the class of serialized object cannot be found, or for usual
	 *             I/O errors.
	 */
	public static < O > void readFeatureMaps(
			final FileIdToObjectMap< O > idmap,
			final Features< O > features,
			final ObjectInputStream ois )
					throws IOException
	{
		try
		{
			final String[] keys = ( String[] ) ois.readObject();
			for ( final String key : keys )
			{
				@SuppressWarnings( "unchecked" )
				final Feature< ?, O, ? > feature = ( Feature< ?, O, ? > ) FeatureRegistry.getFeature( key );
				deserializeFeatureMap( idmap, feature, features.getFeatureMap( feature ), ois );
			}
		}
		catch ( final ClassNotFoundException e )
		{
			throw new IOException( e );
		}
	}

	/**
	 * De-serializes a feature map from an object input stream.
	 * 
	 * @param idmap
	 *            the file id-to-object map.
	 * @param feature
	 *            the feature to read.
	 * @param featureMap
	 *            the feature map to fill.
	 * @param ois
	 *            the object input stream to read from.
	 * @throws IOException
	 *             if a serializer cannot be found for the specified feature, or
	 *             for usual I/O errors.
	 * @throws ClassNotFoundException
	 *             if the class of the serialized object cannot be found.
	 */
	@SuppressWarnings( "unchecked" )
	private static < M, V > void deserializeFeatureMap(
			final FileIdToObjectMap< V > idmap,
			final Feature< M, V, ? > feature,
			final Object featureMap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException
	{
		final Serializer< M, V > serializer = FeatureSerializers.get( feature );
		if ( serializer == null )
			throw new IOException( "No Serializer registered for " + feature );
		serializer.readFeatureMap( idmap, ( M ) featureMap, ois );
	}
}
