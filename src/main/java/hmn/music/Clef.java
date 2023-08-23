package hmn.music;

import hmn.reaction.Gesture;
import hmn.reaction.Mass;
import hmn.reaction.Reaction;
import java.awt.Color;
import java.awt.Graphics;

public class Clef extends Mass {

  public static final Glyph G = Glyph.CLEF_G, F = Glyph.CLEF_F;
  public Staff staff;
  public int x;
  public Glyph glyph;

  public Clef(Staff staff, int x, Glyph glyph) {
    super("NOTE");
    this.staff = staff;
    this.x = x;
    this.glyph = glyph;


    addReaction(new Reaction("DOT") { // Toggle Clef between G and F
      @Override
      public int bid(Gesture g) {
        // TODO: add rejection threshold
        return Math.abs(g.vs.xM() - Clef.this.x) + Math.abs(g.vs.yM() - Clef.this.staff.yLine(4));
      }
      @Override
      public void act(Gesture g) { toggleClef(); }
    });
  }

  public void toggleClef() { glyph = glyph == G ? F : G; }

  @Override
  public void show(Graphics g) {
    g.setColor(Color.BLACK);
    glyph.showAt(g, staff.h(), x, staff.yLine(4)); // 4 is middle line of staff
  }
}






