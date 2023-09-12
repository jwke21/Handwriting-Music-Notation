package hmn;

import hmn.graphicsLib.Window;
import hmn.music.MusicEd;
import hmn.sandbox.Paint;

public class Main {

  public static void main(String[] args) {
//    Window.PANEL = new Paint();
//    Window.launch();
    Window.PANEL = new MusicEd();
    Window.PANEL.launch();
  }
}
