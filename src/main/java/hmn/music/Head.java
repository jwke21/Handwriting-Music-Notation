package hmn.music;

import hmn.UC;
import hmn.reaction.Gesture;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head> { // Note head

  public Staff staff;
  public int line;
  public Time time;
  public Glyph forcedGlyph = null;
  public Stem stem = null;
  public boolean wrongSide = false;
  public Accidental accidental = null;

  public Head(Staff staff, int x, int y) {
    super("NOTE");
    this.staff = staff;
    time = staff.sys.getTime(x);
    time.heads.add(this);
    line = staff.lineOfY(y);
    System.out.println("line: " + line);

    addReaction(new Reaction("S-S") { // This will stem or unstem heads
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int w = Head.this.W();
        int hy = Head.this.y();
        if (y1 > y || y2 < y) { return UC.noBid; }
        int hl = Head.this.time.x, hr = hl + w;
        if (x < hl - w || x > hr + w) { return UC.noBid; }
        if (x < hl + w/2) { return hl - x; } // Winning bid
        if (x > hr - w/2) { return x - hr; } // Winning bid
        return UC.noBid;
      }
      @Override
      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time t = Head.this.time;
        int w = Head.this.W();
        boolean up = x > (t.x + w/2) ? true : false;
        // Deciding whether joining to stem or unstemming
        if (Head.this.stem == null) {
          Stem.getStem(staff, t, y1, y2, up);
//          t.stemHeads(staff, up, y1, y2);
        } else {
          t.unStemHeads(y1, y2);
        }
      }
    });

    addReaction(new Reaction("DOT") { // Draw augmentation dot
      @Override
      public int bid(Gesture g) {
        int xH = Head.this.x(), yH = Head.this.y(), H = Head.this.staff.h(), W = Head.this.W();
        int x = g.vs.xM(), y = g.vs.yM();
        // Check that dot was close enough to note head
        if (x < xH || x > xH + 2*W || y < yH - H || y > yH + H) { return UC.noBid; }
        return Math.abs(xH + W - x) + Math.abs(yH - y);
      }
      @Override
      public void act(Gesture g) {
        if (Head.this.stem != null) { Head.this.stem.cycleDot(); }
      }
    });

    addReaction(new Reaction("NE-SE") {
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yL(); // Coordinates for tip of arrow
        // TODO: add rejection threshold
        return Math.abs(x - Head.this.x()) + Math.abs(y - Head.this.y());
      }
      @Override
      public void act(Gesture g) {
        if (Head.this.accidental != null) {
          Head.this.incAccidental();
        } else {
          Head.this.accidental = new Accidental(Head.this);
        }
      }
    });

    // TODO: add SE-NE gesture reaction
  }

  @Override
  public void show(Graphics g) {
    int H = staff.h();
    g.setColor(stem == null ? Color.RED : Color.BLACK);
    (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
    if (stem != null) {
      int off = UC.augDotOffsetRest, sp = UC.augDotSpaceRest;
      for (int i = 0; i < stem.nDot; i++) {
        g.fillOval(time.x + off + i*sp, y() - 3*H/2, H*2/3, H*2/3);
      }
    }
  }

  public int W() { return 24*staff.h()/10; }

  public int y() { return staff.yLine(line); }

  public int x() {
    int res = time.x;
    // Move to correct side of the staff
    if (wrongSide) { res += (stem != null && stem.isUp) ? W() : -W(); }
    return res;
  }

  public Glyph normalGlyph() {
    if (stem == null) { return Glyph.HEAD_Q; }
    if (stem.nFlag == -1) { return Glyph.HEAD_HALF; }
    if (stem.nFlag == -2) { return Glyph.HEAD_W; }
    return Glyph.HEAD_Q;
  }

  public void deleteHead() { time.heads.remove(this); } // Stub

  public void unStem() {
    if (stem == null) { return; }
    stem.heads.remove(this);
    // If last head is removed, delete stem
    if (stem.heads.size() == 0) { stem.deleteStem(); }
    stem = null;
    wrongSide = false;
  }

  public void incAccidental() { accidental.inc(); }

//  public void joinStem(Stem s) {
//    // Remove head from stem it was already on
//    if (stem != null) { unStem(); }
//    s.heads.add(this);
//    stem = s;
//  }

  @Override
  public int compareTo(Head h) {
    // Sort by staff and line
    return (staff.iStaff != h.staff.iStaff) ? staff.iStaff - h.staff.iStaff : line - h.line;
  }

  // -------------------- List -------------------
  public static class List extends ArrayList<Head> {

  }
}







