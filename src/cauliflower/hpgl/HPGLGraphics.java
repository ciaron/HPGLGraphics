package cauliflower.hpgl;

import java.io.*;
import processing.core.*;

/**
 * This is a library for writing HPGL (plotter) files using beginRecord()/endRecord()
 * Borrows heavily from https://github.com/gregersn/HPGL
 * 
 * (the tag example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 * @example HPGL
 */

public class HPGLGraphics extends PGraphics {

  File file;
  PrintWriter writer;
  
  /** Stores all the transformations */
  /* from HPGL by gsn */
  public PMatrix2D transformMatrix;

  private boolean matricesAllocated = false;
  private boolean resize = false;

  private int MATRIX_STACK_DEPTH = 32;
  private int transformCount = 0;
  private PMatrix2D transformStack[] = new PMatrix2D[MATRIX_STACK_DEPTH];
	
  public final static String VERSION = "##library.prettyVersion##";

  /**
   * a Constructor, usually called in the setup() method in your sketch to
   * initialize and start the Library.
   * 
   * @example HPGL
   * @param none
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

  public void dispose() {
    writeFooter();

    writer.flush();
    writer.close();
    writer = null;
  }


  public boolean displayable() {
    return false;
  }

  public boolean is2D() {
    return false;
  }

  public boolean is3D() {
	return true;
  }
  
  private void writeHeader() {
  }
  private void writeFooter() {
  }
  
  private void welcome() {
	   System.out.println("##library.name## ##library.prettyVersion## by ##author##");
  }
    
  public void beginDraw() {
	
    // have to create file object here, because the name isn't yet
    // available in allocate()
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

  public void endDraw() {

//	   for (int i=0; i<vertices.length; i++) {
// 	    for (int j=0; j<vertices[i].length; j++) {
//      	System.out.println("*" + vertices[i][j]);
//	     }
//   	}
	//popMatrix();

    writer.flush();
  }
  
  /**
   * Write a command on one line (as a String), then start a new line
   * and write out a formatted float. Available for anyone who wants to
   * insert additional commands into the HPGL stream.
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
   */
  public void println(String what) {
    writer.println(what);
  }
  
  public void line(float x1, float y1, float x2, float y2) {
    System.out.println("got a line: " + x1 + " " + y1 + " " + x2 + " " + y2);
  }
  
  public void ellipse(float x1, float y1, float w, float h) {
    System.out.println("got an ellipse: " + x1 + " " + y1 + " " + w + " " + h);
  }
  
  public void rectImpl(float x1, float y1, float x2, float y2) {
    // x2,y2 are opposite corner points, not width and height
    // see PGraphics, line 2578 
    System.out.println("got a rect: " + x1 + " " + y1 + " " + x2 + " " + y2);
    this.transformMatrix.print();
      
    float[] x1y1 = new float[2];
    float[] x2y1 = new float[2];
    float[] x2y2 = new float[2];
    float[] x1y2 = new float[2];
      
    this.transformMatrix.mult(new float[]{x1,y1}, x1y1);
    this.transformMatrix.mult(new float[]{x2,y1}, x2y1);
    this.transformMatrix.mult(new float[]{x2,y2}, x2y2);
    this.transformMatrix.mult(new float[]{x1,y2}, x1y2);
      
    System.out.println(x1y1[0] + " " + x1y1[1] + " " + x2y2[0] + " " + x2y2[1] );
           
  }
  public void beginShape() {
    System.out.println("got a shape");
  }
  
  public void beginShape(int kind) {
    System.out.println("got a shape: " + kind);

	   if (kind==LINE) {
	     System.out.println("LINE");
	   }
	   if (kind==RECT) {
      System.out.println("RECT");
	   }
  }
  
  public void endShape() {
    System.out.println("shape ended");
  }
  
  public void shape(PShape s){
    System.out.println("got a shape");
    
  }
  
  public void vertex(float x, float y) {
    System.out.println("got a vertex");
    //vertex(x,y,0);
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

  
}


