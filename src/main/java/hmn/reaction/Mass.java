package hmn.reaction;

import hmn.I;
import hmn.graphicsLib.G;

// Extends Reaction.List (i.e. is a list of reactions). Will be ancestor to anything with a
// reaction list (e.g. note heads, staff lines, etc.)
public abstract class Mass extends Reaction.List implements I.Show {

  public Layer layer; // Layer that the Mass is currently in

  public Mass(String layerName) {
    layer = Layer.byName.get(layerName);
    if (layer != null) {
      layer.add(this);
    } else {
      System.out.println("Bad layer name: " + layerName);
    }
  }

  public void deleteMass() {
    clearAll(); // Clears all reactions from this list and byShape (the marketplace)
    layer.remove(this); // Remove self from layers
  }

  // Enforces referential equality for masses
  @Override
  public boolean equals(Object o) { return this == o; }

  private int hashCode = G.rnd(1000000000);

  @Override
  public int hashCode() { return hashCode; }
}
