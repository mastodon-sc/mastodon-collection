/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

/**
 * Helper methods to encode and decode different data types ({@code long, double}
 * etc.) from bytes at an offset in a {@code double[]} array.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
@SuppressWarnings( "restriction" )
public class DoubleUtils
{
	public static void putByte( final byte value, final double[] array, final int offset )
	{
		UNSAFE.putByte( array, DOUBLE_ARRAY_OFFSET + offset, value );
	}

	public static byte getByte( final double[] array, final int offset )
	{
		return UNSAFE.getByte( array, DOUBLE_ARRAY_OFFSET + offset );
	}

	public static void copyBytes( final byte[] src, final int srcPos, final double[] dest, final int destPos, final int numBytes )
	{
		UNSAFE.copyMemory( src, BYTE_ARRAY_OFFSET + srcPos, dest, DOUBLE_ARRAY_OFFSET + destPos, numBytes );
	}

	public static void copyBytes( final double[] src, final int srcPos, final byte[] dest, final int destPos, final int numBytes )
	{
		UNSAFE.copyMemory( src, DOUBLE_ARRAY_OFFSET + srcPos, dest, BYTE_ARRAY_OFFSET + destPos, numBytes );
	}

	public static void putBoolean( final boolean value, final double[] array, final int offset )
	{
		putByte( value ? ( byte ) 1 : ( byte ) 0, array, offset );
	}

	public static boolean getBoolean( final double[] array, final int offset )
	{
		return getByte( array, offset ) == ( byte ) 0 ? false : true;
	}

	public static void putShort( final short value, final double[] array, final int offset )
	{
		UNSAFE.putShort( array, DOUBLE_ARRAY_OFFSET + offset, value );
	}

	public static short getShort( final double[] array, final int offset )
	{
		return UNSAFE.getShort( array, DOUBLE_ARRAY_OFFSET + offset );
	}

	public static void putInt( final int value, final double[] array, final int offset )
	{
		UNSAFE.putInt( array, DOUBLE_ARRAY_OFFSET + offset, value );
	}

	public static int getInt( final double[] array, final int offset )
	{
		return UNSAFE.getInt( array, DOUBLE_ARRAY_OFFSET + offset );
	}

	public static void putLong( final long value, final double[] array, final int offset )
	{
		UNSAFE.putLong( array, DOUBLE_ARRAY_OFFSET + offset, value );
	}

	public static long getLong( final double[] array, final int offset )
	{
		return UNSAFE.getLong( array, DOUBLE_ARRAY_OFFSET + offset );
	}

	public static void putFloat( final float value, final double[] array, final int offset )
	{
		UNSAFE.putFloat( array, DOUBLE_ARRAY_OFFSET + offset, value );
	}

	public static float getFloat( final double[] array, final int offset )
	{
		return UNSAFE.getFloat( array, DOUBLE_ARRAY_OFFSET + offset );
	}

	public static void putDouble( final double value, final double[] array, final int offset )
	{
		UNSAFE.putDouble( array, DOUBLE_ARRAY_OFFSET + offset, value );
	}

	public static double getDouble( final double[] array, final int offset )
	{
		return UNSAFE.getDouble( array, DOUBLE_ARRAY_OFFSET + offset );
	}

	public static void putIndex( final int value, final double[] array, final int offset )
	{
		putInt( value, array, offset );
	}

	public static int getIndex( final double[] array, final int offset )
	{
		return getInt( array, offset );
	}

	// Note: offsets in bytes!
	public static void copyBytes( final double[] srcArray, final long srcOffset, final double[] dstArray, final long dstOffset, final int size )
	{
		UNSAFE.copyMemory( srcArray, DOUBLE_ARRAY_OFFSET + srcOffset, dstArray, DOUBLE_ARRAY_OFFSET + dstOffset, size );
	}

	private static final Unsafe UNSAFE;

	static
	{
		try
		{
			final PrivilegedExceptionAction< Unsafe > action = new PrivilegedExceptionAction< Unsafe >()
			{
				@Override
				public Unsafe run() throws Exception
				{
					final Field field = Unsafe.class.getDeclaredField( "theUnsafe" );
					field.setAccessible( true );
					return ( Unsafe ) field.get( null );
				}
			};

			UNSAFE = AccessController.doPrivileged( action );
		}
		catch ( final Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}

	private static final long DOUBLE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset( double[].class );
	private static final long BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset( byte[].class );
}
