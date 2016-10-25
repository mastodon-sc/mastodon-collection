package org.mastodon.pool;

/**
 * A {@link MappedElementArray} that stores {@link DoubleMappedElement
 * DoubleMappedElements} in a {@code double[]} array.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class DoubleMappedElementArray implements MappedElementArray< DoubleMappedElementArray, DoubleMappedElement >
{
	/**
	 * The current data storage. This is changed when the array is
	 * {@link #resize(int) resized}.
	 */
	double[] data;

	final private double[] swapTmp;

	/**
	 * How many bytes on element in this array accupies.
	 */
	final int bytesPerElement;

	/**
	 * How many elements are stored in this array.
	 */
	private long size;

	private long doubleSizeFromByteSize( final long byteSize )
	{
		return ( byteSize + ByteUtils.DOUBLE_SIZE - 1 ) / ByteUtils.DOUBLE_SIZE;
	}

	/**
	 * Create a new array containing {@code numElements} elements of
	 * {@code bytesPerElement} bytes each.
	 */
	private DoubleMappedElementArray( final long numElements, final int bytesPerElement )
	{
		final long numDoubles = doubleSizeFromByteSize( numElements * bytesPerElement );
		if ( numDoubles > Integer.MAX_VALUE )
			throw new IllegalArgumentException(
					"trying to create a " + getClass().getName() + " with more than " + maxSize() + " elements of " + bytesPerElement + " bytes.");

		this.data = new double[ ( int ) numDoubles ];
		this.swapTmp = new double[ ( int ) doubleSizeFromByteSize( bytesPerElement ) ];
		this.bytesPerElement = bytesPerElement;
		this.size = numElements;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public long maxSize()
	{
		return 8l * Integer.MAX_VALUE / bytesPerElement;
	}

	@Override
	public DoubleMappedElement createAccess()
	{
		return new DoubleMappedElement( this, 0 );
	}

	@Override
	public void updateAccess( final DoubleMappedElement access, final long index )
	{
		access.setDataArray( this );
		access.setElementIndex( index );
	}

	/**
	 * {@inheritDoc} Moves the data using
	 * {@link System#arraycopy(Object, int, Object, int, int)}, using
	 * <code>swapTmp</code> as a temporary.
	 */
	@Override
	public void swapElement( final long index, final DoubleMappedElementArray array, final long arrayIndex )
	{
		final long baseOffset = index * bytesPerElement;
		final long arrayBaseOffset = arrayIndex * bytesPerElement;
		DoubleUtils.copyBytes( data, baseOffset, swapTmp, 0, bytesPerElement );
		DoubleUtils.copyBytes( array.data, arrayBaseOffset, data, baseOffset, bytesPerElement );
		DoubleUtils.copyBytes( swapTmp, 0, array.data, arrayBaseOffset, bytesPerElement );
	}

	/**
	 * {@inheritDoc} The storage array is reallocated and the old contents
	 * copied over.
	 */
	@Override
	public void resize( final long numElements )
	{
		final long numDoubles = doubleSizeFromByteSize( numElements * bytesPerElement );
		if ( numDoubles > Integer.MAX_VALUE )
			throw new IllegalArgumentException(
					"trying to resize a " + getClass().getName() + " to more than " + maxSize() + " elements of " + bytesPerElement + " bytes.");

		final double[] datacopy = new double[ ( int ) numDoubles ];
			final int copyLength = Math.min( data.length, datacopy.length );
			System.arraycopy( data, 0, datacopy, 0, copyLength );
		this.data = datacopy;
		this.size = numElements;
	}

	/**
	 * <b>For internal use only!</b>
	 *
	 * @return the data array used in this class.
	 */
	public double[] getCurrentDataArray()
	{
		return data;
	}

	/**
	 * A factory for {@link DoubleMappedElementArray}s.
	 */
	public static final MappedElementArray.Factory< DoubleMappedElementArray > factory = new MappedElementArray.Factory< DoubleMappedElementArray >()
	{
		@Override
		public DoubleMappedElementArray createArray( final long numElements, final int bytesPerElement )
		{
			return new DoubleMappedElementArray( numElements, bytesPerElement );
		}
	};
}
