/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.pool;

import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This test verifies if the garbage collector, can clean the content of the
 * {@link ThreadLocalSoftReferencePool}, if the pool holds too much memory.
 */
public class ThreadLocalSoftReferenceMemoryTest
{
	//@Test
	public void testConcurrentLinkedQueue() {
		// This test is expected to fail, because the ConcurrentLinkedQueue
		// does not allow the queued objects to be garbage collected, if needed.
		// NB: This code puts ~8GB of memory into the queue
		ConcurrentLinkedQueue<int[]> pool = new ConcurrentLinkedQueue<>();
		for ( int i = 0; i < 1_000_000; i++ )
		{
			pool.add( new int[ 2000 ] );
		}
	}

	@Test
	public void testFastConcurrentPool() {
		// NB: This code puts ~8GB of memory into the pool
		ThreadLocalSoftReferencePool<int[]> pool = new ThreadLocalSoftReferencePool<>();
		for ( int i = 0; i < 1_000_000; i++ )
		{
			pool.put( new int[ 2000 ] );
		}
	}
}
