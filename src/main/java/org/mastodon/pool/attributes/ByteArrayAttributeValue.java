package org.mastodon.pool.attributes;

public interface ByteArrayAttributeValue extends ByteArrayAttributeReadOnlyValue
{
	public void set( final int index, final byte value );
}
