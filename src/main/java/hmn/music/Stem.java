package hmn.music;

import hmn.UC;
import hmn.reaction.Gesture;
import hmn.reaction.Reaction;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem> {

  public Staff staff;
  public Head.List heads = new Head.List();
  public boolean isUp = true;
  public Beam beam = null;

  public Stem(Staff staff, Head.List heads, boolean up) {
    this.staff = staff;
    isUp = up;

    for (Head h : heads) {
      h.unStem();
      h.stem = this;
    }
    this.heads = heads;
    staff.sys.stems.addStem(this);
    setWrongSides();

    addReaction(new Reaction("E-E") { // Increment the flags
      @Override
      public int bid(Gesture g) {
        return bidLineCrossesStem(g.vs.yM(), g.vs.xL(), g.vs.xH(), Stem.this);
      }
      @Override
      public void act(Gesture g) { Stem.this.incFlag(); }
    });

    addReaction(new Reaction("W-W") { // Decrement the flags
      @Override
      public int bid(Gesture g) {
        return bidLineCrossesStem(g.vs.yM(), g.vs.xL(), g.vs.xH(), Stem.this);
      }
      @Override
      public void act(Gesture g) { Stem.this.decFlag(); }
    });
  }

  public int bidLineCrossesStem(int y, int x1, int x2, Stem stem) {
    int xS = x();
    if (x1 > xS || x2 < xS) { return UC.noBid; }
    int y1 = stem.yLo(), y2 = stem.yHi();
    if (y < y1 || y > y2) { return UC.noBid; }
    return Math.abs(y - (y1 + y2)/2) + 60; // See E-E reaction in sys class
  }

  // Factory method gets Stem if there are heads available
  public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up) { // Can return null
    Head.List heads = new Head.List();
    for (Head h : time.heads) {
      int yH = h.y();
      // Check if head needs to be added to stem
      if (yH > y1 && yH < y2) { heads.add(h); }
    }
    if (heads.size() == 0) { return null; }
    Beam b = internalStem(staff.sys, time.x, y1, y2);
    Stem res = new Stem(staff, heads, up);
    if (b != null) {
      b.addStem(res);
      res.nFlag = 1;
    }
    return res;
  }

  public static Beam internalStem(Sys sys, int x, int y1, int y2) { // Can return null
    for (Stem s : sys.stems) {
      if (s.beam != null) {
        int bX = s.beam.first().x(), bY = s.beam.first().yBeamEnd();
        int eX = s.beam.last().x(), eY = s.beam.last().yBeamEnd();
        System.out.println("Found beam " + bX + " " + bY + " " + eX + " " + eY);
        if (Beam.verticalLineCrossesSegment(x, y1, y2, bX, bY, eX, eY)) { return s.beam; }
      }
    }
    return null;
  }

  @Override
  public void show(Graphics g) {
    if (nFlag < -1 && heads.size() == 0) { return; }
    int h = staff.h(), yH = yFirstHead(), yB = yBeamEnd();
    if (nFlag > 0 && beam == null) {
      if (nFlag == 1) { (isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, h, x(), yB); }
      if (nFlag == 2) { (isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, h, x(), yB); }
      if (nFlag == 3) { (isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, h, x(), yB); }
      if (nFlag == 4) { (isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, h, x(), yB); }
    }
    g.drawLine(x(), yH, x(), yB);
  }

  public Head firstHead() { return heads.get(isUp ? heads.size() - 1 : 0); }

  public Head lastHead() { return heads.get(isUp ? 0 : heads.size() - 1); }

  public int yLo() { return isUp ? yBeamEnd() : yFirstHead(); }

  public int yHi() { return isUp ? yFirstHead() : yBeamEnd(); }

  public int yFirstHead() {
    if (heads.size() == 0) { return 200; } // Put fake x location for malformed
    return firstHead().y();
  }

  public int yBeamEnd() {
    if (heads.size() == 0) { return 100; } // Put fake x location for malformed
    if (isInternalStem()) {
      beam.setMasterBeam();
      return beam.yOfX(x());
    }
    Head h = lastHead();
    int line = h.line;
    line += isUp ? -7 : 7; // Default is one octave (i.e. 7) from last head on Stem
    int flagInc = nFlag > 2 ? 2*(nFlag - 2) : 0; // If more than 2 flags, adjust beamEnd
    line += isUp ? -flagInc : flagInc;
    // Meet center line if necessary
    if ((isUp && line > 4) || (!isUp && line < 4)) { line = 4; }
    return h.staff.yLine(line);
  }

  public boolean isInternalStem() {
    if (beam == null) { return false; }
    return beam.first() != this && beam.last() != this;
  }

  public int x() {
    if (heads.size() == 0) { return 100; } // Put fake x location for malformed
    Head h = firstHead();
    return h.time.x + (isUp ? h.W() : 0);
  }

  public void deleteStem() {
    if (heads.size() != 0) { System.out.println("Deleting stem that had heads on it."); }
    staff.sys.stems.remove(this);
    if (beam != null) { beam.removeStem(this); }
    deleteMass();
  }

  public void setWrongSides() { // Called by time.stemHeads
    Collections.sort(heads);
    int i, last, next;
    if (isUp) { // Sort from bottom to top
      i = heads.size() - 1;
      last = 0;
      next = -1;
    } else { // Sort from top to bottom
      i = 0;
      last = heads.size() - 1;
      next = 1;
    }
    Head ph = heads.get(i);
    ph.wrongSide = false; // The first head on the stem is always on the right side
    while (i != last) {
      i += next;
      Head nh = heads.get(i);
      nh.wrongSide = (ph.staff == nh.staff) && (Math.abs(nh.line - ph.line) <= 1) && !ph.wrongSide;
      ph = nh;
    }
  }

  @Override
  public int compareTo(Stem s) {
    return x() - s.x();
  }

  // -------------------- List --------------------
  public static class List extends ArrayList<Stem> {

    public int yMin = Integer.MAX_VALUE, yMax = Integer.MIN_VALUE;

    public void addStem(Stem s) {
      add(s);
      if (s.yLo() < yMin) { yMin = s.yLo(); }
      if (s.yHi() > yMax) { yMax = s.yHi(); }
    }

    public void sort() { Collections.sort(this); }

    public boolean fastReject(int y) { return y < yMin || y > yMax; }

    public ArrayList<Stem> allIntersectors(int x1, int y1, int x2, int y2) {
      ArrayList<Stem> res = new ArrayList<>();
      for (Stem s : this) {
        if (Beam.verticalLineCrossesSegment(s.x(), s.yLo(), s.yHi(), x1, y1, x2, y2)) { res.add(s); }
      }
      return res;
    }
  }
}


