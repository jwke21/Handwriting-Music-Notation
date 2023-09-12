package hmn.reaction;

import hmn.UC;
import hmn.graphicsLib.G;
import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Shape implements Serializable {

  public static Database DB = Database.load();
  public static Shape DOT = DB.get("DOT");
  public static Collection<Shape> LIST = DB.values(); // Auto updated when DB is updated
  public static Shape bestMatch; // Best matching shape in DB

  public Prototype.List prototypes = new Prototype.List();
  public String name;

  public Shape(String name) { this.name = name; }

  public static Shape recognize(Ink ink) { // Can return null
    // Handle dots
    if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold) { return DOT; }
    // Get the best matched shape
    bestMatch = null;
    int bestSoFar = UC.noMatchDist;
    for (Shape s : LIST) {
      int d = s.prototypes.bestDist(ink.norm);
      if (d < bestSoFar) { bestSoFar = d; bestMatch = s; }
    }
    return bestMatch;
  }

  // -------------------- Prototype -------------------
  public static class Prototype extends Ink.Norm implements Serializable {
    // Fundamentally a piece of normalized int
    // Represents clusters of points that represent a shape
    public int nBlend = 1; // How many prototypes have been blended together

    public void blend(Ink.Norm norm) { blend(norm, nBlend); nBlend++; }

    // -------------------- List -------------------
    /**
     * List of Shape prototypes which will be used for matching gestures.
     */
    public static class List extends ArrayList<Prototype> implements Serializable {
      public static Prototype bestMatch; // Set by side effect in min/best dist

      /**
       * Computes distance to each prototype and keeps the minimum distance. (Doesn't have to succeed).
       * @param norm
       * @return
       */
      public int bestDist(Ink.Norm norm) {
        bestMatch = null; // Assume no match
        int res = UC.noMatchDist;
        for (Prototype p : this) {
          int d = p.dist(norm);
          if (d < res) { res = d; bestMatch = p; } // Found best match so-far
        }
        return res;
      }

      private static int m = 10, w = 60; // Cells: margin, width
      private static G.VS showBox = new G.VS(m, m, w, w);

      /**
       * Shows all prototypes.
       * @param g
       */
      public void show(Graphics g) {
        g.setColor(Color.ORANGE);
        for (int i = 0; i < size(); i++) {
          Prototype p = get(i);
          int x = m + i*(m + w);
          showBox.loc.set(x, m);
          p.drawAt(g, showBox);
          g.drawString("" + p.nBlend, x, 20);
        }
      }

      public int hitProto(int x, int y) {
        // Return index of hit prototype (or -1)
        if (y < m || x < m || y > m + w) {return -1;}
        int res = (x - m) / (m + w);
        return res < size() ? res : -1;
      }

      public void train(Ink.Norm norm) {
        if (bestDist(norm) < UC.noMatchDist) { // Found match -> blend
          bestMatch.blend(norm);
        } else { add(new Shape.Prototype()); }
      }
    }
  }

  // -------------------- Database -------------------
  /**
   * Shape database used to associate shape names with their prototypes.
   */
  public static class Database extends HashMap<String, Shape> {

    public Database() {
      super();
      String dot = "DOT";
      put(dot, new Shape(dot));
    }

    public static Database load() {
      // Initialize DB
      Database res = null;
      // Load prior saved DB
      try {
        System.out.println("attempting db load...");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.shapeDbFileName));
        res = (Database) ois.readObject();
        System.out.println("successful load - found " + res.keySet());
        ois.close();
      } catch (Exception e) {
        System.out.println("load failed");
        System.out.println(e);
        res = new Database();
        res.put("DOT", new Shape("DOT"));
      }
      return res;
    }

    // Serialize the db
    public static void save() {
      try {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(UC.shapeDbFileName));
        oos.writeObject(DB);
        System.out.println("saved " + UC.shapeDbFileName);
        oos.close();
      } catch (Exception e) {
        System.out.println("failed db save");
        System.out.println(e);
      }
    }

    public Shape forcedGet(String name) {
      if (!DB.containsKey(name)) { DB.put(name, new Shape(name)); }
      return DB.get(name);
    }

    public void train(String name, Ink.Norm norm) {
      if (isLegal(name)) { forcedGet(name).prototypes.train(norm); }
    }

    public static boolean isLegal(String name) { return !name.equals("") && !name.equals("DOT"); }
  }
}







