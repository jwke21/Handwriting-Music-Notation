package hmn.graphicsLib;

import java.awt.Graphics;

public class Spline {
  public static void pSpline(Graphics g, G.V a, G.V b, G.V c, int d) {
    // Base case
    if (d == 0) { g.drawLine(a.x, a.y, c.x, c.y); return; }
    // Calculate mid points
    G.V ab = new G.V((a.x + b.x) / 2, (a.y + b.y) / 2);
    G.V bc = new G.V((b.x + c.x) / 2, (b.y + c.y) / 2);
    G.V abc = new G.V((ab.x + bc.x) / 2, (ab.y + bc.y) / 2);
    // Recursive cases
    pSpline(g, a, ab, abc, d-1);
    pSpline(g, abc, bc, c, d-1);
  }
}
