package org.mastodon.collection.util;

import net.imglib2.EuclideanSpace;

public interface DelegateEuclideanSpace extends EuclideanSpace
{
	EuclideanSpace delegate();

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}
}
