package model.vector;

import java.util.Arrays;

public class Vec2D implements ImmutableVec2D {
  private final double[] vec;
  private final double[] tmp = new double[3]; // used for tranformations

  public static ImmutableVec2D scaled(ImmutableVec2D vec, double scalar) {
    if (vec == null) {
      throw new NullPointerException();
    }

    return new Vec2D(vec.get(0) * scalar, vec.get(1) * scalar);
  }

  public Vec2D(double x, double y) {
    vec = new double[] {x, y, 1};
  }

  public Vec2D(ImmutableVec2D v) {
    this(v.get(0), v.get(1));
  }

  /**
   * Apply a Matrix transformation to {@code this}
   */
  public void transform(Matrix mat) {
    if (mat == null) {
      throw new NullPointerException();
    }

    for (int i = 0; i < 3; i++) {
      double sum = 0;
      for (int j = 0; j < 3; j++) {
        sum += vec[j] * mat.get(i, j);
      }

      tmp[i] = sum;
    }

    System.arraycopy(tmp, 0, vec, 0, 3);
  }

  /**
   * length of vector
   */
  public double length() {
    return Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1]);
  }

  /**
   * scale vector by given scalar
   */
  public void scale(double scalar) {
    if (Math.abs(scalar - 0) < 0.0000001) {
      throw new IllegalArgumentException("zero scalar not allowed");
    }

    vec[0] = vec[0] * scalar;
    vec[1] = vec[1] * scalar;
  }

  public void add(ImmutableVec2D v) {
    vec[0] += v.get(0);
    vec[1] += v.get(1);
  }

  @Override
  public double get(int i) {
    if (i < 0 || i >= 2) {
      throw new IndexOutOfBoundsException();
    }

    return vec[i];
  }

  @Override
  public String toString() {
    return Arrays.toString(vec);
  }
}
