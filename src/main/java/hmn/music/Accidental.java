package hmn.music;

import hmn.reaction.Mass;
import java.awt.Color;
import java.awt.Graphics;

public class Accidental extends Mass {

  public static final Glyph[] glyphs = {Glyph.DOUBLE_FLAT, Glyph.FLAT, Glyph.NATURAL,
                                          Glyph.SHARP, Glyph.DOUBLE_SHARP};
  public int iGlyph;
  public Head head;

  public Accidental(Head head) {
    super("NOTE");
    this.head = head;
    iGlyph = 2;
  }

  public void inc() { iGlyph = iGlyph == glyphs.length - 1 ? 0 : iGlyph + 1; }

  @Override
  public void show(Graphics g) {
    int x = head.x() - head.W(), y = head.y();
    g.setColor(Color.BLACK);
    glyphs[iGlyph].showAt(g, head.staff.h(), x, y);
  }
}
