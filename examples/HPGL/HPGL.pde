import hpglgraphics.*;

void setup() {
  
  // HPGL (Roland DXY-1350A) coordinate ranges:
  // A4 : 11040 x  7721 (297mm x 210mm)
  // A3 : 16158 x 11040 (420mm x 297mm)
  
  size(1104, 772, P2D);
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A3");
  hpgl.setPath(sketchPath("output.hpgl"));
  
  background(255);
  stroke(0);
  noFill();
  
  beginRecord(hpgl);
  rect(0,0,width-1,height-1);

  pushMatrix();
    translate(200,100);
    rect(0, 0, 180, 180);
    rotate(radians(30));
    rect(-30, -30, 96, 96);
  popMatrix();
  
  line(50, 50, width-50, height-50);

  ellipse(width/2, 2*height/3, 175, 175);

  endRecord();
  
}

void draw() {

}

