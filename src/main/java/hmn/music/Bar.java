package hmn.music;

import hmn.UC;
import hmn.reaction.Gesture;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Graphics;

public class Bar extends Mass {

  private static final int FAT = 2, RIGHT = 4, LEFT = 8; // Bit flags
  public Sys sys;
  public int x, barType = 0; // Where it will be drawn

  public Bar(Sys sys, int x) {
    super("BACK");
    this.sys = sys;
    this.x = x;
    // Check if we're close to margin, if so snap to it
    if (Math.abs(MusicEd.PAGE.margin.right - x) < UC.barToMarginSnap) {
      this.x = MusicEd.PAGE.margin.right;
    }

    addReaction(new Reaction("S-S") { // Cycle the barType
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM();
        // S-S stroke must be close to existing bar
        if (Math.abs(x - Bar.this.x) > UC.barToMarginSnap) { return UC.noBid; }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        // If y vals are out of range then no bid
        if (y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20) { return UC.noBid; }
        return Math.abs(x - Bar.this.x);
      }

      @Override
      public void act(Gesture g) { Bar.this.cycleType(); }
    });

    addReaction(new Reaction("DOT") { // DOTs on bar line
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        // Check that we're in the range
        if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) { return UC.noBid; }
        int dist = Math.abs(x - Bar.this.x);
        // Check that the distance wasn't too big
        if (dist > 3*MusicEd.PAGE.sysFmt.maxH) { return UC.noBid; }
        return dist;
      }

      @Override
      public void act(Gesture g) {
        // Check if dots are drawn on left or right
        if (g.vs.xM() < Bar.this.x) {
          Bar.this.toggleLeft();
        } else {
          Bar.this.toggleRight();
        }
      }
    });
  }

  public void cycleType() { barType++; if (barType > 2) { barType = 0; } }

  public void toggleLeft() { barType = barType ^ LEFT; }

  public void toggleRight() { barType = barType ^ RIGHT; }

  @Override
  public void show(Graphics g) {
    // y1, y2 mark top and bottom of connected component
    int y1 = 0, y2 = 0;
    boolean justSawBreak = true;
    for (int i = 0; i < sys.fmt.size(); i++) {
      Staff staff = sys.staffs.get(i);
      int staffTop = staff.yTop();
      // Remember start of connected component
      if (justSawBreak) { y1 = staffTop; }
      y2 = staff.yBot();
      Staff.Fmt sf = sys.fmt.get(i);
      // Reached end of connected component
      if (!sf.barContinues) { drawLines(g, x, y1, y2); }
      justSawBreak = !sf.barContinues;
      // Check if we have dots
      if (barType > 3) { drawDots(g, x, staffTop); }
    }

//    g.setColor(barType == 1 ? Color.RED : Color.BLUE);
//    int yTop = sys.yTop(), N = sys.fmt.size();
//    for (int i = 0; i < N; i++) {
//      Staff.Fmt sf = sys.fmt.get(i);
//      int topLine = yTop + sys.fmt.staffOffset.get(i);
//      g.drawLine(x, topLine, x, topLine + sf.height());
//    }
  }

  // Dots on staff lines (that's why this method is not static)
  public void drawDots(Graphics g, int x, int top) {
    // Dots are drawn from top of single staff
    // Note: this code assumes nLines = 5 (i.e. not applicable to other instrument sheet music)
    int H = MusicEd.PAGE.sysFmt.maxH;
    if ((barType & LEFT) != 0) {
      g.fillOval(x - 3*H, top + 11*H / 4, H / 2, H / 2);
      g.fillOval(x - 3*H, top + 19*H / 4, H / 2, H / 2);
    }
    if ((barType & RIGHT) != 0) {
      g.fillOval(x + 3 * H / 2, top + 11 * H / 4, H / 2, H / 2);
      g.fillOval(x + 3 * H / 2, top + 19 * H / 4, H / 2, H / 2);
    }
  }

  public void drawLines(Graphics g, int x, int y1, int y2) {
    int H = MusicEd.PAGE.sysFmt.maxH;
    // Single bar
    if (barType == 0) { thinBar(g, x, y1, y2); }
    // Double bar
    else if (barType == 1) { thinBar(g, x, y1, y2); thinBar(g, x - H, y1, y2); }
    // Finae
    else if (barType == 2) { fatBar(g, x - H, y1, y2, H); thinBar(g, x - 2*H, y1, y2); }
    // Wings
    else if (barType >= 4) {
      fatBar(g, x - H, y1, y2, H);
      // Draw wings in the right direction
      if ((barType & LEFT) != 0) {
        thinBar(g, x - 2*H, y1, y2);
        wings(g, x - 2*H, y1, y2, -H, H);
      }
      else if ((barType & RIGHT) != 0) {
        thinBar(g, x + 2*H, y1, y2);
        wings(g, x + 2*H, y1, y2, H, H);
      }
    }
  }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
    g.drawLine(x, y1, x + dx, y1 - dy);
    g.drawLine(x, y2, x + dx, y2 + dy);
  }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {
    g.fillRect(x, y1, dx, y2 - y1);
  }

  public static void thinBar(Graphics g, int x, int y1, int y2) {
    g.drawLine(x, y1, x, y2);
  }

}
