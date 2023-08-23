package hmn.sandbox;

import hmn.graphicsLib.Spline;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import hmn.I;
import hmn.UC;
import hmn.graphicsLib.G;
import hmn.graphicsLib.Window;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends Window implements ActionListener {
//  public static G.VS theVS = new G.VS(100, 100, 200, 300);
//  public static Color theColor = G.rndColor();
  public static Square.List theList = new Square.List();
  public static Square theSquare;
  public static boolean dragging = false; // flag for if a square is being dragged on the screen
  public static G.V mouseDelta = new G.V(0, 0); // preserves mouse location within dragged square
  public static Timer timer; // timer for redraws
  public static G.V pressedLoc = new G.V(0, 0); // location pressed at
  public static final int WIDTH = UC.MAIN_WINDOW_WIDTH, HEIGHT = UC.MAIN_WINDOW_HEIGHT;
  public static I.Area curArea;
  public static Square BACKGROUND = new Square(0, 0) {
    public void dn(int x, int y) { theSquare = new Square(x, y); theList.add(theSquare); }
    public void drag(int x, int y) { theSquare.resize(x, y); }
  };
  static { BACKGROUND.c = Color.WHITE; BACKGROUND.size.set(5000, 5000); theList.add(BACKGROUND); } // Static block (i.e. initialization code)

  public Squares() {
    super("squares", WIDTH, HEIGHT);
    timer = new Timer(30, this); // 30 ms means about 30fps
    // rule of thumb:
    // if update occurs within 1 sec = no problem,
    // 2 seconds = dissatisfied
    // 5 seconds = furious
    timer.setInitialDelay(5000);
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g) {
    G.clearBack(g);
    theList.draw(g);
    if (theList.size() > 3) {
      Spline.pSpline(g, theList.get(1).loc, theList.get(2).loc, theList.get(3).loc, 4);}
  }

  @Override
  public void mousePressed(MouseEvent me) {
    /*
    if (theVS.hit(me.getX(), me.getY())) {
      theColor = G.rndColor();
    }
    */
    int x = me.getX(), y = me.getY(); // capture me (x, y)
    curArea = theList.hit(x, y); // capture the clicked square in a variable
    curArea.dn(x, y);
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    int x = me.getX(), y = me.getY();
    curArea.drag(x, y);
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent me) {
    if (dragging) {
      // update velocity of the square
      theSquare.dv.set(me.getX() - pressedLoc.x, me.getY() - pressedLoc.y);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) { repaint(); }

  public static void main(String[] args) { (PANEL = new Squares()).launch(); }

  //--------------------------- Square ---------------------------
  public static class Square extends G.VS implements I.Draw, I.Area {
    public Color c = G.rndColor();
    public G.V dv = new G.V(0, 0); // delta velocity
//    public G.V dv = new G.V(G.rnd(20) - 10, G.rnd(20) - 10);

    public Square(int x, int y) { super(x, y, 100, 100); }

    // draw the square
    public void draw(Graphics g) {
      fill(g, c);  // update color
      moveAndBounce();
    }

    // enable rubber banding
    public void resize(int x, int y) { if (x > loc.x && y > loc.y) { size.set(x - loc.x, y - loc.y); } }

    // enable moving of already drawn boxes
    public void move(int x, int y) { loc.set(x, y); }

    // enable bouncing
    public void moveAndBounce() {
      loc.add(dv);
      if (xL() < 0 && dv.x < 0) { dv.x = -dv.x; }    // hit left side of screen
      if (xH() > WIDTH && dv.x > 0) { dv.x = -dv.x; } // hit right side of screen
      if (yL() < 0 && dv.y < 0) { dv.y = -dv.y; }    // hit top of screen
      if (yH() > HEIGHT && dv.y > 0) { dv.y = -dv.y; }  // hit bottom of screen
    }

    @Override
    public void dn(int x, int y) { mouseDelta.set(loc.x - x, loc.y - y); }

    @Override
    public void up(int x, int y) {}

    @Override
    public void drag(int x, int y) { loc.set(mouseDelta.x + x, mouseDelta.y + y); }

    //--------------------------- List ---------------------------
    public static class List extends ArrayList<Square> implements I.Draw {
      // draw the squares in the list
      public void draw(Graphics g) {
        for (Square s: this) { s.draw(g); }
      }

      public Square hit(int x, int y) {
        Square res = null;
        for (Square s: this) { if (s.hit(x, y)) { res = s; } }
        return res;
      }
    }
  }
}
