package org.mastodon.kdtree;

import org.mastodon.RefPool;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.attributes.RealPointAttribute;

import net.imglib2.RealLocalizable;

/**
 *
 * @author Tobias Pietzsch
 *
 * @param <O>
 *            type of objects stored in the tree.
 * @param <T>
 *            the {@link MappedElement} type of the created pool of nodes.
 */
public class KDTreeNode<
			O extends RealLocalizable,
			T extends MappedElement >
		extends PoolObject< KDTreeNode< O, T >, KDTree< O, T >, T >
		implements RealPointAttribute.DelegateRealLocalizable
{
	private final int n;

	private final RefPool< O > objPool;

	private final RealPointAttribute< KDTreeNode< O, T > >.AbstractRealPointAccess position;

	public KDTreeNode( final KDTree< O, T > kdtree )
	{
		super( kdtree );
		this.objPool = kdtree.getObjectPool();
		position = pool.position.new QuietRealPointAccess( this );
		n = position.numDimensions();
}

	@Override
	public RealLocalizable delegate()
	{
		return position;
	}

	protected int getLeftIndex()
	{
		return pool.leftIndex.get( this ) / pool.sizeInDoubles;
	}

	protected void setLeftIndex( final int index )
	{
		pool.leftIndex.setQuiet( this, index * pool.sizeInDoubles );
	}

	protected int getRightIndex()
	{
		return pool.rightIndex.get( this ) / pool.sizeInDoubles;
	}

	protected void setRightIndex( final int index )
	{
		pool.rightIndex.setQuiet( this, index * pool.sizeInDoubles );
	}

	protected int getDataIndex()
	{
		return pool.dataIndex.get( this );
	}

	protected void setDataIndex( final int index )
	{
		pool.dataIndex.setQuiet( this, index );
	}

	protected int getFlags()
	{
		return pool.flags.get( this );
	}

	protected void setFlags( final int flags )
	{
		pool.flags.setQuiet( this, flags );
	}

	protected double getPosition( final int d )
	{
		return pool.position.getDoublePosition( this, d );
	}

	@Override
	protected void setToUninitializedState()
	{}

	protected void init( final O o )
	{
		setDataIndex( objPool.getId( o ) );
		setFlags( 0 );
		for ( int d = 0; d < o.numDimensions(); ++d )
			pool.position.setPositionQuiet( this, o.getDoublePosition( d ), d );;
	}

	/**
	 * Compute the squared distance from p to this node.
	 *
	 * @param p
	 *            coordinates to compute squared distance to. Must have at least
	 *            <code>numDimensions</code> elements. Only the first
	 *            <code>numDimensions</code> elements are used.
	 * @return the square distance.
	 */
	public final float squDistanceTo( final float[] p )
	{
		float sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double diff = getPosition( d ) - p[ d ];
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 *
	 * @param p
	 *            coordinates to compute squared distance to. Must have at least
	 *            <code>numDimensions</code> elements. Only the first
	 *            <code>numDimensions</code> elements are used.
	 * @return the square distance.
	 */
	public final double squDistanceTo( final double[] p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double diff = getPosition( d ) - p[ d ];
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 *
	 * @param p
	 *            coordinates to compute squared distance to. Must be at least
	 *            <code>numDimensions</code>-dimensional.
	 * @return the square distance.
	 */
	public final double squDistanceTo( final RealLocalizable p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double diff = getPosition( d ) - p.getDoublePosition( d );
			sum += diff * diff;
		}
		return sum;
	}

	public boolean isValid()
	{
		return getFlags() == 0;
	}

	public void setValid( final boolean valid )
	{
		setFlags( valid ? 0 : 1 );
	}
}
