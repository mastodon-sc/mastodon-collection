/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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

import static org.junit.Assume.assumeTrue;

/**
 * Tests {@link MultiArrayMemPool}.
 *
 * @author Matthias Arzt
 */
public class MultiArrayMemPoolTest {

	@Test
	public void testAppend() {
		assumeTrue("This test requires roughly 5 GB of memory.", Runtime.getRuntime().freeMemory() > 5_000_000_000L);
		MemPool.Factory<ByteMappedElement> factory =
			MultiArrayMemPool.factory(ByteMappedElementArray.factory);
		int bytesPerElement = 1024 * 1024; // 1 MB
		MemPool<ByteMappedElement> memPool = factory.createPool(2, bytesPerElement, MemPool.FreeElementPolicy.CHECK_FREE_ELEMENT_LIST);
		// Add 4 GB of elements
		for (int i = 0; i < 4000; i++)
			memPool.append();
	}
}
