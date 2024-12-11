package bvvpg.debug;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.viewer.SourceAndConverter;
import bvvpg.vistools.Bvv;
import bvvpg.vistools.BvvFunctions;
import bvvpg.vistools.BvvSource;
import bvvpg.vistools.BvvStackSource;
import mpicbg.spim.data.SpimDataException;

public class DebugRestartBVV
{
	public static void main( final String[] args )
	{
		final String xmlFilename = "/home/eugene/Desktop/projects/BigTrace/BigTrace_data/t1-head.xml";
		SpimDataMinimal spimData = null;
		try {
			spimData = new XmlIoSpimDataMinimal().load( xmlFilename );
		} catch (SpimDataException e) {
			e.printStackTrace();
		}		
		final List< BvvStackSource< ? > > sources = BvvFunctions.show( spimData );
		final BvvSource source = sources.get(0);
		source.setDisplayRangeBounds( 0, 1200 );
		source.setDisplayRange(0, 655);
		Set< SourceAndConverter< ? > > sacs = source.getBvvHandle().getViewerPanel().state().getVisibleAndPresentSources();
		Iterator< SourceAndConverter< ? > > it = sacs.iterator();
		SourceAndConverter< ? > ss = it.next();
		BvvFunctions.show(ss,1,Bvv.options().frameTitle( "test"));
		
//		Bvv bvv2 = BvvFunctions.show( Bvv.options().frameTitle( "BigVolumeViewer2" ));
//		source.removeFromBdv();
//		source.setBdvHandle( bvv2.getBvvHandle() );
//		bvv2.getBvvHandle().addBvvSource( source );
		
	}
}
