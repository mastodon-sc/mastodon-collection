package org.mastodon.rtree;

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;
import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.Ref;
import org.mastodon.RefPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.PoolObject;

import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;

public class RTreeNode< O extends Geometry & Ref< O > >
extends PoolObject< RTreeNode< O >, RTree< O >, ByteMappedElement > implements Geometry
{

	private final RefPool< O > objPool;

	private final int n;

	private final int ISLEAF_OFFSET;
	private final int ENTRIES_OFFSET;
	private final int N_ENTRIES_OFFSET;
	private final int MBR_OFFSET;

	private final int sizeInDoubles;

	private final int maxNEntries;
	private final int minNEntries;

	protected RTreeNode( final RTree< O > rtree, final int numDimensions )
	{
		super( rtree );
		this.objPool = rtree.getObjectPool();
		this.n = numDimensions;
		this.maxNEntries = rtree.layout.maxNEntries;
		this.minNEntries = rtree.layout.maxNEntries;
		ISLEAF_OFFSET = rtree.layout.isLeaf.getOffset();
		ENTRIES_OFFSET = rtree.layout.entries.getOffset();
		N_ENTRIES_OFFSET = rtree.layout.nEntries.getOffset();
		MBR_OFFSET = rtree.layout.mbr.getOffset();
		sizeInDoubles = ( rtree.layout.getSizeInBytes() + DOUBLE_SIZE - 1 ) / DOUBLE_SIZE;
	}

	@Override
	protected void setToUninitializedState()
	{}

	/**
	 * Initializes this node to be a leaf with the specified object as single
	 * entry.
	 *
	 * @param o
	 *            the object to attach to this leaf node.
	 * @return this node.
	 */
	public < K extends Ref< K > & RealInterval > RTreeNode< O > init( final K o )
	{
		reset();
		add( o );
		return this;
	}

	/**
	 * Empties the node.
	 */
	void reset()
	{
		setNEntrie( 0 );
		setIsLeaf( true );
		for ( int i = 0; i < maxNEntries; i++ )
			access.putIndex( -1, ENTRIES_OFFSET + i * INDEX_SIZE );
		for ( int d = 0; d < n; d++ )
		{
			setMax( Double.NEGATIVE_INFINITY, d );
			setMin( Double.POSITIVE_INFINITY, d );
		}
	}

	protected void setNEntrie( final int nEntries )
	{
		access.putInt( nEntries, N_ENTRIES_OFFSET );
	}

	public int getNEntries()
	{
		return access.getInt( N_ENTRIES_OFFSET );
	}

//	protected void setMBR( final double[] minmax )
//	{
//		for ( int i = 0; i < minmax.length; i++ )
//			access.putDouble( minmax[ i ], MBR_OFFSET + i * DOUBLE_SIZE );
//	}

//	public void getMBR( final double[] minmax )
//	{
//		for ( int i = 0; i < 2 * n; i++ )
//			minmax[ i ] = access.getDouble( MBR_OFFSET + i * DOUBLE_SIZE );
//	}

	protected void setIsLeaf( final boolean isLeaf )
	{
		access.putBoolean( isLeaf, ISLEAF_OFFSET );
	}

	public boolean isLeaf()
	{
		return access.getBoolean( ISLEAF_OFFSET );
	}

	/**
	 * Returns the id of the entry with index i.
	 * <p>
	 * If this node is a lead, the returned id is the internal pool index of a
	 * geometry object in the object pool. If not, then it is the internal pool
	 * index of a child node in the tree node pool.
	 * <p>
	 * The results are unspecified if i is lower than 0 or higher than the
	 * number of entries in this node.
	 *
	 * @param i
	 *            the index of the entry in the entries array of this node.
	 */
	public int getEntry( final int i )
	{
		return access.getIndex( ENTRIES_OFFSET + i * INDEX_SIZE );
	}

	/**
	 * Stores the specified object id as ith entry of this node.
	 *
	 * @param id
	 *            the id to store.
	 * @param i
	 *            the entry index in this node.
	 */
	protected void setEntry( final int id, final int i )
	{
		access.putIndex( id, ENTRIES_OFFSET + i * INDEX_SIZE );
	}

	public void addEntry( final O o )
	{
		assert isLeaf(): "Can only store entries in leaf nodes.";
		add( o );
	}

	public void addChild( final RTreeNode< O > child )
	{
		assert !isLeaf(): "Can only store child nodes in non-leaf nodes.";
		add( child );
	}

	< K extends Ref< K > & RealInterval > void add( final K o )
	{
		// TODO public method with brood generic types dangerous?

		// Store the id.
		final int i = getNEntries();
		setEntry( o.getInternalPoolIndex(), i );
		setNEntrie( i + 1 );

		// Recompute MBR.
		for ( int d = 0; d < n; d++ )
		{
			if (o.realMin( d ) < realMin( d ))
				setMin( o.realMin( d ), d );

			if ( o.realMax( d ) > realMax( d ) )
				setMax( o.realMax( d ), d );
		}
	}

	public void recalculateMBR()
	{
		if ( isLeaf() )
			recalculateMBR( objPool );
		else
			recalculateMBR( pool );
	}

	private < K extends Ref< K > & RealInterval > void recalculateMBR( final RefPool< K > kpool )
	{
		final K ref = kpool.createRef();
		for ( int i = 0; i < getNEntries(); i++ )
		{
			final int id = getEntry( i );
			final K child = kpool.getObject( id, ref );
			for ( int d = 0; d < n; d++ )
			{
				if ( child.realMin( d ) < realMin( d ) )
					setMin( child.realMin( d ), d );

				if ( child.realMax( d ) > realMax( d ) )
					setMax( child.realMax( d ), d );
			}
		}
		kpool.releaseRef( ref );
	}

	protected void setMin( final double val, final int d )
	{
		access.putDouble( val, MBR_OFFSET + d * DOUBLE_SIZE );
	}

	protected void setMax( final double val, final int d )
	{
		access.putDouble( val, MBR_OFFSET + ( n + d ) * DOUBLE_SIZE );
	}

	@Override
	public double realMin( final int d )
	{
		return access.getDouble( MBR_OFFSET + d * DOUBLE_SIZE );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < min.length; d++ )
			min[ d ] = access.getDouble( MBR_OFFSET + d * DOUBLE_SIZE );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
			min.setPosition( access.getDouble( MBR_OFFSET + d * DOUBLE_SIZE ), d );
	}

	@Override
	public double realMax( final int d )
	{
		return access.getDouble( MBR_OFFSET + ( n + d ) * DOUBLE_SIZE );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < max.length; d++ )
			max[ d ] = access.getDouble( MBR_OFFSET + ( n + d ) * DOUBLE_SIZE );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
			max.setPosition( access.getDouble( MBR_OFFSET + ( n + d ) * DOUBLE_SIZE ), d );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder();
		str.append( super.toString() + " id = " + getInternalPoolIndex() + "\n" );
		str.append( "\tMBR: " + GeometryUtil.printInterval( this ) );
		str.append( "\t- is leaf: " + isLeaf() + "\n" );
		str.append( getNEntries() == 0 ? "\tEmpty.\n" : ( getNEntries() == 1 ? "\t1 entry.\n" : "\t" + getNEntries() + " entries:\n" ) );
		if (getNEntries() > 0)
		{
			if (isLeaf())
			{
				final O ref = objPool.createRef();
				for ( int i = 0; i < getNEntries(); i++ )
					str.append( "\t\t" + i + ": " + objPool.getObject( getEntry( i ), ref ) + "\n" );
				objPool.releaseRef( ref );
			}
			else
			{
				for ( int i = 0; i < getNEntries(); i++ )
					str.append( "\t\t" + i + ": Node id = " + getEntry( i ) + "\n" );
			}
		}
		return str.toString();
	}
}
