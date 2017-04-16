package org.mastodon.undo.attributes;

import org.mastodon.properties.AbstractProperty;

public class Attribute< O > extends AbstractProperty< O >
{
	private final AttributeSerializer< O > serializer;

	private final String name;

	public Attribute( final AttributeSerializer< O > serializer )
	{
		this( serializer, "unnamed" );
	}

	public Attribute( final AttributeSerializer< O > serializer, final String name )
	{
		this.serializer = serializer;
		this.name = name;
	}

	public AttributeSerializer< O > getUndoSerializer()
	{
		return serializer;
	}

	@Override
	public String toString()
	{
		return "Attribute(\"" + name + "\")";
	}

	/**
	 * Notify listeners that the value of this attribute for {@code object} is
	 * about to change.
	 *
	 * @param object
	 *            the object for which the attribute value is about to change.
	 */
	public void notifyBeforeAttributeChange( final O object )
	{
		super.notifyBeforePropertyChange( object );
	}

	/**
	 * Notify listeners that the value of this attribute for {@code object} has
	 * changed.
	 *
	 * @param object
	 *            the object for which the attribute value has changed.
	 */
	public void notifyAttributeChanged( final O object )
	{
		super.notifyPropertyChanged( object );
	}
}
