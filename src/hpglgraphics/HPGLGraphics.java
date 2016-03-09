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
  //private boolean resize = false;
  private String size;  // paper size, A3 or A4 for now
  private int MATRIX_STACK_DEPTH = 32;
  private int transformCount = 0;
  private PMatrix2D transformStack[] = new PMatrix2D[MATRIX_STACK_DEPTH];
  
  private int A4W = 11040;
  private int A4H =  7721;
  private int A3W = 16158;
  private int A3H = 11040;
  
  private float[][] shapeVertices;
	
  public final static String VERSION = "##library.prettyVersion##";

  /**
   * a Constructor, usually called in the setup() method in your sketch to
   * initialize and start the Library.
   * 
   * @example HPGL
   * 
   */

  public HPGLGraphics(){
    
    welcome();
    
    if (!matricesAllocated) {   
      //System.out.println("HPGL: Allocating and setting up default matrices");
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
   * @example HPGL
   * @param size String: "A3" or "A4", depending on the intended plot size
   */
  
  public void setPaperSize(String size) {
    this.size=size;
  }
  
  /**
   * This method sets the path and filename for the HPGL output.
   * Must be called from the Processing sketch
   * 
   * @example HPGL
   * @param path String: name of file to save to
   */
  
  public void setPath(String path) {

    this.path = path;
   	if (path != null) {
	     file = new File(path);
   	  if (!file.isAbsolute()) file = null;
   	}
	   if (file == null) {
	     throw new RuntimeException("HPGL export requires an absolute path " +
	                                "for the location of the output file.");
   	}
  }

  public void selectPen(int pen) {
    writer.println("SP"+pen+";");    
  }
  
  public void beginDraw() {
	
   	if (this.path == null) {
   	  throw new RuntimeException("call setPath() before recording begins!");
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
 
    //pushMatrix();
  }

  public void endDraw(String path) {

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
  
  public void beginShape() {
	    //System.out.println("got a shape");
	    
	    shapeVertices = new float[DEFAULT_VERTICES][VERTEX_FIELD_COUNT];
  }
	  
  public void beginShape(int kind) {
    //System.out.println("got a shape: " + kind);
    shapeVertices = new float[DEFAULT_VERTICES][VERTEX_FIELD_COUNT];
    //System.out.println(vertices.length);
    shape = kind;
    vertexCount = 0;
  }
  
  public void endShape() {
    endShape(OPEN);
  }
  
  public void endShape(int mode){
    //System.out.println("endShape.");
    
    int stop = vertexCount - 1;
    
    writer.println("PU" + shapeVertices[0][0] + "," + shapeVertices[0][1] + ";");
    
    for (int i=1; i<=stop; i++){
      writer.println("PD" + shapeVertices[i][0] + "," + shapeVertices[i][1] + ";");
    }
    
    if (mode==CLOSE){
      writer.println("PD" + shapeVertices[0][0] + "," + shapeVertices[0][1] + ";");
    }
    
    writer.println("PU;");
    
    vertexCount = 0;
    shapeVertices = null;
  }
  
  /*public void endShape(int mode) {
    System.out.println("endShape, mode: " + mode);
    
   	switch(shape) {
      case TRIANGLE:
        {
          System.out.println("TRIANGLE " + vertexCount);
          for (int i=0; i<vertexCount; i++) {
        	   System.out.println(vertices[i][0]);
        	   System.out.println(vertices[i][1]);
          }
        }
        break;
        
      case QUAD:
	       {
	         System.out.println("QUAD " + vertexCount);
	       }
	       break;
	       
   	} // switch
  }
 */ 
  /*public void shape(PShape s){
    System.out.println("got a shape(): " + s);
  }
  */
  public void vertex(float x, float y) {
	   // Store vertices here? TODO See MeshExport.java
    vertexCount++;
    //System.out.println(vertices.length + " " + vertexCount);
    
    // check if shapeVertices is big enough, extend if necessary.
    // via OBJExport (MeshExport.java)
    if(vertexCount >= shapeVertices.length) {
      float newVertices[][] = new float[shapeVertices.length*2][VERTEX_FIELD_COUNT];
      System.arraycopy(shapeVertices,0,newVertices,0,shapeVertices.length);
      shapeVertices = newVertices;
    }
    float[] xy = new float[2];
          
    // get the transformed/scaled points
    xy = getNewXY(x, y);
    shapeVertices[vertexCount-1][0] = xy[0];
    shapeVertices[vertexCount-1][1] = xy[1];
    //System.out.println("X: " + xy[0] + " Y: " + xy[1]);
  }
  
  // UTILITIES
  
  /**
   * This method return x,y coordinates converted to plotter coordinates
   * It also flip the y-coordinate to match Processing axis orientation.
   * 
   * @example HPGL
   * @param float[] xy: A 2-array with the x and y parameters
   */
  private float[] scaleToPaper(float[] xy) {
    
    float[] xy1 =  new float[xy.length];
    
    float W=(float) 0.0;
    float H=(float) 0.0;
    
    if (this.size == "A3") {
      W=A3W; H=A3H;
    } else if (this.size == "A4"){
      W=A4W; H=A4H;
    }
    
    // scale x-coordinate
    xy1[0] = this.map(xy[0], 0, this.width, 0, W);
    
    // scale and flip y-coord (origin is reversed vis-a-vis Processing canvas)
    xy1[1] = H-this.map(xy[1], 0, this.height, 0, H);
    
    return xy1;
    
  }
  
  private float map(float val, float minval, float maxval, float minrange, float maxrange) {
    return (maxrange-minrange)*val / (maxval-minval);
  }
  
  private float[] getNewXY(float x, float y){
	  float[] xy = new float[2];
	  this.transformMatrix.mult(new float[]{x,y}, xy);
	  xy = scaleToPaper(xy);
	  return xy;
  }
  
  private float[] getNewWH(float w, float h){
	   float[] wh = {w,h};
	   wh = scaleToPaper(wh);
	   return wh;
  }
  
  // END UTILITIES
  
  // DRAWING METHODS
  
  public void line(float x1, float y1, float x2, float y2) {

	   float[] x1y1 = new float[2];
    float[] x2y2 = new float[2];
      
    // get the transformed/scaled points
    x1y1 = getNewXY(x1, y1);
    x2y2 = getNewXY(x2, y2);
      
    writer.println("PU" + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("PD" + x2y2[0] + "," + x2y2[1] + ";");
    writer.println("PU;");
	  
  }
  
  public void ellipse(float x, float y, float w, float h) {
    
    float[] xy = new float[2];
    float[] wh = new float[2];
    
    // get the transformed/scaled points
    xy = getNewXY(x, y);
    // get scaled width and height
    wh = getNewWH(w, h);
    
    if (Math.abs(w-h) < 0.1) {
      // draw a circle, need to figure out ellipses later (TODO)
      writer.println("PU" + xy[0] + "," + xy[1] + ";");
      writer.println("CI"+wh[0]/2.0+";");
      writer.println("PU;");
    }
  }
  
  public void rectImpl(float x1, float y1, float x2, float y2) {
    // x2,y2 are opposite corner points, not width and height
    // see PGraphics, line 2578 
      
    float[] x1y1 = new float[2];
    float[] x2y1 = new float[2];
    float[] x2y2 = new float[2];
    float[] x1y2 = new float[2];

    // get the transformed/scaled points    
    x1y1 = getNewXY(x1,y1);
    x2y1 = getNewXY(x2,y1);
    x1y2 = getNewXY(x1,y2);
    x2y2 = getNewXY(x2,y2);
    
    writer.println("PU" + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("PD" + x2y1[0] + "," + x2y1[1] +
                    "," + x2y2[0] + "," + x2y2[1] +
                    "," + x1y2[0] + "," + x1y2[1] +
                    "," + x1y1[0] + "," + x1y1[1] + ";");
    writer.println("PU;");
           
  }

  // / MATRIX STACK - from GraphicsHPGL.java, gsn/src

  public void pushMatrix() {
	   //System.out.println("pushMatrix");
	  
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
	   //System.out.println("translate");
	   // PenUp, move by x, y.
	   //this.modelView.print();
    this.transformMatrix.translate(x, y);
	   //this.modelView.print();
  }
  
  public void rotate(float angle) { 
    //System.out.println("rotate");
    //this.modelView.print();
    this.transformMatrix.rotate(angle);
    //this.modelView.print();
  }
  
  public void dispose() {
    //System.out.println("file is: " + this.path);
    //System.out.println("size is: " + this.size);
    //writeFooter();

    writer.flush();
    writer.close();
    writer = null;
  }

  // GENERAL METHODS

  public boolean displayable() {
    return false;
  }

  public boolean is2D() {
    return true;
  }

  public boolean is3D() {
	  return true;
  }
  
  public void resetMatrix(){
  }
  public void blendMode(int mode){
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
    writer.println("PU0,0;");
  }
  
  
}


