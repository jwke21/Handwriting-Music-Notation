package hmn.music;

import hmn.UC;
import hmn.reaction.Gesture;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Graphics;
import java.util.ArrayList;

public class Sys extends Mass {

  public ArrayList<Staff> staffs = new ArrayList<>(); // Fuck 'staves' bc code shouldn't be English
  public Page page = MusicEd.PAGE; // Page this Sys lives on (only one for now but can be extended)
  public int iSys; // How far down on page this Sys is
  public Sys.Fmt fmt; // Format of this Sys
  public Time.List times;
  public Stem.List stems = new Stem.List();

  public Sys(int iSys, Sys.Fmt fmt) {
    super("BACK");
    this.iSys = iSys;
    this.fmt = fmt;
    times = new Time.List(this);
    // Add the system's staffs
    for (int i = 0; i < fmt.size(); i++) { addStaff(new Staff(i, fmt.get(i))); }

    addReaction(new Reaction("E-E") { // Beaming stems
      @Override
      public int bid(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL();
        int x2 = g.vs.xH(), y2 = g.vs.yH();
        if (stems.fastReject((y1 + y2)/2)) { return UC.noBid; }
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        if (temp.size() < 2) { return UC.noBid; }
        Beam b = temp.get(0).beam;
        for (Stem s : temp) {
          if (s.beam != b) { return UC.noBid; }
        }
        if (b == null && temp.size() != 2) { return UC.noBid; }
        if (b == null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)) { return UC.noBid; }
        return 50;
      }
      @Override
      public void act(Gesture g) {
        int x1 = g.vs.xL(), y1 = g.vs.yL();
        int x2 = g.vs.xH(), y2 = g.vs.yH();
        ArrayList<Stem> temp = stems.allIntersectors(x1, y1, x2, y2);
        Beam b = temp.get(0).beam;
        if (b == null) {
          new Beam(temp.get(0), temp.get(1));
        } else {
          for (Stem s : temp) {
            s.incFlag();
          }
        }
      }
    });
  }

  // Get top of system
  public int yTop() { return page.sysTop(iSys); }

  public int yBot() { return staffs.get(staffs.size() - 1).yBot(); }

  public void addStaff(Staff s) { staffs.add(s); s.sys = this; }

  @Override
  public void show(Graphics g) {
    int y = yTop(), x = page.margin.left;
    g.drawLine(x, y, x, y + fmt.height());
  }

  public Time getTime(int x) { return times.getTime(x); }

  // ------------------------------ Sys.Fmt ------------------------------
  public static class Fmt extends ArrayList<Staff.Fmt> {

    public ArrayList<Integer> staffOffset = new ArrayList<>();
    public int maxH = UC.defaultStaffH;

    public int height() {
      int last = size() - 1;
      return staffOffset.get(last) + get(last).height();
    }

    public void showAt(Graphics g, int y) {
      for (int i = 0; i < size(); i++) { get(i).showAt(g, y + staffOffset.get(i)); }
    }
  }
}
