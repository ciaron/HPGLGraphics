import java.util.Calendar;
import hpglgraphics.*;

int fg=0;
int bg=255;

//boolean DEBUG=    false; // print debug information
//boolean FADE=     false; // blank each frame, (opposite is fade);
//int fadelevel=10;       // how much to fade (0: no fade (overlay), 10,: mild fade, 255: full fade, same as blanking)
float x,y, px,py, fx, fy;

int r;
int theta;
boolean curvy=true;

int count = 0;
int opacity=16;
int n=36; //360, 720, 1080, 1440
int laps=1;
int gap=25;
int randomness=10;
int nstrokes=1;

int[] rd = new int[n];

float xc1 = 0;
float yc1 = 0;
float xc2 = 0;
float yc2 = 0;
float xc3 = 0;
float yc3 = 0;

HPGLGraphics hpgl;

void setup () {
  size(1000, 1000);
  background(bg);  
  
  hpgl = (HPGLGraphics) createGraphics(width, height, HPGLGraphics.HPGL);
  hpgl.setPaperSize("A4");
  hpgl.setPath("wobby.hpgl");
  
} //<>//

void draw () {
  
  // outside-in
  r = int(0.18*width/3);
  // inside-out
  //r = int(0.98*width/8);
  
  // initialise randomness
  for (int i=0; i<n; i++) {
    // TODO try noise() instead of random()
    rd[i] = int(random(-width/50, width/50));
    //rd[i] = int(randomGaussian()*50);  
  }
  
  background(bg);
  
  beginRecord(hpgl);
  
  stroke(fg);
  strokeWeight(1);
  pushMatrix();
  translate(width/2, height/2);
  
  noFill();
  
  for (int c=0; c<laps; c++) {

    for (int m=1; m<=nstrokes; m++) {
      count = 0;
      beginShape();
    
      for (theta=0; theta<n; theta+=1) {
     
        int R1 = r + int(random(0, width/map(c,0,laps, randomness/1, randomness/1)));

        x = R1 * cos(radians(theta/(n/360.0)));
        y = R1 * sin(radians(theta/(n/360.0)));
      
        if (curvy) {
          curveVertex(x,y);
        } else {
          vertex(x,y);
          fx=x; fy=y;
        }

        px=x; py=y;
      
        if (curvy) {
          if (count == 0) {
            xc1 = x;
            yc1 = y;
          } else if (count == 1) {
            xc2 = x;
            yc2 = y;
          } else if (count == 2) {
            xc3 = x;
            yc3 = y;
          }
          count++;
        }
      }

      if (curvy) {
        // need the extra vertices cos 4 are needed to close the loop properly
        curveVertex(xc1, yc1);
        curveVertex(xc2, yc2);
        curveVertex(xc3, yc3);
      } else {
        //line(fx,fy,x,y);
        vertex(fx,fy);
      }
      
      endShape(CLOSE);

    }
    
    r = int(r*0.97);  
  } 
  
  popMatrix();
  endRecord();
  
  noLoop();

}

void keyPressed() {
  //if (DEBUG) {
  //  println("KEYPRESS: keyCode: " + keyCode + " key: "+key);
  //}
  
  if (keyCode == 45) { //'-'
  }
  
  if (keyCode == 61) { //'='
  } 
  
  if (key == CODED) {
    if (keyCode == UP) {

    } else if (keyCode == DOWN) {

    } else if (keyCode == LEFT) {

    } else if (keyCode == RIGHT) {

    }
  }
  
  //loop();
}

void keyReleased() {  
  if (key == 's' || key == 'S') {
    // SAVE PNG
    //saveFrame(timestamp()+"_####.png");
    
    save(timestamp()+"_"+randomness+".png");
  }
  
  if (key == 'r' || key == 'R')  {
    // RESET
    background(bg);
  }
  
  if (key == 'c' || key == 'C')  {
    // CLEAR
    background(bg);
  }
}

void mouseReleased() {
  //randomness += 25;
  loop();
}

// timestamp
String timestamp() {
  Calendar now = Calendar.getInstance();
  return String.format("%1$ty%1$tm%1$td_%1$tH%1$tM%1$tS", now);
}