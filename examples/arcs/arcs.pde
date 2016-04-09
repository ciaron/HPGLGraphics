import hpglgraphics.*;

void setup() {

  // HPGL (Roland DXY-1350A) coordinate ranges:
  // A4 : 11040 x 7721 (297 x 210 mm)
  // A3 : 16158 x 11040 (420 x 297 mm)
  
  size(1104, 772);
  
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath("arcs.hpgl");
  
  background(255);
  stroke(0);
  noFill();

  beginRecord(hpgl);
  
  rect(10,10,width-20,height-20);

  pushMatrix();
  translate(200,200);
  point(0,0);
  arc(0, 0, 200, 200, radians(-90), radians(160), PIE);
  popMatrix();
  
  pushMatrix();
  translate(450, 200);
  
  arc(0, 0, 200, 200, radians(90), radians(160), PIE);
  popMatrix();
  
  pushMatrix();
  translate(700, 200);
  
  rotate(radians(90));
  arc(0, 0, 200, 200, radians(80), radians(280));
  popMatrix();
  
  pushMatrix();
  translate(950, 200);
  rotate(radians(45));
  point(0,0);
  
  hpgl.setChordAngle(6);
  arc(0, 0, 200, 200, radians(-30), radians(90), CHORD);
  arc(0, 0, 200, 200, radians(150), radians(270), CHORD);
  ellipse(0, 0, 220, 220);
  popMatrix();
   
  pushMatrix();
  translate(200, 575);
  arc(0, 0, 320, 320, radians(45), radians(350), PIE);
  rotate(radians(90));
  arc(0, 0, 240, 240, radians(35), radians(360), PIE);
  rotate(radians(90));
  arc(0, 0, 160, 160, radians(25), radians(370), PIE);
  popMatrix();
  
  pushMatrix();
  translate(550, 575);
  arc(0, 0, 320, 320, radians(85), radians(350), CHORD);
  popMatrix();
  
  pushMatrix();
  translate(900, 575);
  arc(0, 0, 320, 320, radians(345), radians(360), PIE);
  popMatrix();
  
  endRecord();

}

void draw() {
}