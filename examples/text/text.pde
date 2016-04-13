import hpglgraphics.*;

void setup() {

  size(1104, 772);
  
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  
  hpgl.setPaperSize("A4");
  hpgl.setPath("output.hpgl");
  
  background(255);
  stroke(0);
  fill(0);
  
  int off=200;
  String s1 = "First text";
  String s2 = "Second text";
  String s3 = "Third text";
  
  beginRecord(hpgl);
    
  pushMatrix();
  
   translate(100, height/2-off);
   textSize(16);
   text(s1, 0, height/4);
   
   translate(textWidth(s1)+100, 0);
   textSize(20);
   text(s2, 0, height/4);
 
   translate(textWidth(s2)+200, 0);
   textSize(24);
   text(s3, 0, height/4);
 
  popMatrix();
    
  endRecord();
  
}

void draw() {
}