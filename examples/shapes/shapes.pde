import hpglgraphics.*;

PShape s;

void setup() {

  size(1104, 772);
  
  // HPGL (Roland DXY-1350A) coordinate ranges:
  // A4 : 11040 x 7721 (297 x 210 mm)
  // A3 : 16158 x 11040 (420 x 297 mm)
  
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  
  hpgl.setPaperSize("A3");
  
  hpgl.setPath("output.hpgl");
  
  background(255);
  stroke(0);
  
  noFill();

  beginRecord(hpgl);

  // a bounding box
  rect(0,0,width-1,height-1);

  // overlapping rects, one rotated
  pushMatrix();
    translate(200,100);
    rect(0, 0, 180, 180);
    rotate(radians(45));
    rect(-30, -30, 96, 96);
  popMatrix();
  
  // selectPen(n): Select a pen. hp2xx can display pen colours via "-c"
  // -c 11111111 = all black pens
  // 0=off, 1=black, 2=red, 3=green, 4=blue, 5=cyan,
  //        6=magenta, 7=yellow.
  // Default pen is pen 1.

  // a diagonal line
  //hpgl.selectPen(2);
  line(50, 50, width-50, height-50);

  // Circle and radial line
  //hpgl.selectPen(3);
  pushMatrix();
   translate(width/4, 3*height/4);
   rotate(radians(-45));
   ellipse(0,0,260, 260);
   line(0,0,130,0);
  popMatrix();
  
  // A PShape
  //hpgl.selectPen(6);
  pushMatrix();
  translate(500, 675);
  PShape s;
  s = createShape();
  s.beginShape();
  s.vertex(0,0);
  s.vertex(-50,-100);
  s.vertex(150,-100);
  s.vertex(100,0);
  s.endShape(CLOSE);
  shape(s);
  translate(100, -220);
  rotate(radians(180));
  //hpgl.selectPen(5);
  shape(s);
  popMatrix();

  //hpgl.selectPen(1);
  
  pushMatrix();
   translate(100, 100);
   
   // draw a triangle using CLOSE
   stroke(255,0,0);
   beginShape();
     vertex(570, 125);
     vertex(880, 270);
     vertex(780, 70);
   endShape(CLOSE);
    
   stroke(0,0,255);
   beginShape();
     vertex(360, -20);
     vertex(410, 75);
     vertex(480, 120);
     vertex(550, 20);
     vertex(680, 0);
   endShape();
    
   popMatrix();
    
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