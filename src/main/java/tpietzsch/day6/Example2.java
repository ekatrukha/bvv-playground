package tpietzsch.day6;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.sequence.MultiResolutionSetupImgLoader;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import org.joml.Matrix4f;
import tpietzsch.day2.Shader;
import tpietzsch.day4.InputFrame;
import tpietzsch.day4.ScreenPlane1;
import tpietzsch.day4.WireframeBox1;
import tpietzsch.util.MatrixMath;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LINEAR;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_R16F;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES2.GL_RED;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_3D;

/**
 * Draw bounding box of source 0 timepoint 0.
 */
public class Example2 implements GLEventListener
{
	private final RandomAccessibleInterval< UnsignedShortType > rai;

	private final AffineTransform3D sourceTransform;

	private int vao;

	private Shader prog;

	private Shader progvol;

	private WireframeBox1 box;

	private ScreenPlane1 screenPlane;

	private int texture;

	public Example2( final RandomAccessibleInterval< UnsignedShortType > rai, final AffineTransform3D sourceTransform )
	{
		this.rai = rai;
		this.sourceTransform = sourceTransform;
	}

	@Override
	public void init( final GLAutoDrawable drawable )
	{
		final GL3 gl = drawable.getGL().getGL3();

		box = new WireframeBox1();
		box.updateVertices( gl, rai );
		screenPlane = new ScreenPlane1();
		screenPlane.updateVertices( gl, new FinalInterval( 640, 480 ) );

		prog = new Shader( gl, "ex1", "ex1" );
		progvol = new Shader( gl, "ex1", "ex2vol" );

		loadTexture( gl );

		gl.glEnable( GL_DEPTH_TEST );
	}

	private void loadTexture( final GL3 gl )
	{
		if ( rai.dimension( 0 ) % 2 != 0 )
			System.err.println( "possible GL_UNPACK_ALIGNMENT problem. glPixelStorei ..." );

		// ..:: TEXTURES ::..
		final int[] tmp = new int[ 1 ];
		gl.glGenTextures( 1, tmp, 0 );
		texture = tmp[ 0 ];
		gl.glBindTexture( GL_TEXTURE_3D, texture );
		int w = ( int ) rai.dimension( 0 );
		int h = ( int ) rai.dimension( 1 );
		int d = ( int ) rai.dimension( 2 );
		final ByteBuffer data = imgToBuffer( rai );

//		create volume black with white border:
//		Img< UnsignedShortType > img = ArrayImgs.unsignedShorts( w, h, d );
//		for ( int dim = 0; dim < 3; ++dim )
//		{
//			Views.iterable( Views.hyperSlice( img, dim, img.min( dim ) ) ).forEach( t -> t.set( 0xffff ) );
//			Views.iterable( Views.hyperSlice( img, dim, img.max( dim ) ) ).forEach( t -> t.set( 0xffff ) );
//		}
//		final ByteBuffer data = imgToBuffer( img );

		gl.glTexImage3D( GL_TEXTURE_3D, 0, GL_R16F, w, h, d, 0, GL_RED, GL_UNSIGNED_SHORT, data );
		gl.glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
		gl.glTexParameteri( GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
//		gl.glTexStorage3D();
//		gl.glTexImage3DMultisample();

		System.out.println( "Image Loaded" );
	}

	public static ByteBuffer imgToBuffer( RandomAccessibleInterval< UnsignedShortType > img )
	{
		assert img.numDimensions() == 3;

		final int bytesPerPixel = 2;
		int size = ( int ) Intervals.numElements( img ) * bytesPerPixel;
		final ByteBuffer buffer = ByteBuffer.allocateDirect( size );
		return imgToBuffer( img, buffer );
	}

	public static ByteBuffer imgToBuffer( RandomAccessibleInterval< UnsignedShortType > img, ByteBuffer buffer )
	{
		assert img.numDimensions() == 3;

		buffer.order( ByteOrder.LITTLE_ENDIAN );
		final ShortBuffer sbuffer = buffer.asShortBuffer();
		Cursor< UnsignedShortType > c = Views.iterable( img ).localizingCursor();
		while( c.hasNext() )
		{
			c.fwd();
			final int i = ( int ) IntervalIndexer.positionToIndex( c, img );
			sbuffer.put( i, c.get().getShort() );
		}
		return buffer;
	}

	@Override
	public void dispose( final GLAutoDrawable drawable )
	{}

	private final double screenPadding = 100;

	private double dCam = 2000;
	private double dClip = 1000;
	private double screenWidth = 640;
	private double screenHeight = 480;

	@Override
	public void display( final GLAutoDrawable drawable )
	{
		final GL3 gl = drawable.getGL().getGL3();

		gl.glClearColor( 0f, 0f, 0f, 1f );
		gl.glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

		final Matrix4f model = new Matrix4f();
		final Matrix4f view = new Matrix4f();
		final Matrix4f projection = new Matrix4f();
		MatrixMath.affine( sourceTransform, model );
		view.set( new float[] {0.56280f, -0.13956f, 0.23033f, 0.00000f, 0.00395f, 0.53783f, 0.31621f, 0.00000f, -0.26928f, -0.28378f, 0.48603f, 0.00000f, 96.02715f, 211.68768f, -186.46806f, 1.00000f } );
		projection.set( new float[] {5.40541f, 0.00000f, 0.00000f, 0.00000f, -0.00000f, -6.89655f, -0.00000f, -0.00000f, -0.00000f, -0.00000f, 2.00000f, 1.00000f, -1729.72974f, 1655.17236f, 1000.00000f, 2000.00000f } );
		final Matrix4f ipvm = new Matrix4f( projection ).mul( view ).mul( model ).invert();

		System.out.println( "projection = " + projection );
		System.out.println( "ipvm = " + ipvm );

		prog.use( gl );
		prog.setUniform( gl, "model", model );
		prog.setUniform( gl, "view", view );
		prog.setUniform( gl, "projection", projection );

		prog.setUniform( gl, "color", 1.0f, 0.5f, 0.2f, 1.0f );
		box.draw( gl );

		model.identity();
		view.identity();
		progvol.use( gl );
		progvol.setUniform( gl, "model", model );
		progvol.setUniform( gl, "view", view );
		progvol.setUniform( gl, "projection", projection );
		progvol.setUniform( gl, "ipvm", ipvm );
		progvol.setUniform( gl, "sourcemin", rai.min( 0 ), rai.min( 1 ), rai.min( 2 ) );
		progvol.setUniform( gl, "sourcemax", rai.max( 0 ), rai.max( 1 ), rai.max( 2 ) );
		progvol.setUniform( gl, "volume", 0 );
		progvol.setUniform( gl, "invVolumeSize",
				( float ) ( 1.0 / rai.dimension( 0 ) ),
				( float ) ( 1.0 / rai.dimension( 1 ) ),
				( float ) ( 1.0 / rai.dimension( 2 ) ) );
		gl.glActiveTexture( GL_TEXTURE0 );
		gl.glBindTexture( GL_TEXTURE_3D, texture );
		double min = 962;
		double max = 6201;
		double fmin = min / 0xffff;
		double fmax = max / 0xffff;
		double s = 1.0 / ( fmax - fmin );
		double o = -fmin * s;
		progvol.setUniform( gl, "intensity_offset", ( float ) o );
		progvol.setUniform( gl, "intensity_scale", ( float ) s );

		gl.glEnable( GL_BLEND );
		gl.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
		screenPlane.draw( gl );
		gl.glDisable( GL_BLEND );
	}

	@Override
	public void reshape( final GLAutoDrawable drawable, final int x, final int y, final int width, final int height )
	{}

	public static void main( final String[] args ) throws SpimDataException
	{
		final String xmlFilename = "/Users/pietzsch/workspace/data/111010_weber_full.xml";
		final SpimDataMinimal spimData = new XmlIoSpimDataMinimal().load( xmlFilename );
		MultiResolutionSetupImgLoader< UnsignedShortType > sil = ( MultiResolutionSetupImgLoader< UnsignedShortType > ) spimData.getSequenceDescription().getImgLoader().getSetupImgLoader( 0 );
		final int level = 0; //source.getNumMipmapLevels() - 1;
		final RandomAccessibleInterval< UnsignedShortType > rai = sil.getImage( 1, level );
		final AffineTransform3D sourceTransform = spimData.getViewRegistrations().getViewRegistration( 1, 0 ).getModel();

		final InputFrame frame = new InputFrame( "Example2", 640, 480 );
		InputFrame.DEBUG = false;
		Example2 glPainter = new Example2( rai, sourceTransform );
		frame.setGlEventListener( glPainter );
		frame.show();
	}
}