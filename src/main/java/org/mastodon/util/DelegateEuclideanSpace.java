package org.mastodon.util;

import net.imglib2.EuclideanSpace;

// TODO: this should probably be in imglib?
public interface DelegateEuclideanSpace extends EuclideanSpace
{
	EuclideanSpace delegate();

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}
}
