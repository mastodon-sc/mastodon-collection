package org.mastodon.pool.attributes;

public interface DoubleArrayAttributeValue extends DoubleArrayAttributeReadOnlyValue
{
	public void set( final int index, final double value );
}
