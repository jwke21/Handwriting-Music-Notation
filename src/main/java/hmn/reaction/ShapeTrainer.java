package hmn.reaction;

import hmn.graphicsLib.G;
import hmn.graphicsLib.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends Window {

  public static String UNKNOWN = " <- This name is currently unknown.";
  public static String ILLEGAL = " <- This name is NOT a legal shape name.";
  public static String KNOWN = " <- This is a known shape.";
  public static String curName = "";
  public static String curState = ILLEGAL;
  public static Shape.Prototype.List pList = null;

  public ShapeTrainer() {
    super("ShapeTrainer", 1000, 700);
  }

  public void setState() {
    curState = (Shape.Database.isLegal(curName)) ? UNKNOWN : ILLEGAL;
    if (curState == UNKNOWN) {
      if (Shape.DB.containsKey(curName)) {
        curState = KNOWN;
        pList = Shape.DB.get(curName).prototypes;
      } else { pList = null; }
    }
  }

  public void paintComponent(Graphics g) {
    G.clearBack(g);
    g.setColor(Color.BLACK);
    g.drawString(curName, 600, 30);
    g.drawString(curState, 700, 30);
    g.setColor(Color.RED);
    Ink.BUFFER.show(g);
    if (pList != null) { pList.show(g); }
  }

  // keyTyped only correctly interprets ASCII values
  public void keyTyped(KeyEvent ke) {
    char c = ke.getKeyChar();
    System.out.println("Typed: " + c);
    // End symbols: ' ', '\r', '\n'
    curName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : curName + c;
    if (c == 0x0D || c == 0x0A) { Shape.Database.save(); }
    setState();
    repaint();
  }

  public void mousePressed(MouseEvent me) { Ink.BUFFER.dn(me.getX(), me.getY()); repaint(); }
  public void mouseDragged(MouseEvent me) { Ink.BUFFER.drag(me.getX(), me.getY()); repaint(); }
  public void mouseReleased(MouseEvent me) {
    Ink ink = new Ink();
    Shape recognized = Shape.recognize(ink);
    if (recognized == Shape.DOT) { removePrototype(me.getX(), me.getY()); return; }
    Shape.DB.train(curName, ink.norm);
    setState();
    repaint();
  }

  public void removePrototype(int x, int y) {
    if (pList == null) {return;}
    int mdx = pList.hitProto(x, y);
    if (mdx >= 0) {pList.remove(mdx);}
    repaint();
  }

  public static void main(String[] args) { (PANEL = new ShapeTrainer()).launch(); }
}








