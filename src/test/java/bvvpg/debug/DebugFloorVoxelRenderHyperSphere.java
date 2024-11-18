package bvvpg.debug;

import java.util.List;

import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Point;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import bvvpg.vistools.Bvv;
import bvvpg.vistools.BvvFunctions;
import bvvpg.vistools.BvvSource;
import bvvpg.vistools.BvvStackSource;
import ij.IJ;
import ij.ImagePlus;
import mpicbg.spim.data.SpimDataException;

public class DebugFloorVoxelRenderHyperSphere
{
	public static void main( final String[] args )
	{
		int nRadius = 35;
	
		//hyperSphere example
		long [] dim = new long[] {2*nRadius+2,2*nRadius+2,2*nRadius+2};
		Point center = new Point( 3 );
		center.setPosition( nRadius+1 , 0 );
		center.setPosition( nRadius+1 , 1 );
		center.setPosition( nRadius+1 , 2 );
		ArrayImg< UnsignedShortType, ShortArray > sphereRai = ArrayImgs.unsignedShorts(dim);
		HyperSphere< UnsignedShortType > hyperSphere =
				new HyperSphere<>( sphereRai, center, nRadius);			
		HyperSphereCursor< UnsignedShortType > cursor = hyperSphere.localizingCursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.get().setInteger( Math.round(Math.random()*255.0) );
		}
		final BvvSource source = BvvFunctions.show( sphereRai, "sphere_left" );
		source.setClipInterval( new FinalInterval(new long[] {0,0,0}, new long[] {nRadius,2*nRadius+2,2*nRadius+2}) );
		source.setDisplayRangeBounds( 0, 255 );
		source.setRenderType( 1 );
		source.setAlphaRangeBounds( 0, 255 );
		source.setLUT( "Spectrum" );
		source.setAlphaRangeBounds( 0, 1 );
		final BvvSource source2 = BvvFunctions.show( sphereRai, "sphere_right" ,Bvv.options().addTo( source ));
		source2.setClipInterval( new FinalInterval(new long[] {nRadius,0,0}, new long[] {2*nRadius+2,2*nRadius+2,2*nRadius+2}) );
		source2.setDisplayRangeBounds( 0, 255 );
		source2.setRenderType( 1 );
		source2.setAlphaRangeBounds( 0, 255 );
		source2.setVoxelRenderInterpolation( 1 );
		source2.setLUT( "Spectrum" );
		source2.setAlphaRangeBounds( 0, 1 );
	}
}
