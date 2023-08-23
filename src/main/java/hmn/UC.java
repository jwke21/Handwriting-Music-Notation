package hmn;

import java.awt.Color;

// Universal Constants
public class UC {
  public static final int MAIN_WINDOW_WIDTH = 1_000;
  public static final int MAIN_WINDOW_HEIGHT = 700;
  public static final int inkBufferMax = 200;
  public static final int normSampleSize = 20; // Will sub-sample 20 points from drawn line (tunable)
  public static final int normCoordMax = 1_000;
  public static final int noMatchDist = 500_000; // Dist threshold for there to be a match
  public static final int dotThreshold = 5;
  public static final int noBid = 10_000;
  public static final int barToMarginSnap = 20;
  public static final int defaultStaffH = 8;
  public static final int snapTime = 30;
  public static final int augDotOffsetRest = 24;
  public static final int augDotSpaceRest = 9;
  public static final Color defaultInkColor = Color.BLACK;
  public static String shapeDbFileName = "ShapeDB.dat";
  public static String FontName = "Sinfonia";
}








