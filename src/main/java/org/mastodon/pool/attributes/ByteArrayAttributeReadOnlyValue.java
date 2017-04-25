package org.mastodon.pool.attributes;

public interface ByteArrayAttributeReadOnlyValue
{
	public int length();

	public byte get( final int index );
}
