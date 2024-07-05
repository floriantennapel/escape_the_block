package model.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestVectorTransformations {
  @Test
  public void testRotate1() {
    var vec = new Vec2D(4, 0);
    var rotMat = Matrix.newRotMat(Math.PI / 2);
    vec.transform(rotMat);
    assertTrue(vecEquals(vec, new Vec2D(0, 4)));
  }

  @Test
  public void testRotate2() {
    var vec = new Vec2D(2, 1);
    var rotMat = Matrix.newRotMat(-2 * Math.PI);
    vec.transform(rotMat);
    assertTrue(vecEquals(vec, new Vec2D(2, 1)));
  }

  @Test
  public void testTranslate() {
    var vec = new Vec2D(-3, 2);
    var transMat = Matrix.newTransMat(new Vec2D(1.5, 0));
    vec.transform(transMat);
    assertTrue(vecEquals(vec, new Vec2D(-1.5, 2)));
  }

  @Test
  public void testScaling1() {
    assertThrows(IllegalArgumentException.class, () -> Matrix.newScalarMat(0.00000001));
  }

  @Test
  public void testScaling2() {
    var vec = new Vec2D(4, 9);
    var scaleMat = Matrix.newScalarMat(1);
    vec.transform(scaleMat);
    assertTrue(vecEquals(vec, new Vec2D(4, 9)));
  }

  @Test
  public void testScaling3() {
    var vec = new Vec2D(4, 9);
    var scaleMat = Matrix.newScalarMat(-2.5);
    vec.transform(scaleMat);
    assertTrue(vecEquals(vec, new Vec2D(-10, -22.5)));
  }

  @Test
  public void testTransitive() {
    var vec = new Vec2D(1, 0);
    var rotMat1 = Matrix.newRotMat(Math.PI);
    var rotMat2 = Matrix.newRotMat(Math.PI / 2.);
    var scaleMat = Matrix.newScalarMat(-2);
    var transMat = Matrix.newTransMat(new Vec2D(0, 1));

    var combined = rotMat1.mul(rotMat2).mul(scaleMat).mul(transMat);
    vec.transform(combined);
    assertTrue(vecEquals(vec, new Vec2D(0, 3)));
  }

  /**
   * Since Vec2D is mutable, we do not want to add equals and hashCode to the class itself
   */
  private boolean vecEquals(Vec2D a, Vec2D b) {
    for (int i = 0; i < 2; i++) {
      if (Math.abs(a.get(i) - b.get(i)) > 0.0001) {
        return false;
      }
    }

    return true;
  }
}
