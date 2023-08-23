package hmn.sandbox;

import hmn.UC;
import hmn.graphicsLib.G;
import hmn.graphicsLib.Window;
import hmn.reaction.Gesture;
import hmn.reaction.Ink;
import hmn.reaction.Layer;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class ReactionTest extends Window {

  static { new Layer("BACK"); new Layer("FORE"); }

  public ReactionTest() {
    super("Simple Reaction Test", 1000, 700);

    Reaction.initialReactions.addReaction(new Reaction("SW-SW") {
      public int bid(Gesture g) { return 0; }
      public void act(Gesture g) { new Box(g.vs); }
    });
  }

  public void paintComponent(Graphics g) {
    G.clearBack(g);
    g.setColor(Color.BLUE);
    Ink.BUFFER.show(g);
    Layer.ALL.show(g);
  }

  public void mousePressed(MouseEvent me) { Gesture.AREA.dn(me.getX(), me.getY()); repaint(); }

  public void mouseDragged(MouseEvent me) { Gesture.AREA.drag(me.getX(), me.getY()); repaint(); }

  public void mouseReleased(MouseEvent me) { Gesture.AREA.up(me.getX(), me.getY()); repaint(); }

  public static void main(String[] args) { (PANEL = new ReactionTest()).launch(); }

  // -------------------- Box --------------------
  public static class Box extends Mass {

    public G.VS vs;
    public Color c = G.rndColor();

    public Box(G.VS vs) {
      super("BACK");
      this.vs = vs;

      addReaction(new Reaction("S-S") { // Delete box
        public int bid(Gesture g) {
          int x = g.vs.xM(), y = g.vs.yL();
          if (Box.this.vs.hit(x, y)) {
            return Math.abs(x - Box.this.vs.xM());
          }
          return UC.noBid;
        }
        public void act(Gesture g) { Box.this.deleteMass(); }
      });

      addReaction(new Reaction("DOT") { // Change color
        public int bid(Gesture g) {
          int x = g.vs.xM(), y = g.vs.yL();
          if (Box.this.vs.hit(x, y)) {
            return Math.abs(x - Box.this.vs.xM()) + Math.abs(y - Box.this.vs.yM());
          }
          return UC.noBid;
        }
        public void act(Gesture g) { Box.this.c = G.rndColor(); }
      });
    }

    public void show(Graphics g) { vs.fill(g, c); }
  }


}
