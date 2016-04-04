The following describes how to set up a Processing Library project in Eclipse and build it successfully, and to make your Library ready for distribution.

## HPGLGraphics Library for Processing

This library implements [HPGL](https://en.wikipedia.org/wiki/HPGL) (Hewlett-Packard Graphics Language) file creation from Processing sketches. It works in much the same way as the PDF Export and DXF Export libraries bundled with Processing.

### Getting started 

    import hpglgraphics.*;
    HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
    setPath("filename.hpgl");
    beginRecord(hpgl);
    // do some drawing
    endRecord();

Have a look at the examples included with the library. These demonstrate:

  * line()
  * ellipse() (and circles)
  * arc(0 (PIE, CHORD and OPEN)
  * rect()

### Additional methods

Some additional methods are available for controlling the HPGL output. This include:
  * selectPen(int pen);       // choose another pen (e.g. colour)
  * setPaperSize();           // "A3" or "A4". Landscape orientation is assumed.
  * setPath("filename.hpgl"); // required. HPGL is output to this file in the sketch directory.

The website for the library is [here](ggg).
Source code is [here](https://github.com/ciaron/HPGLGraphics)
