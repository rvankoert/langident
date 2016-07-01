package nl.knaw.huygens.util;

public class Math2 {
  /**
   * log(exp(x) + exp(y)), computed in a stable way.
   */
  public static double logAddExp(double x, double y) {
    if (x > y) {
      double t = x;
      x = y;
      y = x;
    }
    return x + Math.log1p(Math.exp(y - x));
  }
}
