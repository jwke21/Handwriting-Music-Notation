package hmn.music;

import hmn.UC;
import hmn.reaction.Gesture;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Graphics;

public class Staff extends Mass {

  public Sys sys; // System that the Staff lives in
  public int iStaff; // Index of the current staff in the Sys
  public Staff.Fmt fmt; // Format of the staff


  public Staff(int iStaff, Staff.Fmt sf) {
    super("BACK");
    this.iStaff = iStaff;
    this.fmt = sf;

    addReaction(new Reaction("S-S") { // Reaction for bar line
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        if (x < MusicEd.PAGE.margin.left || x > MusicEd.PAGE.margin.right + UC.barToMarginSnap) {
          return UC.noBid;
        }
        // Compute distance (reaction lives in a Staff so the Staff's features are available to us)
        int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
        // Upgrading a bar will always take precedence to drawing a new bar
        return (d < 30) ? d + UC.barToMarginSnap : UC.noBid;
      }
      @Override
      public void act(Gesture g) {
        new Bar(Staff.this.sys, g.vs.xM());
      }
    });

    addReaction(new Reaction("S-S") { // Toggling barContinues
      @Override
      public int bid(Gesture g) {
        // Only toggle barContinues on first system
        if (Staff.this.sys.iSys != 0) { return UC.noBid; }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        int iStaff = Staff.this.iStaff;
        if (iStaff == MusicEd.PAGE.sysFmt.size() - 1) { return UC.noBid; }
        if (Math.abs(y1 - Staff.this.yBot()) > 30) { return UC.noBid; }

        Staff nextStaff = sys.staffs.get(iStaff + 1);
        if (Math.abs(y2 - nextStaff.yTop()) > 30) { return UC.noBid; }
        // If we got this far, we very likely got a
        return 10;
      }
      @Override
      public void act(Gesture g) { MusicEd.PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues(); }
    });

    addReaction(new Reaction("SW-SW") { // Add Note to Staff
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if (x < MusicEd.PAGE.margin.left || x > MusicEd.PAGE.margin.right) { return UC.noBid; }
        int H = Staff.this.h();
        int top = Staff.this.yTop() - H;
        int bot = Staff.this.yBot() + H;
        if (y < top || y > bot) { return UC.noBid; }
        return 10;
      }
      @Override
      public void act(Gesture g) {
        new Head(Staff.this, g.vs.xM(), g.vs.yM());
      }
    });

    addReaction(new Reaction("W-S") { // Add Quarter Rest (i.e. REST_Q)
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < MusicEd.PAGE.margin.left || x > MusicEd.PAGE.margin.right) { return UC.noBid; }
        int H = Staff.this.h();
        int top = Staff.this.yTop() - H;
        int bot = Staff.this.yBot() + H;
        if (y < top || y > bot) { return UC.noBid; }
        return 10;
      }
      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        new Rest(Staff.this, t);
      }
    });

    addReaction(new Reaction("E-S") { // Add Eighth Rest (i.e. REST_)
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < MusicEd.PAGE.margin.left || x > MusicEd.PAGE.margin.right) { return UC.noBid; }
        int H = Staff.this.h();
        int top = Staff.this.yTop() - H;
        int bot = Staff.this.yBot() + H;
        if (y < top || y > bot) { return UC.noBid; }
        return 10;
      }
      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        (new Rest(Staff.this, t)).nFlag = 1;
      }
    });

    addReaction(new Reaction("SW-SE") { // Add clef
      @Override
      public int bid(Gesture g) {
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        return Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
      }
      @Override
      public void act(Gesture g) {
        new Clef(Staff.this, g.vs.xM(), Clef.G);
      }
    });
  }

  public int sysOff() { return sys.fmt.staffOffset.get(iStaff); } // This staff's offset in the system

  public int yTop() { return sys.yTop() + sysOff(); }

  public int yBot() { return yTop() + fmt.height(); }

  public int h() { return fmt.H; }

  public int yLine(int n) { return yTop() + n*h(); }

  public int lineOfY(int y) {
    int H = h();
    int bias = 100;
    int top = yTop() - H*bias;
    return (y - top + H/2)/H - bias;
  }

  @Override
  public void show(Graphics g) {} // Stub


  // ------------------------------ Staff.Fmt ------------------------------
  public static class Fmt {

    public int nLines = 5, H = UC.defaultStaffH; // Number of lines and height of Staff
    public boolean barContinues = false; // By default, bars don't change (user can change)

    public void toggleBarContinues() { barContinues = !barContinues; }

    public int height() { return 2 * H * (nLines - 1); }

    public void showAt(Graphics g, int y) {
      int left = MusicEd.PAGE.margin.left, right = MusicEd.PAGE.margin.right;
      for (int i = 0; i < nLines; i++) {
        g.drawLine(left, y + 2 * H * i, right, y + 2 * H * i);
      }
    }
  }
}
