import hpglgraphics.*;

import processing.pdf.*;
import processing.svg.*;

boolean PAUSED=false;
int fg = 255;
int bg = 0;
int x=0;

PShape s;

//HPGLGraphics hpgl;

void setup() {

  size(400, 400, P2D);
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath(sketchPath("test1.hpgl"));
  
  beginRecord(hpgl);

  //createGraphics()
  //beginRecord(PDF, "rect.pdf");
  //beginRecord(SVG, "rect.svg");
  background(255);
  stroke(0);
  pushMatrix();
    translate(200,100);
    rect(0, 0, 180, 180);
  popMatrix();
  //line(10,10,width-10,height-10);

  //stroke(255,0,0);
  //pushMatrix();
  // translate(200, 200);
  // rect(0, 0, 100, 100);
  //popMatrix();

  //stroke(0);
  //pushMatrix();
  //  translate(200, 200);
  //  rotate(radians(30));
  //  rect(0, 0, 100, 100);
  //popMatrix();
  
  //stroke(0);
  //pushMatrix();
  //  translate(100, 100);
  //  //s = createShape();
  //  //s.beginShape();
  //  //s.vertex(30, 75);
  //  //s.vertex(40, 20);
  //  //s.vertex(50, 75);
  //  //s.vertex(60, 20);
  //  //s.vertex(70, 75);
  //  //s.vertex(80, 20);
  //  //s.vertex(90, 75);
  //  //s.endShape(CLOSE);
  //  //shape(s);
    
  //  beginShape(LINES);
  //  vertex(70, 75);
  //  vertex(80, 20);
  //  endShape(CLOSE);
    
  //popMatrix();
  println(sketchPath("test.hpgl"));
  //hpgl.endRecord(sketchPath("test1.hpgl"));
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