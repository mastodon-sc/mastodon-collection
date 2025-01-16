/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.io;

/**
 * Provides serialization of (parts of) an object of type {@code O} to a byte
 * array.
 * <p>
 * This is used for undo/redo and for raw i/o.
 * </p>
 *
 * @param <O>
 *            type of object.
 *
 * @author Tobias Pietzsch
 */
public interface AttributeSerializer< O >
{
	/**
	 * How many bytes are needed for storage. (This is the expected size of the
	 * {@code bytes} array passed to {@link #getBytes(Object, byte[])},
	 * {@link #setBytes(Object, byte[])}.
	 *
	 * @return number of bytes that are needed for storage.
	 */
	public int getNumBytes();

	/**
	 * Stores data from {@code obj} into {@code bytes}.
	 * <p>
	 * The required array size can be obtained by {@link #getNumBytes()}.
	 *
	 * @param obj
	 *            the object to store.
	 * @param bytes
	 *            the byte array in which to write.
	 */
	public void getBytes( final O obj, final byte[] bytes );

	/**
	 * Restores data from {@code bytes} into {@code obj}.
	 *
	 * @param obj
	 *            the object to restore.
	 * @param bytes
	 *            the byte array to read.
	 */
	public void setBytes( final O obj, final byte[] bytes );

	/**
	 * Notifies that bytes have been written ({@link #setBytes(Object, byte[])})
	 * to {@code obj}.
	 * <p>
	 * Note: Currently nothing is ever done in between {@code setBytes()} and
	 * {@code notifySet()}, so maybe this will be removed later and
	 * notifications directly linked to {@code setBytes()}. For now, we keep it
	 * explicit.
	 *
	 * @param obj
	 *            the object that has been modified.
	 */
	public void notifySet( final O obj );
}
