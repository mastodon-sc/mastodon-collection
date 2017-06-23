package org.mastodon.pool.attributes;

public interface FloatArrayAttributeReadOnlyValue
{
	public int length();

	public float get( final int index );
}
