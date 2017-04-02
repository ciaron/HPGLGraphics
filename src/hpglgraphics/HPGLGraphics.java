package hpglgraphics;

import java.io.*;

import processing.core.*;

/**
 * This is a library for writing HPGL (plotter) files using beginRecord()/endRecord()
 * Inspired by https://github.com/gregersn/HPGL
 * Borrows from http://phi.lho.free.fr/programming/Processing/P8gGraphicsSVG/
 * the OBJExport library and the Processing DXF library.
 * 
 * (the tag example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 * @example HPGL
 */

public class HPGLGraphics extends PGraphics {

  File file;
  PrintWriter writer;
  public static final String HPGL = "hpglgraphics.HPGLGraphics";
  
  /** Stores all the transformations */
  /* from HPGL by gsn */
  private PMatrix2D transformMatrix;

  private boolean matricesAllocated = false;
  
  private String size;  // paper size, A3 or A4 for now
  private int MATRIX_STACK_DEPTH = 32;
  private int transformCount = 0;
  private PMatrix2D transformStack[] = new PMatrix2D[MATRIX_STACK_DEPTH];
  
  private double chordangle = 5.0; // HPGL default is 5 degrees
  private int A4W = 11040;
  private int A4H =  7721;
  private int A3W = 16158;
  private int A3H = 11040;
  
  private int A4Wmm = 297;
  private int A4Hmm = 210;
  private int A3Wmm = 420;
  private int A3Hmm = 297;
      
  private char terminator = (char)3; // Label/text terminator;
  
  private double[][] shapeVertices;
  //private double[][] bezierVertices;
	
  public final static String VERSION = "##library.prettyVersion##";

  /**
   * a Constructor, usually called in the setup() method in your sketch to
   * initialize and start the Library.
   * 
   * @example simple_demo
   * 
   */

  public HPGLGraphics(){
	   super();
    welcome();
    
//    this.path = parent.sketchPath("output.hpgl");
//    file = new File(this.path);
    
    if (!matricesAllocated) {   
      // Init default matrix
      this.transformMatrix = new PMatrix2D();
      matricesAllocated = true;
     }
  }
  
  private void welcome() {
	   System.out.println("##library.name## ##library.prettyVersion## by ##author##");
  }
    
  /**
   * This method sets the plotter output size. Used to scale the Processing sketch to
   * match either A3 or A4 dimensions (only these supported now)
   * 
   * @example simple_demo
   * @param size String: "A3" or "A4", depending on the intended plot size
   */
  public void setCurveDetail(int d) {
    curveDetail = d;
  }
  
  public void setPaperSize(String size) {
    this.size=size;
  }
  
  /**
   * This method sets the path and filename for the HPGL output.
   * Must be called from the Processing sketch
   * 
   * @example simple_demo
   * @param path String: name of file to save to
   */
  public void setPath(String path) {
    
    this.path = parent.sketchPath(path);
   	if (path != null) {
	     file = new File(this.path);

   	}
	   if (file == null) {
		    throw new RuntimeException("Something went wrong trying to create file "+this.path);
   	}
  }
  
  /**
   * This method selects plotter pen via the HPGL 'SP' instruction.
   * Called from the Processing sketch.
   * 
   * @example simple_demo
   * @param pen : integer number of pen to select (depends on plotter)
   */
  public void selectPen(int pen) {
    writer.println("SP"+pen+";");    
  }
  
  public void beginDraw() {
	   
   	if (this.path == null) {
   	  //throw new RuntimeException("call setPath() before recording begins!");
   	  System.out.println("here1");
   	  this.path = parent.sketchPath("output.hpgl");
   	  file = new File(this.path);
  	 }
	
	   if (this.size == null) {
		    this.size="A4";
		    System.out.println("setPaperSize undefined: defaulting to A4");
   	}
	
    if (writer == null) {
	     try {
	       writer = new PrintWriter(new FileWriter(file));
	     } catch (IOException e) {
	       throw new RuntimeException(e);  // java 1.4+
	     }
	     writeHeader();
   	}
  }

  //public void endDraw(String path) {
  public void endDraw() {

    writeFooter();
    writer.flush();
  }

  public void endRecord() {   
      endDraw();
      dispose(); 
  }

  public void endRecord(String filePath) {   
      setPath(filePath);
      endRecord();
  }
  /**
   * begin a shape.
   * Called from the Processing sketch.
   * 
   * @example shapes
   *
   */
  public void beginShape() {
	   shapeVertices = new double[DEFAULT_VERTICES][VERTEX_FIELD_COUNT];
	   vertexCount = 0;
  }
  
  /**
   * begin a shape
   * 
   * @example shapes
   * @param kind : type of shape (see Processing docs for beginShape())
   */
  public void beginShape(int kind) {
    shapeVertices = new double[DEFAULT_VERTICES][VERTEX_FIELD_COUNT];
    shape = kind;
    vertexCount = 0;
  }
  
  public void endShape() {
    endShape(OPEN);
  }
  
  public void endShape(int mode){
	  double x, y;
	  double[] xy = new double[2];
	  
      int stop = vertexCount - 1;
      
      x=shapeVertices[0][X];
      y=shapeVertices[0][Y];
      xy = scaleXY((float)x, (float)y);
      
      writer.println("PU" + xy[X] + "," + xy[Y] + ";");
    
      for (int i=1; i<=stop; i++){
          x=shapeVertices[i][X];
          y=shapeVertices[i][Y];
          xy = scaleXY((float)x, (float)y);
          writer.println("PD" + xy[X] + "," + xy[Y] + ";");
      }
    
      if (mode==CLOSE) {
          x=shapeVertices[0][X];
          y=shapeVertices[0][Y];
          xy = scaleXY((float)x, (float)y);
          // Pen down to starting point
          writer.println("PD" + xy[X] + "," + xy[Y] + ";");
      }
    
      writer.println("PU;");
      vertexCount = 0;
      curveVertexCount = 0;
      shapeVertices = null;
      curveVertices = null;
    
  }
  /**
   * This method converts vertices in Processing coordinates to
   * plotter coordinates.
   * 
   * @example shapes
   * 
   */
  public void vertex(float x, float y) {
	
	//System.out.println(" "+x+" "+y);
   	curveVertexCount = 0;
    vertexCount++;
    
    // check if shapeVertices is big enough, extend if necessary.
    // via OBJExport (MeshExport.java)
    if(vertexCount >= shapeVertices.length) {
      double newVertices[][] = new double[shapeVertices.length*2][VERTEX_FIELD_COUNT];
      System.arraycopy(shapeVertices, 0, newVertices, 0, shapeVertices.length);
      shapeVertices = newVertices;
    }
    
    shapeVertices[vertexCount-1][X] = x;
    shapeVertices[vertexCount-1][Y] = y;

  }
  
  // CURVE VERTEX CODE FROM PGraphics.java

  protected void curveVertexCheck() {
    curveVertexCheck(shape);
  }

  /**
   * Perform initialization specific to curveVertex(), and handle standard
   * error modes. Can be overridden by subclasses that need the flexibility.
   */
  protected void curveVertexCheck(int shape) {

    // to improve code init time, allocate on first use.
    if (curveVertices == null) {
      curveVertices = new float[128][3];
    }

    if (curveVertexCount == curveVertices.length) {
      // Can't use PApplet.expand() cuz it doesn't do the copy properly
      float[][] temp = new float[curveVertexCount << 1][3];
      System.arraycopy(curveVertices, 0, temp, 0, curveVertexCount);
      curveVertices = temp;
    }
    curveInitCheck();
  }
  /**
   * 
   * @example curves
   * 
   */
  public void curveVertex(float x, float y) {
	    curveVertexCheck();
	    float[] vertex = curveVertices[curveVertexCount];
	    vertex[X] = x;
	    vertex[Y] = y;
	    curveVertexCount++;

	    // draw a segment if there are enough points
	    if (curveVertexCount > 3) {
	      curveVertexSegment(curveVertices[curveVertexCount-4][X],
	                         curveVertices[curveVertexCount-4][Y],
	                         curveVertices[curveVertexCount-3][X],
	                         curveVertices[curveVertexCount-3][Y],
	                         curveVertices[curveVertexCount-2][X],
	                         curveVertices[curveVertexCount-2][Y],
	                         curveVertices[curveVertexCount-1][X],
	                         curveVertices[curveVertexCount-1][Y]);
	    }
	  }
  
  /**
   * Handle emitting a specific segment of Catmull-Rom curve. This can be
   * overridden by subclasses that need more efficient rendering options.
   */
  protected void curveVertexSegment(float x1, float y1,
                                    float x2, float y2,
                                    float x3, float y3,
                                    float x4, float y4) {
    float x0 = x2;
    float y0 = y2;

    PMatrix3D draw = curveDrawMatrix;

    float xplot1 = draw.m10*x1 + draw.m11*x2 + draw.m12*x3 + draw.m13*x4;
    float xplot2 = draw.m20*x1 + draw.m21*x2 + draw.m22*x3 + draw.m23*x4;
    float xplot3 = draw.m30*x1 + draw.m31*x2 + draw.m32*x3 + draw.m33*x4;

    float yplot1 = draw.m10*y1 + draw.m11*y2 + draw.m12*y3 + draw.m13*y4;
    float yplot2 = draw.m20*y1 + draw.m21*y2 + draw.m22*y3 + draw.m23*y4;
    float yplot3 = draw.m30*y1 + draw.m31*y2 + draw.m32*y3 + draw.m33*y4;

    // vertex() will reset splineVertexCount, so save it
    int savedCount = curveVertexCount;

    vertex(x0, y0);
    for (int j = 0; j < curveDetail; j++) {
      x0 += xplot1; xplot1 += xplot2; xplot2 += xplot3;
      y0 += yplot1; yplot1 += yplot2; yplot2 += yplot3;
      vertex(x0, y0);
    }
    curveVertexCount = savedCount;
  }
  
  /**
   * 
   * @example curves
   * 
   */
  public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4){
      // We add vertices to shapeVertices here. Code mostly copies from PGraphics. Is there a better way?
	  // But here we are...
      bezierDetail(bezierDetail);
	  PMatrix3D draw = bezierDrawMatrix;
	 
	  double[] prev = shapeVertices[vertexCount-1];
	  double x1 = prev[X];
	  double y1 = prev[Y];
	  
	  double xplot1 = draw.m10*x1 + draw.m11*x2 + draw.m12*x3 + draw.m13*x4;
	  double xplot2 = draw.m20*x1 + draw.m21*x2 + draw.m22*x3 + draw.m23*x4;
	  double xplot3 = draw.m30*x1 + draw.m31*x2 + draw.m32*x3 + draw.m33*x4;
	
	  double yplot1 = draw.m10*y1 + draw.m11*y2 + draw.m12*y3 + draw.m13*y4;
	  double yplot2 = draw.m20*y1 + draw.m21*y2 + draw.m22*y3 + draw.m23*y4;
	  double yplot3 = draw.m30*y1 + draw.m31*y2 + draw.m32*y3 + draw.m33*y4;
	
	  for (int j = 0; j < bezierDetail; j++) {
	    x1 += xplot1; xplot1 += xplot2; xplot2 += xplot3;
	    y1 += yplot1; yplot1 += yplot2; yplot2 += yplot3;
	    vertex((float)x1, (float)y1);
	  } 
  }
  
  // UTILITIES
  
  /**
   * This method returns x,y coordinates converted to plotter coordinates
   * It also flips the y-coordinate to match Processing axis orientation.
   * 
   */
  private double[] scaleToPaper(double[] xy) {
    
   	double[] xy1 =  new double[xy.length];
    
   	double W=0.0;
   	double H=0.0;
    
    if (this.size == "A3") {
      W=A3W; H=A3H;
    } else if (this.size == "A4"){
      W=A4W; H=A4H;
    }

    // Assume width>height, i.e. always plotting in landscape orientation
    double ratio = H/this.height;
    xy1[0] = ratio * xy[0];
    xy1[1] = ratio * xy[1];
    
    return xy1;
    
  }

  private double[] scaleXY(float x, float y){
 	  float[] xy = new float[2];
	   double[] ret = new double[2];
	  
	   double W=0.0;
	   double H=0.0;
	    
	   if (this.size == "A3") {
	     W=A3W; H=A3H;
	   } else if (this.size == "A4"){
	     W=A4W; H=A4H;
	   }
	  
	   this.transformMatrix.mult(new float[]{x,y}, xy);
	  
	   for (int i = 0 ; i < xy.length; i++) {
	     ret[i] = (double) xy[i];
	   }
	  
	   ret = scaleToPaper(ret);
	   ret[1] = H-ret[1];
	   
	   return ret;
  }

  private double[] scaleWH(double w, double h){
   	double[] wh = {w,h};
   	wh = scaleToPaper(wh);
   	return wh;
  }
  
  /**
   * 
   * @return chord angle in degrees
   */
  private double getChordAngle() {
	  return chordangle;
  }
  
  /**
   * This method sets the chord angle (in degrees) used for drawing arcs, circles and ellipses.
   * 
   * @example arcs
   * @param ca: chord angle (degrees)
   */
  public void setChordAngle(float ca) {
	   chordangle=ca;
  }
  
  // END UTILITIES
  
  // DRAWING METHODS
  
  public void point(float x, float y) {
    
    double[] xy = new double[2];
    
    xy = scaleXY(x, y); // get the transformed/scaled points
      
    writer.println("PU" + xy[0] + "," + xy[1] + ";");
    writer.println("PD;");
    writer.println("PU;");  
    
  }
  /**
   * This method draws a line
   * 
   * @example simple_demo
   * @param x1, y1, x2, y2: start and end coordinates of line
   */
  public void line(float x1, float y1, float x2, float y2) {

    double[] x1y1 = new double[2];
    double[] x2y2 = new double[2];
      
    x1y1 = scaleXY(x1, y1); // get the transformed/scaled points
    x2y2 = scaleXY(x2, y2); // get the transformed/scaled points
      
    writer.println("PU" + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("PD" + x2y2[0] + "," + x2y2[1] + ";");
    writer.println("PU;");
  }
  
  /**
   * This method implements the ellipse() method
   * 
   * @example ellipse
   * @param x, y, w, h: center-coordinates, width and height of ellipse.
   */
  public void ellipseImpl(float x, float y, float w, float h) {
    
    double[] xy = new double[2];
    double[] initxy = new double[2];
    double[] wh = new double[2];
    double   ca = getChordAngle();
	
    xy = scaleXY(x, y); // get the transformed/scaled points
    wh = scaleWH(w, h); // scaled width and height
    
    if (Math.abs(w-h) < 0.1) {
    	
      // draw a circle
      writer.println("PU" + xy[0] + "," + xy[1] + ";");  
      writer.println("CI" + wh[0]/2.0 + "," + ca + ";");
      writer.println("PU;");
      
    } else {
    	
      // draw an ellipse
      double initx = x + w/2.0 * Math.cos(0.0);
      double inity = y + h/2.0 * Math.sin(0.0);
      initxy = scaleXY((float)initx, (float)inity);
     
      double _x, _y;
      
      writer.println("PU" + initxy[0] + "," + initxy[1] + ";");
      
      for (double t=ca; t<360.0; t+=ca) {

    	_x = x + w/2.0 * Math.cos(Math.toRadians(t));
        _y = y + h/2.0 * Math.sin(Math.toRadians(t));
    	
    	if (Math.abs(_x) < 0.01) _x=0.01;
    	if (Math.abs(_y) < 0.01) _y=0.01;
    	   
    	xy = scaleXY((float)_x, (float)_y);
        
        writer.println("PD" + xy[0] + "," + xy[1] + ";");
        
      }
      
      writer.println("PD" + initxy[0] + "," + initxy[1] + ";");
      writer.println("PU;");
    }
  }
  
  // arcs
  
  public void arc(float x, float y, float w, float h, float start, float stop) {
   	arc(x,y,w,h,start,stop,OPEN);
  }
  
  public void arc(float x, float y, float w, float h, float start, float stop, int mode) {
	
	  double[] xy   = new double[2];
	  double[] x1y1 = new double[2];
	  double[] x2y2 = new double[2];
	  double   x1, y1, x2, y2;
    
	  x1 = x + w/2 * Math.cos(start);
	  y1 = y + w/2 * Math.sin(start);
    
	  x2 = x + w/2 * Math.cos(stop);
	  y2 = y + w/2 * Math.sin(stop);
    
	  xy = scaleXY(x, y); // get the transformations:
	  x1y1 = scaleXY((float)x1, (float) y1);
	  x2y2 = scaleXY((float)x2, (float) y2);
    
	  // convert radians to degrees, swap clockwise to anti-clockwise
	  double startd = 360 - start*180.0/PI;
	  double stopd  = 360 - stop*180.0/PI;
    
	  if (Math.abs(w-h) < 0.1) {

		  // draw the arc
		  writer.println("SP1;");
		  writer.println("PU" + x1y1[0] + "," + x1y1[1] + ";");
		  writer.println("PD;AA"+xy[0]+","+xy[1]+","+(stopd-startd)+","+getChordAngle()+";");
      
		  if (mode == CHORD) {
			  writer.println("PD" + x1y1[0] + "," + x1y1[1] + ";");
		  }
      
		  if (mode == PIE){
			  writer.println("PD" + xy[0] + "," + xy[1] + ";");
			  writer.println("PD" + x1y1[0] + "," + x1y1[1] + ";");
		  }

		  writer.println("PU;");
	  }
    
  }
  
  public void rectImpl(float x1, float y1, float x2, float y2) {
    // x2,y2 are opposite corner points, not width and height
    // see PGraphics, line 2578 
      
    double[] x1y1 = new double[2];
   	double[] x2y1 = new double[2];
   	double[] x2y2 = new double[2];
   	double[] x1y2 = new double[2];

    // get the transformed/scaled points    
    x1y1 = scaleXY(x1,y1);
    x2y1 = scaleXY(x2,y1);
    x1y2 = scaleXY(x1,y2);
    x2y2 = scaleXY(x2,y2);
    
    writer.println("PU" + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("PD" + x2y1[0] + "," + x2y1[1] +
                    "," + x2y2[0] + "," + x2y2[1] +
                    "," + x1y2[0] + "," + x1y2[1] +
                    "," + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("PU;");
           
  }

  /**
   * This method puts text in the HPGL output.
   * 
   * 
   * @example text
   * @param s String : text to write
   * @param x float : x location (pixels)
   * @param y float : y location (pixels)
   */
  public void text(String s, float x, float y) {
    double[] x1y1 = new double[2];
    x1y1 = scaleXY(x,y);
    
    writer.println("PU" + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("DT" + terminator + ";");
    writer.println("LB" + s + terminator + ";");
  }
  
  /**
   * This method sets text size in the HPGL output.
   * 
   * Note: this doesn't work very well for different font sizes.
   * You'll need to experiment to get things accurate.
   * 
   * @example text
   * @param sizepx float : size of the text in pixels
   */
  public void textSize(float sizepx){
    
    double sizecm_w=0.19;  // default 
    double sizecm_h=0.27;
    double paperWidth=1.0;
    double paperHeight=1.0;
    
    if (this.size == "A3") {
      //sizecm_w=0.29;
      //sizecm_h=0.38;
      paperWidth=A3Wmm;
      paperHeight=A3Hmm;
    } else if (this.size == "A4"){
      //sizecm_w=0.19;
      //sizecm_h=0.27;
      paperWidth=A4Wmm;
      paperHeight=A4Hmm;
    }
    
    //sizecm_w = (sizepx*paperWidth)/this.width/10;
    //sizecm_h = sizecm_w*(0.27/0.19);
    
    sizecm_h = (sizepx*paperHeight)/this.height/10/2;
    sizecm_w = sizecm_h*(0.19/0.27);
    
    // get the size of the text in pixels, convert to cm based on selected paper size;
    writer.println("SI" + sizecm_w + "," + sizecm_h + ";");
  }
  
  // MATRIX STACK - from GraphicsHPGL.java, gsn/src

  public void pushMatrix() {
    if (transformCount == transformStack.length) {   
      throw new RuntimeException("pushMatrix() overflow");
    }
      
    transformStack[transformCount] = this.transformMatrix.get();
    transformCount++;
  }

  public void popMatrix() {
	   //System.out.println("popMatrix");

    if (transformCount == 0) {   
      throw new RuntimeException("HPGL: matrix stack underrun");
    }
    transformCount--;
    this.transformMatrix = new PMatrix2D();
    for (int i = 0; i <= transformCount; i++) {   
      this.transformMatrix.apply(transformStack[i]);
    }
  }

  public void translate(float x, float y) {
    // PenUp, move by x, y.
    //this.modelView.print();
    this.transformMatrix.translate(x, y);
    //this.modelView.print();
  }
  
  public void rotate(float angle) { 
    this.transformMatrix.rotate(angle);
  }
  
  public void dispose() {
    writer.flush();
    writer.close();
    writer = null;
  }
  
  // WRITER METHODS

  /**
   * Write a command on one line (as a String), then start a new line
   * and write out a formatted float. Available for anyone who wants to
   * insert additional commands into the HPGL stream.
   * @param cmd HPGL command
   * @param val HPGL parameter
   */
  public void write(String cmd, float val) {
    writer.println(cmd);
    // Don't number format, will cause trouble on systems that aren't en-US
    // http://dev.processing.org/bugs/show_bug.cgi?id=495
    writer.println(val);
  }

  /**
   * Write a line to the HPGL file. Available for anyone who wants to
   * insert additional commands into the HPGL stream.
   * @param what String to write
   */
  public void println(String what) {
    writer.println(what);
  }
  
  private void writeHeader() {
	writer.println("IN;SP1;");
  }
  
  private void writeFooter() {
    writer.println("PA0,0;SP;");
  }

  // GENERAL METHODS

  public boolean displayable() { return false; }
  public boolean is2D() { return true; }
  public boolean is3D() { return true; }
  public void resetMatrix(){ }
  public void blendMode(int mode){ }
  
}


