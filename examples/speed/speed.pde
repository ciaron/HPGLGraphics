import hpglgraphics.*;

int lines = 30;

void setup() {

  size(1104, 772);

  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);

  hpgl.setPaperSize("A4");
  hpgl.setPath("output.hpgl");

  background(255);
  stroke(0);
  fill(0);

  beginRecord(hpgl);
  
  // draw lines at incremental speed
  for(int i = 0; i < lines; i++) {
     hpgl.setSpeed(i);
     int y = height / lines * (i + 1);
     line(0, y, width, y); 
  }

  endRecord();

}

void draw() {
}