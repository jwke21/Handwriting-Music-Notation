package hmn.music;

import hmn.UC;
import hmn.reaction.Gesture;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Page extends Mass {

  public Margin margin = new Margin();
  public Sys.Fmt sysFmt;
  public int sysGap, nSys; // Gap between systems and number of systems
  public ArrayList<Sys> sysList = new ArrayList<>();

  public Page(Sys.Fmt sysFmt) {
    super("BACK");
    this.sysFmt = sysFmt;

    addReaction(new Reaction("E-E") {
      public int bid(Gesture g) { // Adding a new Staff
        int y = g.vs.yM();
        // New Staffs must below top margin, below current system, and 30px below previous staff
        if (y <= MusicEd.PAGE.margin.top + sysFmt.height() + 30) { return UC.noBid; }
        return 50;
      }
      public void act(Gesture g) {
        int y = g.vs.yM();
        MusicEd.PAGE.addNewStaff(y - MusicEd.PAGE.margin.top);
      }
    });

    addReaction(new Reaction("E-W") { // Adding a new Sys
      public int bid(Gesture g) {
        int y = g.vs.yM();
        int yBot = MusicEd.PAGE.sysTop(nSys);
        // New staffs must be drawn below others
        if (y <= yBot) { return UC.noBid; }
        return 50;
      }
      public void act(Gesture g) {
        int y = g.vs.yM();
        // If this is the second Sys, then the sysGap must be defined
        if (MusicEd.PAGE.nSys == 1) {
          MusicEd.PAGE.sysGap = y - MusicEd.PAGE.sysTop(1);
        }
        MusicEd.PAGE.addNewSys();
      }
    });
  }

  public int sysTop(int iSys) { return margin.top + iSys * (sysFmt.height() + sysGap); }

  public void addNewStaff(int yOff) {
    Staff.Fmt sf = new Staff.Fmt();
    int n = sysFmt.size();
    sysFmt.add(sf); sysFmt.staffOffset.add(yOff);
    for (int i = 0; i < nSys; i++) {
      sysList.get(i).addStaff(new Staff(n, sf));
    }
  }

  public void addNewSys() { sysList.add(new Sys(nSys, sysFmt)); nSys++; }

  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLACK);
    for (int i = 0; i < nSys; i++) { sysFmt.showAt(g, sysTop(i)); }
  }

  // ------------------------------ Margin ------------------------------
  public static class Margin {
    private static final int M = 50;
    public int top = M, left = M;
    public int bot = UC.MAIN_WINDOW_HEIGHT - M, right = UC.MAIN_WINDOW_WIDTH - M;
  }

}
