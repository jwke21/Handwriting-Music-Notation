package hmn.reaction;

import hmn.I;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

// Showable objects (When it is full of showable objects, it is showable itself)
public class Layer extends ArrayList<I.Show> implements I.Show {

  public static HashMap<String,Layer> byName = new HashMap<>();
  public static Layer ALL = new Layer("ALL"); // All layers
  public String name;

  public Layer(String name) {
    this.name = name;
    if (!name.equals("ALL")) { ALL.add(this); }
    byName.put(name, this);
  }

  @Override
  public void show(Graphics g) {
    // Show all items in this layer
    for (I.Show item : this) { item.show(g); }
  }

  public static void nuke() { // Nukes layers and prep for undo
    for (I.Show layer : ALL) { ((Layer) layer).clear(); }
  }

  public static void createAll(String[] a) { for (String s : a) { new Layer(s); } }

}
