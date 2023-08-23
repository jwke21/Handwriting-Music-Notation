package hmn.reaction;

import hmn.I;
import hmn.UC;
import java.util.ArrayList;
import java.util.HashMap;

// Will do bookkeeping for everything that implements I.React
public abstract class Reaction implements I.React {

  private static Map byShape = new Map(); // The marketplace
  public static List initialReactions = new List(); // Used by undo to restart
  public Shape shape;

  public Reaction(String shapeName) {
    shape = Shape.DB.get(shapeName);
    if (shape == null) { System.out.println("WTF? ShapeDB does not contain " + shapeName); }
  }

  public void enable() {
    List list = byShape.getList(shape);
    if (!list.contains(this)) { list.add(this); }
  }

  public void disable() {
    List list = byShape.getList(shape);
    list.remove(this);
  }

  public static Reaction best(Gesture g) { // Can return null
    return byShape.getList(g.shape).loBid(g);
  }

  public static void nuke() { // Used for undo
    byShape.clear();
    initialReactions.enable();
  }

  // -------------------- List --------------------
  public static class List extends ArrayList<Reaction> {

    public void addReaction(Reaction r) { add(r); r.enable(); }

    public void removeReaction(Reaction r) { remove(r); r.disable(); }

    public void clearAll() {
      for (Reaction r : this) { r.disable(); }
      this.clear();
    }

    public Reaction loBid(Gesture g) { // Can return null
      Reaction res = null;
      int bestSoFar = UC.noBid;
      for (Reaction r : this) {
        int b = r.bid(g);
        if (b < bestSoFar) { bestSoFar = b; res = r; }
      }
      return res;
    }

    public void enable() { for (Reaction r : this) { r.enable(); } }
  }

  // -------------------- Map --------------------
  public static class Map extends HashMap<Shape,List> {

    // Forces return Reaction.List (if no matching List will return empty List)
    public List getList(Shape s) {
      List res = get(s);
      if (res == null) { res = new List(); put(s, res); }
      return res;
    }
  }
}
