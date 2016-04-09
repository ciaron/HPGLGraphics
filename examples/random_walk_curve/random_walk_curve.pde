import hpglgraphics.*;

PShape s;

void setup() {

  //size(400, 400, P2D);
  
  // HPGL (Roland DXY-1350A) coordinate ranges:
  // A4 : 11040 x 7721 (297 x 210 mm)
  // A3 : 16158 x 11040 (420 x 297 mm)
  
  size(1104, 772);
  
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath("random_walk.hpgl");
  
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
  
  //beginShape();
  
  // random walk
  //for (x=20; x<width-10; x+=10) {
  //  y = y + (int)random(-50,50);
  //  //line(px, py, x, y);
  //  curveVertex(x, y);
  //  px=x;
  //  py=y;
  //}
  
  // reference curve
    beginShape();
  vertex(0,0);
  vertex(700,100);
 // endShape();
  
 //   beginShape();
  curveVertex(184,  191);
  curveVertex(184,  191);
  curveVertex(168,  419);
  curveVertex(221,  417);
  curveVertex(232, 500);
  curveVertex(232, 500);
  //  endShape();

  //beginShape();
  vertex(100,100);
  vertex(190,190);
  
  endShape();
  
  endRecord();

}

void draw() {
}

void mouseReleased() {
}

import java.util.Calendar;

// timestamp
String timestamp() {
  Calendar now = Calendar.getInstance();
  return String.format("%1$ty%1$tm%1$td_%1$tH%1$tM%1$tS", now);
}

void keyReleased() {  
  if (key == 's' || key == 'S') {
    //saveFrame(timestamp()+"_####.png");
    save(timestamp()+".png");
  }
}