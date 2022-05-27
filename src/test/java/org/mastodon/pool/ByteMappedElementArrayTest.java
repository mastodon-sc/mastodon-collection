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

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * Tests {@link ByteMappedElementArray}.
 *
 * @author Matthias Arzt
 */
public class ByteMappedElementArrayTest {

	MappedElementArray.Factory<ByteMappedElementArray> factory =
		ByteMappedElementArray.factory;

	/** Test {@link ByteMappedElementArray#size()}. */
	@Test
	public void testSize() {
		ByteMappedElementArray array = factory.createArray(2, 8);
		assertEquals(2, array.size());
	}

	/**
	 * Test {@link ByteMappedElementArray#createAccess()},
	 * and the returned {@link ByteMappedElement}.
	 */
	@Test
	public void testCreateAccess() {
		ByteMappedElementArray array = factory.createArray(2, 6);
		ByteMappedElement access = array.createAccess();
		access.setElementIndex(0);
		access.putFloat(42, 0);
		access.putShort((short) 43, 4);
		access.setElementIndex(1);
		access.putFloat(44, 0);
		access.putShort((short) 45, 4);
		access.setElementIndex(0);
		assertEquals(42, access.getFloat(0), 0f);
		assertEquals(43, access.getShort(4));
		access.setElementIndex(1);
		assertEquals(44, access.getFloat(0), 0f);
		assertEquals(45, access.getShort(4));
	}

	/**
	 * Test, if an array as large as {@link ByteMappedElementArray#maxSize()}
	 * canin deed be created.
	 */
	@Test
	public void testMaxSize() {
		int maxSize = factory.createArray(0, 1).maxSize();

		assumeTrue("Skip test of ByteMappedElementArray.maxSize(), due too limited memory", Runtime.getRuntime().freeMemory() > (long) maxSize + 100_000);

		ByteMappedElementArray array = factory.createArray(maxSize, 1);
		ByteMappedElement access = array.createAccess();
		access.setElementIndex(maxSize - 1);
		access.putByte((byte) 42, 0);
		assertEquals(42, access.getByte(0));
	}

	/** Test {@link org.mastodon.pool.ByteMappedElementArray}. */
	@Test
	public void testResize() {
		ByteMappedElementArray array = factory.createArray(2, 8);
		ByteMappedElement element = array.createAccess();
		element.setElementIndex(1);
		element.putDouble(2.0, 0);

		// resize
		array.resize(3);

		// make sure size and memory content is correct
		assertEquals(3, array.size());
		assertEquals(2.0, element.getDouble(0), 0.0);
	}
}
