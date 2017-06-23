package org.mastodon.bufferpooldemo;

import java.nio.FloatBuffer;

import org.mastodon.collection.RefList;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.pool.BufferMappedElement;
import org.mastodon.pool.BufferMappedElementArray;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObjectLayout;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.ByteArrayAttribute;
import org.mastodon.pool.attributes.ByteArrayAttributeValue;
import org.mastodon.pool.attributes.FloatArrayAttribute;
import org.mastodon.pool.attributes.FloatArrayAttributeValue;
import org.mastodon.pool.attributes.IndexAttribute;

import net.imglib2.type.numeric.ARGBType;

public class SceneryStuff
{
	public static void main( final String[] args )
	{
		final Vertex3Pool vp = new Vertex3Pool( 10 );
		final TrianglePool tp = new TrianglePool( vp, 10 );


//		final RefList< Vertex3 > myVertices = RefCollections.createRefList( vp.asRefCollection() );
//		final List< Vertex3 > myVertices = RefCollections.createRefList( vp.asRefCollection() );
		final RefList< Vertex3 > myVertices = new RefArrayList<>( vp );

		for ( int i = 0; i < 10; ++i )
		{
			final Vertex3 v = vp.create().init( i, 0, 0, 1, 1, 1, 0xc0ff8844 );
			myVertices.add( v );
		}

		myVertices.forEach( v -> System.out.println( v ) );
		System.out.println();

		myVertices.remove( 3 );

		myVertices.forEach( v -> System.out.println( v ) );
		System.out.println();

		final RefList< Triangle > myTriangles = new RefArrayList<>( tp );
		for ( int i = 0; i < 3; ++i )
		{
			final Vertex3 v1 = myVertices.get( 3 * i );
			final Vertex3 v2 = myVertices.get( 3 * i + 1 );
			final Vertex3 v3 = myVertices.get( 3 * i + 2 );
			final Triangle t = tp.create().init( v1, v2, v3 );
			myTriangles.add( t );
		}

		myTriangles.forEach( t -> System.out.println( t ) );
		System.out.println();

		myVertices.add( myTriangles.get( 1 ).getVertex( 0 ) );

		myVertices.forEach( v -> System.out.println( v ) );
		System.out.println();

		myVertices.get( 3 ).setX( 1000 );

		myVertices.forEach( v -> System.out.println( v ) );
		System.out.println();


		// ========== underlying direct buffer ==============

		// FloatBuffer currently (!) underlying the pool
		final float[] dataForPrinting = new float[ 30 ];
		FloatBuffer floatBuffer = vp.getFloatBuffer();
		floatBuffer.rewind();
		floatBuffer.get( dataForPrinting );
		System.out.println( "vertex data" );
		for ( int i = 0; i < dataForPrinting.length; ++i )
		{
			System.out.print( String.format( "%f ", dataForPrinting[ i ] ) );
			if ( i % 7 == 6 )
				System.out.println();
		}
		System.out.println();
		floatBuffer = tp.getFloatBuffer();
		floatBuffer.rewind();
		floatBuffer.get( dataForPrinting );
		System.out.println( "triangle data" );
		for ( int i = 0; i < dataForPrinting.length; ++i )
		{
			System.out.print( String.format( "%e ", dataForPrinting[ i ] ) );
			if ( i % 3 == 2 )
				System.out.println();
		}
		System.out.println();

		// ========== proxies ==============

		final Vertex3 vref = vp.createRef();
		for ( int i = 0; i < 10000000; ++i )
		{
			final Vertex3 v = vp.create( vref ).init( i, 0, 0, 1, 1, 1, 0x88228822 );
			myVertices.add( v );
		}
		vp.releaseRef( vref );

		final Triangle tref = tp.createRef();
		final Vertex3 vref1 = vp.createRef();
		final Vertex3 vref2 = vp.createRef();
		final Vertex3 vref3 = vp.createRef();
		for ( int i = 0; i < 3000000; ++i )
		{
			final Vertex3 v1 = myVertices.get( 3 * i, vref1 );
			final Vertex3 v2 = myVertices.get( 3 * i + 1, vref2 );
			final Vertex3 v3 = myVertices.get( 3 * i + 2, vref3 );
			final Triangle t = tp.create( tref ).init( v1, v2, v3 );
			myTriangles.add( t );
		}
		tp.releaseRef( tref );
		Pool.releaseRefs( vref1, vref2, vref3 ); // hmmm...?  maybe not...

		System.out.println( "done" );
	}

	static class Vertex3Layout extends PoolObjectLayout
	{
		final FloatArrayField position = floatArrayField( 3 );
		final FloatArrayField normal = floatArrayField( 3 );
		final ByteArrayField color = byteArrayField( 4 );
	}

	static final Vertex3Layout vertexLayout = new Vertex3Layout();

	static class Vertex3Pool extends Pool< Vertex3, BufferMappedElement >
	{
		final FloatArrayAttribute< Vertex3 > position;
		final FloatArrayAttribute< Vertex3 > normal;
		final ByteArrayAttribute< Vertex3 > color;

		public Vertex3Pool( final int initialCapacity )
		{
			super( initialCapacity, vertexLayout, Vertex3.class, SingleArrayMemPool.factory( BufferMappedElementArray.factory ) );
			position = new FloatArrayAttribute<>( vertexLayout.position, this );
			normal = new FloatArrayAttribute<>( vertexLayout.normal, this );
			color = new ByteArrayAttribute<>( vertexLayout.color, this );
		}

		public Vertex3 create()
		{
			return super.create( createRef() );
		}

		@Override
		public Vertex3 create( final Vertex3 obj )
		{
			return super.create( obj );
		}

		@Override
		public void delete( final Vertex3 obj )
		{
			super.delete( obj );
		}

		@Override
		protected Vertex3 createEmptyRef()
		{
			return new Vertex3( this );
		}

		public FloatBuffer getFloatBuffer()
		{
			final SingleArrayMemPool< BufferMappedElementArray, ? > memPool = ( SingleArrayMemPool< BufferMappedElementArray, ? > ) getMemPool();
			final BufferMappedElementArray dataArray =  memPool.getDataArray();
			return dataArray.getBuffer().asFloatBuffer();
		}
	}

	static class Vertex3 extends PoolObject< Vertex3, Vertex3Pool, BufferMappedElement >
	{
		private final FloatArrayAttributeValue position;
		private final FloatArrayAttributeValue normal;
		private final ByteArrayAttributeValue color;

		Vertex3( final Vertex3Pool pool )
		{
			super( pool );

			/*
			 * doesn't send property change events
			 */
//			position = pool.position.createQuietAttributeValue( this );
//			normal = pool.normal.createQuietAttributeValue( this );
//			color = pool.color.createQuietAttributeValue( this );

			/*
			 * sends property change events
			 */
			position = pool.position.createAttributeValue( this );
			normal = pool.normal.createAttributeValue( this );
			color = pool.color.createAttributeValue( this );
		}

		public Vertex3 init(
				final float x,
				final float y,
				final float z,
				final float nx,
				final float ny,
				final float nz,
				final int argb )
		{
			// like this:
			pool.position.setQuiet( this, 0, x );
			pool.position.setQuiet( this, 1, y );
			pool.position.setQuiet( this, 2, z );

			// or like this:
			normal.set( 0, nx );
			normal.set( 1, ny );
			normal.set( 2, nz );

			color.set( 0, ( byte ) ARGBType.alpha( argb ) );
			color.set( 1, ( byte ) ARGBType.red( argb ) );
			color.set( 2, ( byte ) ARGBType.green( argb ) );
			color.set( 3, ( byte ) ARGBType.blue( argb ) );
			return this;
		}

		@Override
		protected void setToUninitializedState()
		{}

		public float getX()
		{
			return position.get( 0 );
		}

		public void setX( final float x)
		{
			position.set( 0, x );
		}

		/*
		 * etc ...
		 */

		@Override
		public String toString()
		{
			return String.format( "v(%.2f, %.2f, %.2f, ...)", getX(), position.get( 1 ), position.get( 2 ) );
		}
	}

	static class TriangleLayout extends PoolObjectLayout
	{
		final IndexField v1 = indexField();
		final IndexField v2 = indexField();
		final IndexField v3 = indexField();
		// indexArrayField ... but IndexArrayAttribute not yet implemented
	}

	static final TriangleLayout triangleLayout = new TriangleLayout();

	static class TrianglePool extends Pool< Triangle, BufferMappedElement >
	{
		final IndexAttribute< Triangle > iv1;
		final IndexAttribute< Triangle > iv2;
		final IndexAttribute< Triangle > iv3;

		Vertex3Pool vertex3Pool;

		public TrianglePool( final Vertex3Pool vertex3Pool, final int initialCapacity )
		{
			super( initialCapacity, triangleLayout, Triangle.class, SingleArrayMemPool.factory( BufferMappedElementArray.factory ) );
			this.vertex3Pool = vertex3Pool;
			iv1 = new IndexAttribute<>( triangleLayout.v1, this );
			iv2 = new IndexAttribute<>( triangleLayout.v2, this );
			iv3 = new IndexAttribute<>( triangleLayout.v3, this );
		}

		public Triangle create()
		{
			return super.create( createRef() );
		}

		@Override
		public Triangle create( final Triangle obj )
		{
			return super.create( obj );
		}

		@Override
		public void delete( final Triangle obj )
		{
			super.delete( obj );
		}

		@Override
		protected Triangle createEmptyRef()
		{
			return new Triangle( this );
		}

		public FloatBuffer getFloatBuffer()
		{
			final SingleArrayMemPool< BufferMappedElementArray, ? > memPool = ( SingleArrayMemPool< BufferMappedElementArray, ? > ) getMemPool();
			final BufferMappedElementArray dataArray =  memPool.getDataArray();
			return dataArray.getBuffer().asFloatBuffer();
		}
	}

	static class Triangle extends PoolObject< Triangle, TrianglePool, BufferMappedElement >
	{
		Triangle( final TrianglePool pool )
		{
			super( pool );
		}

		public Triangle init(
				final Vertex3 v1,
				final Vertex3 v2,
				final Vertex3 v3 )
		{
			pool.iv1.setQuiet( this, pool.vertex3Pool.getId( v1 ) );
			pool.iv2.setQuiet( this, pool.vertex3Pool.getId( v2 ) );
			pool.iv3.setQuiet( this, pool.vertex3Pool.getId( v3 ) );
			return this;
		}

		@Override
		protected void setToUninitializedState()
		{}

		// index = 0,1,2
		public Vertex3 getVertex( final int index )
		{
			return getVertex( index, pool.vertex3Pool.createRef() );
		}

		// index = 0,1,2
		public Vertex3 getVertex( final int index, final Vertex3 ref )
		{
			switch ( index )
			{
			case 0:
				return pool.vertex3Pool.getObject( pool.iv1.get( this ), ref );
			case 1:
				return pool.vertex3Pool.getObject( pool.iv2.get( this ), ref );
			case 2:
				return pool.vertex3Pool.getObject( pool.iv3.get( this ), ref );
			default:
				throw new IllegalArgumentException();
			}
		}

		/*
		 * etc ...
		 */

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append( "triangle(" );
			final Vertex3 ref = pool.vertex3Pool.createRef();
			sb.append( getVertex( 0, ref ) );
			sb.append( ", " );
			sb.append( getVertex( 1, ref ) );
			sb.append( ", " );
			sb.append( getVertex( 2, ref ) );
			sb.append( ")" );
			return sb.toString();
		}
	}
}
