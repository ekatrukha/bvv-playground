/*-
 * #%L
 * Volume rendering of bdv datasets
 * %%
 * Copyright (C) 2018 - 2021 Tobias Pietzsch
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
package btbvv.util;

import bdv.tools.brightness.ConverterSetup;
import bdv.tools.brightness.MinMaxGroup;
import bdv.util.Bounds;
import bdv.viewer.ConverterSetupBounds;
import bdv.viewer.SourceAndConverter;
import btbvv.tools.GammaConverterSetup;
import btbvv.tools.MinMaxGroupBT;
import btbvv.tools.SetupAssignmentsBT;
import bvvbigtrace.multires.SourceStacks;

import java.util.HashSet;
import java.util.List;

import net.imglib2.RealInterval;
import net.imglib2.type.numeric.ARGBType;

public class BvvStackSource< T > extends BvvSource
{
	private final T type;

	private final List< ConverterSetup > converterSetups;

	private final List< SourceAndConverter< T > > sources;

	protected BvvStackSource(
			final BvvHandle bvv,
			final int numTimepoints,
			final T type,
			final List< ConverterSetup > converterSetups,
			final List< SourceAndConverter< T > > sources )
	{
		super( bvv, numTimepoints );
		this.type = type;
		this.converterSetups = converterSetups;
		this.sources = sources;
	}

	@Override
	public void removeFromBdv()
	{
		getBvvHandle().remove( converterSetups, sources, null, null, null );
		getBvvHandle().removeBvvSource( this );
		setBdvHandle( null );
	}

	@Override
	protected boolean isPlaceHolderSource()
	{
		return false;
	}

	@Override
	public void setColor( final ARGBType color )
	{
		for ( final ConverterSetup setup : converterSetups )
			setup.setColor( color );
	}

	@Override
	public void setDisplayRange( final double min, final double max )
	{
		for ( final ConverterSetup setup : converterSetups )
			setup.setDisplayRange( min, max );
	}
	
	@Override
	public void setDisplayGamma( final double gamma )
	{
		for ( final ConverterSetup setup : converterSetups )
		{
			if (setup instanceof GammaConverterSetup)
			{
				final GammaConverterSetup gconverter = ((GammaConverterSetup)setup);
				gconverter.setDisplayGamma(gamma);
			}
		}
	}
	
	@Override
	public void setAlphaRange( final double minAlpha, final double maxAlpha )
	{
		for ( final ConverterSetup setup : converterSetups )
		{
			if (setup instanceof GammaConverterSetup)
			{
				final GammaConverterSetup gconverter = ((GammaConverterSetup)setup);
				gconverter.setAlphaRange(minAlpha, maxAlpha);
			}
		}
	}
	
	@Override
	public void setAlphaGamma( final double gammaAlpha )
	{
		for ( final ConverterSetup setup : converterSetups )
		{
			if (setup instanceof GammaConverterSetup)
			{
				final GammaConverterSetup gconverter = ((GammaConverterSetup)setup);
				gconverter.setAlphaGamma(gammaAlpha);
			}
		}
	}
	
	@Override
	public void setRenderType(final int nRenderType)
	{
		for ( final ConverterSetup setup : converterSetups )
		{
			if (setup instanceof GammaConverterSetup)
			{
				final GammaConverterSetup gconverter = ((GammaConverterSetup)setup);
				gconverter.setRenderType(nRenderType);
			}
		}
	}
	
	@Override
	public void setLUT(final float[][] lut_in)
	{
		for ( final ConverterSetup setup : converterSetups )
		{
			if (setup instanceof GammaConverterSetup)
			{
				final GammaConverterSetup gconverter = ((GammaConverterSetup)setup);
				gconverter.setLUT(lut_in);
			}
		}
	}
	
	@Override
	public void setCropInterval(RealInterval cropInt)
	{
		for ( final ConverterSetup setup : converterSetups )
		{
			if (setup instanceof GammaConverterSetup)
			{
				final GammaConverterSetup gconverter = ((GammaConverterSetup)setup);
				gconverter.setCropInterval(cropInt);
			}
		}
	}

	@Override
	public void setDisplayRangeBounds( final double min, final double max )
	{
		final ConverterSetupBounds bounds = getBvvHandle().getConverterSetups().getBounds();
		for ( final ConverterSetup setup : converterSetups )
			bounds.setBounds( setup, new Bounds( min, max ) );

		// TODO: REMOVE
		final HashSet< MinMaxGroup > groups = new HashSet<>();
		final SetupAssignmentsBT sa = getBvvHandle().getSetupAssignments();
		for ( final ConverterSetup setup : converterSetups )
			groups.add( sa.getMinMaxGroup( setup ) );
		for ( final MinMaxGroup group : groups )
			group.setRange( min, max );
	}
	
	@Override
	public void setDisplayGammaRangeBounds( final double min, final double max )
	{
		// TODO: REMOVE
		final HashSet< MinMaxGroup > groups = new HashSet<>();
		final SetupAssignmentsBT sa = getBvvHandle().getSetupAssignments();
		for ( final ConverterSetup setup : converterSetups )
			groups.add( sa.getMinMaxGroup( setup ) );
		for ( final MinMaxGroup group : groups )
		{
			if(group instanceof MinMaxGroupBT)
				((MinMaxGroupBT)group).gammaRange.setRange(min, max);
		}
	}
	
	@Override
	public void setAlphaRangeBounds(final double min, final double max)
	{
		// TODO: REMOVE
		final HashSet< MinMaxGroup > groups = new HashSet<>();
		final SetupAssignmentsBT sa = getBvvHandle().getSetupAssignments();
		for ( final ConverterSetup setup : converterSetups )
			groups.add( sa.getMinMaxGroup( setup ) );
		for ( final MinMaxGroup group : groups )
		{
			if(group instanceof MinMaxGroupBT)
				((MinMaxGroupBT)group).alphaRange.setRange(min, max);
		}
	}
	
	@Override
	public void setAlphaGammaRangeBounds(final double min, final double max)
	{
		// TODO: REMOVE
		final HashSet< MinMaxGroup > groups = new HashSet<>();
		final SetupAssignmentsBT sa = getBvvHandle().getSetupAssignments();
		for ( final ConverterSetup setup : converterSetups )
			groups.add( sa.getMinMaxGroup( setup ) );
		for ( final MinMaxGroup group : groups )
		{
			if(group instanceof MinMaxGroupBT)
				((MinMaxGroupBT)group).gammaAlphaRange.setRange(min, max);
		}
	}

	@Override
	public void setCurrent()
	{
		getBvvHandle().getViewerPanel().getVisibilityAndGrouping().setCurrentSource( sources.get( 0 ).getSpimSource() );
	}

	@Override
	public boolean isCurrent()
	{
		return sources.contains( getBvvHandle().getViewerPanel().state().getCurrentSource() );
	}

	@Override
	public void setActive( final boolean isActive )
	{
		getBvvHandle().getViewerPanel().state().setSourcesActive( sources, isActive );
	}

//	public T getType()
//	{
//		return type;
//	}

	public List< ConverterSetup > getConverterSetups()
	{
		return converterSetups;
	}

	public List< SourceAndConverter< T > > getSources()
	{
		return sources;
	}

	public void invalidate()
	{
		for ( final SourceAndConverter< T > source : sources )
			SourceStacks.invalidate( source.getSpimSource() );

		final BvvHandle bvv = getBvvHandle();
		if ( bvv != null )
			bvv.getViewerPanel().requestRepaint();
	}
}
