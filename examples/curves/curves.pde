import hpglgraphics.*;

void setup(){
  size(1200, 900);
  background(255);
  stroke(0);
  noFill();
  
  HPGLGraphics hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath("curves.hpgl");
  
  beginRecord(hpgl);
  
  strokeWeight(1);
  rect(10, 10, width-20, height-20);
  //rect(10, 10, 200, 00);
  /*beginShape();
  stroke(255,0,255); //MAGENTA
  curveVertex(50, 50);
  curveVertex(50, 50);
  curveVertex(400, 200);
  curveVertex(500, 400);
  curveVertex(700, 700);
  endShape();
  
  beginShape();
  
   stroke(255,0,0); //RED

   vertex( 50,   50);
   bezierVertex(200, 600, 400, 200, 500, 400);
    
  endShape();
  
  //BEZIER VERTEX
  beginShape();
   stroke(0); //BLACK
   vertex(520, 400);
   bezierVertex(700, 348, 532, 459, 589, 92);
  endShape();
  
  // CURVE VERTEX
  pushMatrix();
  translate(300, 0);
  beginShape();
    stroke(0,255,0); //BLACK
    curveVertex(200, 800);
    curveVertex(390, 270);
    curveVertex(532, 376);
    curveVertex(750, 100);   
    curveVertex(0,0);
  endShape();
  popMatrix();

  beginShape();
   vertex(230, 220);
   bezierVertex(280, 200, 280, 275, 230, 275);
   bezierVertex(250, 280, 260, 225, 230, 220);
  endShape();
  
*/
  
  endRecord();
  
}