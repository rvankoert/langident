package nl.knaw.huygens.pergamon.util;

import org.junit.Assert;
import org.junit.Test;

import static java.lang.Math.log;
import static nl.knaw.huygens.pergamon.util.Math2.logAddExp;

public class Math2Test {
  @Test
  public void testLogAddExp() {
    // XXX Find a better example here.
    double x = log(1e-50);
    double y = log(2.5e-50);
    Assert.assertTrue(Math.abs(logAddExp(x, y) + 113.87649168120691) < 1e-15);
  }
}
