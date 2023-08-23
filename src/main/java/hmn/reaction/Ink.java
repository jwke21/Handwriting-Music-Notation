package hmn.reaction;

import hmn.I;
import hmn.UC;
import hmn.graphicsLib.G;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink implements I.Show {
  public static Buffer BUFFER = new Buffer();
  public Norm norm;
  public G.VS vs;

  public Ink() {
    norm = new Norm(); // Get normalized representation of what user drew
    vs = BUFFER.bBox.getNewVS(); // Location where user drew on the screen
  }

  @Override
  public void show(Graphics g) { g.setColor(UC.defaultInkColor); norm.drawAt(g, vs); }

  //--------------------------- Norm ---------------------------
  public static class Norm extends G.PL implements Serializable {
    // Normalized coordinate system within which shape matching will occur
    public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
    public static final G.VS normCoordSys = new G.VS(0, 0, MAX, MAX); // Normalized coordinate system

    public Norm() {
      super(N);
      BUFFER.subSample(this);
      G.V.T.set(BUFFER.bBox, normCoordSys);
      transform();
    }

    public void drawAt(Graphics g, G.VS vs) {
      G.V.T.set(normCoordSys, vs);
      for (int i = 1; i < N; i++) {
        g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
      }
    }

    // Computes Euclidean distance between this Norm and another
    public int dist(Norm n) {
      int res = 0;
      for (int i = 0; i < N; i++) {
        int dx = points[i].x - n.points[i].x;
        int dy = points[i].y - n.points[i].y;
        res += dx * dx + dy * dy;
      }
      // Do not need square root calculation
      return res;
    }

    public void blend(Norm norm, int n) {
      for (int i = 0; i < N; i++) { points[i].blend(norm.points[i], n); }
    }
  }

  //--------------------------- Buffer ---------------------------
  public static class Buffer extends G.PL implements I.Show, I.Area {
    public static final int MAX = UC.inkBufferMax; // max size of buffer
    public int n; // number of points in buffer
    public G.BBox bBox = new G.BBox(); // Bounding box

    // Singleton
    private Buffer() { super(MAX); }

    public void add(int x, int y) { if (n < MAX) { points[n].set(x, y); bBox.add(x, y); n++; } }

    public void clear() { n = 0; }

    public void show(Graphics g) { drawNDots(g, n); }

    public boolean hit(int x, int y) { return true; }

    public void dn(int x, int y) { clear(); add(x, y); bBox.set(x, y); }

    public void drag(int x, int y) { add(x, y); }

    public void up(int x, int y) {}

    // Copy from Ink buffer into PL
    public void subSample(G.PL pl) {
      // Get size of polyline (num points in result)
      int K = pl.size();
      // Linear subsample
      for (int i = 0; i < K; i++) {
        // Multiplicative factor: (n - 1) / (K - 1)
        int j = i * (n - 1) / (K - 1);
        pl.points[i].set(points[j].x, points[j].y);
      }
    }
  }

  //--------------------------- List ---------------------------
  public static class List extends ArrayList<Ink> implements I.Show {

    @Override
    public void show(Graphics g) { for (Ink ink : this) { ink.show(g); } }
  }
}
