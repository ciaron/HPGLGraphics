import hpglgraphics.*;

void setup() {
  
  // HPGL (Roland DXY-1350A) coordinate ranges:
  // A4 : 11040 x 7721 (297 x 210 mm)
  // A3 : 16158 x 11040 (420 x 297 mm)
  
  size(800, 800);
  
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath("ellipse.hpgl");
  
  background(255);
  stroke(0);
  noFill();

  beginRecord(hpgl);
  
  rect(10,10,width-20,height-20);

  pushMatrix();
  translate(width/2, height/2);
  
  for (int i=0; i<6; i++) {
    ellipse(0, -height/4, width/5, height/3);
    rotate(radians(60));
  }
  
  popMatrix();

  endRecord();

}

void draw() {
}