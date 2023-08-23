package hmn.reaction;

import hmn.I;
import hmn.graphicsLib.G;
import java.util.ArrayList;

// When user draws something on the screen
public class Gesture {

  public static List UNDO = new List();
  public Shape shape; // Shape that was recognized
  public G.VS vs;

  private Gesture (Shape shape, G.VS vs) { this.shape = shape; this.vs = vs; }

  public static Gesture getNew(Ink ink) { // Can return null
    Shape s = Shape.recognize(ink);
    return (s == null) ? null : new Gesture(s, ink.vs);
  }

  public void doGesture() {
    Reaction r = Reaction.best(this);
    if (r != null) { UNDO.add(this); r.act(this); }
  }

  public void redoGesture() {
    Reaction r = Reaction.best(this);
    if (r != null) { r.act(this); }
  }

  public static void undo() {
    if (UNDO.isEmpty()) { return; }
    UNDO.remove(UNDO.size() - 1); // Remove last elem
    Layer.nuke(); // Eliminate all Masses
    Reaction.nuke(); // Clear the byShape map and reload initial reactions
    UNDO.redo();
  }

  // Anonymous class
  public static I.Area AREA = new I.Area() {
    // Gesture area fills whole screen
    @Override
    public boolean hit(int x, int y) { return true; }

    @Override
    public void dn(int x, int y) { Ink.BUFFER.dn(x, y); }

    @Override
    public void drag(int x, int y) { Ink.BUFFER.drag(x, y); }

    @Override
    public void up(int x, int y) {
      Ink.BUFFER.add(x, y);
      Ink ink = new Ink();
      Gesture gest = Gesture.getNew(ink); // Can fail if unrecognized
      Ink.BUFFER.clear();
      if (gest != null) {
        if (gest.shape.name.equals("N-N")) { // N-N gesture reserved for undo
          undo();
        } else {
          gest.doGesture();
        }
      }

//      if (gest != null) {
//        Reaction r = Reaction.best(gest); // Can fail if no bid
//        if (r != null) {
//          r.act(gest);
//        }
//      }
    }
  };

  // -------------------- List --------------------
  public static class List extends ArrayList<Gesture> {

    public void redo() { for (Gesture g : this) { g.redoGesture(); } }
  }
}
