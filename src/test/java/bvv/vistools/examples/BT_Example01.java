/*-
 * #%L
 * Volume rendering of bdv datasets with gamma and transparency option
 * %%
 * Copyright (C) 2022 - 2024 Cell Biology, Neurobiology and Biophysics
 * Department of Utrecht University.
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
package bvv.vistools.examples;


import java.awt.image.IndexColorModel;
import java.util.List;

import javax.swing.UIManager;

import org.fusesource.jansi.Ansi.Color;

import com.formdev.flatlaf.FlatIntelliJLaf;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import btbvv.vistools.BvvFunctions;
import btbvv.vistools.BvvSource;
import btbvv.vistools.BvvStackSource;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.LutLoader;
import ij.process.ByteProcessor;
import mpicbg.spim.data.SpimDataException;
import net.imglib2.FinalRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;


public class BT_Example01 {
	
	/**
	 * Show 16-bit volume, change rendering type and gamma
	 */
	public static void main( final String[] args )
	{
		
		//regular tif init
		/**/
		//final ImagePlus imp = IJ.openImage( "https://imagej.nih.gov/ij/images/t1-head.zip" );
		final ImagePlus imp = IJ.openImage( "/home/eugene/Desktop/projects/BigTrace/BigTrace_data/t1-head.tif" );
		final Img< UnsignedShortType > img = ImageJFunctions.wrapShort( imp );
		final BvvSource source = BvvFunctions.show( img, "t1-head" );
	
		double [] minI = img.minAsDoubleArray();
		double [] maxI = img.maxAsDoubleArray();
		/**/

		//BDV XML init
		/*
		final String xmlFilename = "/home/eugene/Desktop/head/export.xml";
		SpimDataMinimal spimData = null;
		try {
			spimData = new XmlIoSpimDataMinimal().load( xmlFilename );
		} catch (SpimDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		final List< BvvStackSource< ? > > sources = BvvFunctions.show( spimData );
		final BvvSource source = sources.get(0);
		double [] minI = spimData.getSequenceDescription().getImgLoader().getSetupImgLoader(0).getImage(0).minAsDoubleArray();
		double [] maxI = spimData.getSequenceDescription().getImgLoader().getSetupImgLoader(0).getImage(0).maxAsDoubleArray();
		*/
	
		
		source.setDisplayRange(0, 655);
		source.setDisplayGamma(0.5);
		
		
		//set volumetric rendering (1), instead of max intensity max intensity (0)
		source.setRenderType(1);
		
		//DisplayRange maps colors (or LUT values) to intensity values
		source.setDisplayRange(0, 400);
		//it is also possible to change LUT gamma value
		//source.setDisplayGamma(0.9);
		
		//alpha channel to intensity mapping can be changed independently
		source.setAlphaRange(0, 500);
		//it is also possible to change alpha-channel gamma value
		//source.setAlphaGamma(0.9);
		
		//assign a "Fire" lookup table to this source
		//(input: float [256][3], the last index (color component) changes from 0 to 1)
		//source.setLUT(getRGBLutTable("Fire"));
		source.setLUT(getRGBLutTable("Spectrum"));
		
		ByteProcessor ish = new ByteProcessor(256,1);
		for (int i=0; i<256; i++)
			for (int j=0; j<1; j++)
				ish.putPixel(i, j, i);
		ImagePlus ccc = new ImagePlus("test LUT",ish);
		ccc.show();
		IJ.run("Fire");
		IJ.run("RGB Color");
		Img<ARGBType> rai = ImageJFunctions.wrapRGBA(ccc);
		source.setchLUT(rai);
		//ccc.close();
		
		//clip half of the volume along Z axis in the shaders
		//clipInterval is defined inside the "raw", non-transformed data interval		
		minI[2]=0.5*maxI[2];		
		//source.setClipInterval(new FinalRealInterval(minI,maxI));
		
		
	}
	
	//a helper function to get LUT array from ImageJ
	static public float [][] getRGBLutTable(String sLUTName)
	{
				
		IndexColorModel icm = LutLoader.getLut(sLUTName);
		
		int size = icm.getMapSize();
		byte [][] colors = new byte[3][size];

		icm.getReds(colors[0]);
		icm.getGreens(colors[1]);
		icm.getBlues(colors[2]);
		
		float [][] RGBLutTable = new float[size][3];
		
		for(int i=0;i<size;i++)
		{			
			for (int c=0;c<3;c++)
			{
				RGBLutTable[i][c]=(float)((colors[c][i]& 0xff)/255.0f);
			}
		}
		
		return RGBLutTable;
		
	}
}
