import hpglgraphics.*;

HPGLGraphics hpgl;

void setup() {

  //size(400, 400, P2D);
  
  // HPGL (Roland DXY-1350A) coordinate ranges:
  // A4 : 11040 x 7721 (297 x 210 mm)
  // A3 : 16158 x 11040 (420 x 297 mm)
  
  size(1104, 772);
  
  hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath("random_walk.hpgl");

}

void draw() {
  background(255);
  stroke(0);
  noFill();

  beginRecord(hpgl);

  // continuous lines are best drawn with shapes/vertices. Otherwise the pen up/down
  // for each line segment causes weird effect (v0.1 of HPGLGraphics)
  int x, y, px, py;
  
  x=10;
  y=height/2;
  px=x; py=y;
  hpgl.setCurveDetail(5);
  beginShape();

  for (x=20; x<width-10; x+=10) {
    y = y + (int)random(-50,50);
    curveVertex(x, y);
    px=x;
    py=y;
  }
  
  endShape();
  
  endRecord();
  noLoop();
}

void mouseReleased() {
  loop();
}