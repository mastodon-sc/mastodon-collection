package org.mastodon.pool;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import org.mastodon.Options;

import sun.misc.Unsafe;

/**
 * Helper methods to encode and decode different data types ({@code long, double}
 * etc.) from bytes at an offset in a {@code byte[]} array.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
@SuppressWarnings( { "restriction", "unused" } )
public class ByteUtils
{
	public static final int BYTE_SIZE = 1;

	public static final int BOOLEAN_SIZE = 1;

	public static final int INT_SIZE = 4;

	public static final int SHORT_SIZE = 2;

	public static final int LONG_SIZE = 8;

	public static final int FLOAT_SIZE = 4;

	public static final int DOUBLE_SIZE = 8;

	public static final int INDEX_SIZE = INT_SIZE;

	public static void putByte( final byte value, final byte[] array, final int offset )
	{
		array[ offset ] = value;
	}

	public static byte getByte( final byte[] array, final int offset )
	{
		return array[ offset ];
	}

	public static void copyBytes( final byte[] src, final int srcPos, final byte[] dest, final int destPos, final int length )
	{
		System.arraycopy( src, srcPos, dest, destPos, length );
	}

	public static void putBoolean( final boolean value, final byte[] array, final int offset )
	{
		array[ offset ] = value ? ( byte ) 1 : ( byte ) 0;
	}

	public static boolean getBoolean( final byte[] array, final int offset )
	{
		return array[ offset ] == ( byte ) 0 ? false : true;
	}

	public static void putInt( final int value, final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + INT_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		UNSAFE.putInt( array, BYTE_ARRAY_OFFSET + offset, value );
	}

	public static int getInt( final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + INT_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		return UNSAFE.getInt( array, BYTE_ARRAY_OFFSET + offset );
	}

	public static void putShort( final short value, final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + SHORT_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		UNSAFE.putShort( array, BYTE_ARRAY_OFFSET + offset, value );
	}

	public static short getShort( final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + SHORT_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		return UNSAFE.getShort( array, BYTE_ARRAY_OFFSET + offset );
	}

	public static void putLong( final long value, final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + LONG_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		UNSAFE.putLong( array, BYTE_ARRAY_OFFSET + offset, value );
	}

	public static long getLong( final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + LONG_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		return UNSAFE.getLong( array, BYTE_ARRAY_OFFSET + offset );
	}

	public static void putFloat( final float value, final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + FLOAT_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		UNSAFE.putFloat( array, BYTE_ARRAY_OFFSET + offset, value );
	}

	public static float getFloat( final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + FLOAT_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		return UNSAFE.getFloat( array, BYTE_ARRAY_OFFSET + offset );
	}

	public static void putDouble( final double value, final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + DOUBLE_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		UNSAFE.putDouble( array, BYTE_ARRAY_OFFSET + offset, value );
	}

	public static double getDouble( final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + DOUBLE_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		return UNSAFE.getDouble( array, BYTE_ARRAY_OFFSET + offset );
	}

	public static void putIndex( final int value, final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + INDEX_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		putInt( value, array, offset );
	}

	public static int getIndex( final byte[] array, final int offset )
	{
		if ( Options.DEBUG && ( offset < 0 || array.length < offset + INDEX_SIZE ) )
			throw new ArrayIndexOutOfBoundsException( offset );
		return getInt( array, offset );
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

	private static final long BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset( byte[].class );
}
