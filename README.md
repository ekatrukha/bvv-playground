[![Build Status](https://github.com/UU-cellbiology/bvv-playground/actions/workflows/build.yml/badge.svg)](https://github.com/UU-cellbiology/bvv-playground/actions/workflows/build.yml)  
 
# BigVolumeViewer-playground

This is a fork of [BVV](https://github.com/bigdataviewer/bigvolumeviewer-core) with some additional features:
- "alpha-bending" volume rendering method (transparency/illumination);
- alpha opacity/transparency slider;
- gamma correction for brightness/opacity;
- Lookup tables (LUTs, custom and coming from ImageJ);
- clipping of displayed sources in shaders (optionally using custom transform).

Currently synced to BVV version 0.3.3 (this [commit](https://github.com/bigdataviewer/bigvolumeviewer-core/commit/60fe3d0595f1a68dd45f85e216f09b369eaa165d)).

## How to install it for users

Download latest zip archive with jar files from <a href="https://github.com/ekatrukha/bvv-playground/releases">releases</a>  
(it is called *bvv-playground-X.X.X_jar_files.zip*),   
extract and put them to the _jar_ folder of the _latest_ FIJI installation.

The plugin should appear in the _Plugins>BigDataViewer>BVV-playground_

## Additional features

### Volumetric rendering
A new shortcut <kbd>O</kbd> (letter) is used to switch between "Maximum intensity projection" and "Volumetric" rendering. By "volumetric" I mean "transparency" or "alpha-blending" ray-casting. These two different rendering methods are illustrated below:  
_Maximum intensity_  
![Maximum intensity render](https://katpyxa.info/software/bvv_playground/bvvPG_maximum_intensity_render.png)  
_Volumetric_  
![Maximum intensity render](https://katpyxa.info/software/bvv_playground/bvvPG_volumetric_render.png)  

### Gamma and opacity sliders
The standard "Brightness and Color" dialog (shortcut <kbd>S</kbd>) is now different:

![brighntess dialog collapsed](https://katpyxa.info/software/bvv_playground/bvvPG_brightness_0.2.0.png)  

*(works also with Cards panel, activated by shortcut <kbd>P</kbd>)

It uses range sliders (and one can pull the range by dragging the middle interval).

A standard "brightness" slider is now located next to the color/LUT icon/button, it maps image intensity values to specific colors.  
By clicking on the "three trianlges" button it can be expanded to show advanced settings.  
![brighntess dialog expanded](https://katpyxa.info/software/bvv_playground/bvvPG_brightness_expanded_0.2.0.png)  

There is a new slider marked "**γ**" that adjusts non-linear color/LUT mapping by introducing [gamma](https://en.wikipedia.org/wiki/Gamma_correction) correction (a power-law).  

In addition to color, one can also independently adjust the mapping of intensity values to the opacity using "**α**" range slider (with a corresponding "**γ α**" gamma adjustment slider below).

A new additional checkbox on the left is used to synchronize top pair of sliders (color/LUT + **γ**) with the bottom pair (**α** + **γ α**), but not the other way around. It also synchronizes slider ranges. It is useful to keep it selected in the beginning, to see the volume, and later fine-tune the alpha values independently (unselected) for a better result. It is especially helpful in the volumetric rendering mode.   

### Lookup tables (LUTs)

In a brightness dialog, right clicking on the color icon displayes the list of ImageJ LUTs that can be selected and applied to a specific source.   

![LUT selection](https://katpyxa.info/software/bvv_playground/bvvPG_lut_selection_0.2.0.gif)   

The left mouse button click still activates a dialog for a monochromatic, "single-color" painting of voxels. LUTs can be specified in Cards dialog, also on the Source and Group tables by using the right mouse button click too.   

## For developers

A list of additional methods (adjusting gamma and opacity, adding LUTs and volume clipping) is illustrated by [this example](https://github.com/ekatrukha/bvv-playground/blob/master/src/test/java/bvv/vistools/examples/BT_Example01.java).  
There is a possibility to specify "clipping transform", shown [here](https://github.com/ekatrukha/bvv-playground/blob/master/src/test/java/bvv/vistools/examples/BT_Example02.java).   
   
The project is currently under development and has a lot of "experimental" code (i.e. not clean and in the state of "well, at least it works"). So comments and optimization suggestions are welcome.

### Adding project in maven
This project lives in _scijava.public_ maven repository.
To use it in your own project, add to your _pom.xml_ :
```
<repositories>
....	
	<repository>
		<id>scijava.public</id>
		<url>https://maven.scijava.org/content/groups/public</url>
	</repository>
</repositories>
```
and add the corresponding dependency:

```
<dependency>
  <groupId>nl.uu.science.cellbiology</groupId>
  <artifactId>bvv-playground</artifactId>
  <version>X.X.X</version>
</dependency>
```


----------
Developed in <a href='http://cellbiology.science.uu.nl/'>Cell Biology group</a> of Utrecht University.  
<a href="mailto:katpyxa@gmail.com">E-mail</a> for any questions or tag <a href="https://forum.image.sc/u/ekatrukha/summary">@ekatrukha</a> at <a href="https://forum.image.sc/">image.sc</a> forum.

