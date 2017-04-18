package org.mastodon.pool.attributes;

public interface DoubleArrayAttributeReadOnlyValue
{
	public int length();

	public double get( final int index );
}
