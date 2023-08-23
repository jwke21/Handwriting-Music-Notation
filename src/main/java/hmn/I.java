package hmn;

import hmn.reaction.Gesture;
import java.awt.*;

public interface I {
  public interface Draw { public void draw(Graphics g); }
  public interface Hit { public boolean hit(int x, int y); }
  public interface Area extends Hit {
    public void dn(int x, int y); // Mouse down
    public void up(int x, int y); // Mouse up
    public void drag(int x, int y); // Mouse dragged
  }
  public interface Show { public void show(Graphics g); }
  public interface Act { public void act(Gesture g); }
  public interface React extends Act { public int bid(Gesture g); }

}
