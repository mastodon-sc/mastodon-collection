package org.mastodon.io.properties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;
import org.mastodon.properties.PropertyMap;

/**
 * De/serialize a {@link PropertyMap} of type {@code M}.
 *
 * @param <M>
 *            the property map type
 * @param <O>
 *            type of object which the property is attached to.
 *
 * @author Tobias Pietzsch
 */
public interface PropertyMapSerializer< O, M extends PropertyMap< O, ? > >
{
	public void writePropertyMap(
			final ObjectToFileIdMap< O > idmap,
			final ObjectOutputStream oos )
					throws IOException;

	public void readPropertyMap(
			final FileIdToObjectMap< O > idmap,
			final ObjectInputStream ois )
					throws IOException, ClassNotFoundException;

	public M getPropertyMap();
}
