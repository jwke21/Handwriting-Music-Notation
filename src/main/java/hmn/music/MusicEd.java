package hmn.music;

import hmn.UC;
import hmn.graphicsLib.G;
import hmn.graphicsLib.Window;
import hmn.reaction.Gesture;
import hmn.reaction.Ink;
import hmn.reaction.Layer;
import hmn.reaction.Reaction;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class MusicEd extends Window {

  public static Page PAGE; // Will be created by a gesture

  static { Layer.createAll("BACK NOTE FORE".split(" ")); }

  public MusicEd() {
    super("Music Editor", UC.MAIN_WINDOW_WIDTH, UC.MAIN_WINDOW_HEIGHT);

    Reaction.initialReactions.addReaction(new Reaction("E-E") {
      public int bid(Gesture g) { return 10; } // Nothing else in system so bid can be anything
      public void act(Gesture g) {
        int y = g.vs.yM();
        Sys.Fmt sysFmt = new Sys.Fmt();
        PAGE = new Page(sysFmt);
        PAGE.margin.top = y;
        PAGE.addNewSys();
        PAGE.addNewStaff(0);
        this.disable(); // Take out of marketplace
      }
    });
  }

//  public static int[] xPoly = {100, 200, 200, 100}; // Coordinates for beam polygon
//  public static int[] yPoly = {50, 70, 80, 60};
//  public static Polygon poly = new Polygon(xPoly, yPoly, 4); // Creates deep copy of xPoly and yPoly

  public void paintComponent(Graphics g) {
    G.clearBack(g);
    g.setColor(Color.GREEN);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
    if (PAGE != null) {
      Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margin.top + 4*8);
//      int H = 32;
//      Glyph.HEAD_Q.showAt(g, H, 200, PAGE.margin.top + 4*H);
//      g.setColor(Color.RED);
//      g.drawRect(200, PAGE.margin.top + 3*H, 24*H/10, 2*H);
    }
    // Draw polygon
//    g.setColor(Color.BLACK);
//    Beam.setPoly(100, 100 + G.rnd(100), 200, 200 + G.rnd(200), 8);
//    g.fillPolygon(Beam.poly);
//    int h = 8, x1 = 100, x2 = 200;
//    Beam.setMasterBeam(x1, 100 + G.rnd(100), x2, G.rnd(100));
//    Beam.drawBeamStack(g, 0, 1, x1, x2, -h);
//    g.setColor(Color.ORANGE);
//    Beam.drawBeamStack(g, 1, 3, x1 + 10, x2 - 10, -h);
  }

  public void mousePressed(MouseEvent me) {
    Gesture.AREA.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    Gesture.AREA.drag(me.getX(), me.getY());
    repaint();
  }

  public void mouseReleased(MouseEvent me) {
    Gesture.AREA.up(me.getX(), me.getY());
    repaint();
  }

  public static void main(String[] args) { (PANEL = new MusicEd()).launch(); }
}
