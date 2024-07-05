package model.vector;

import java.util.Arrays;

public class Matrix {
  private final double[][] mat;

  private Matrix(double[][] mat) {
    if (mat == null) {
      throw new NullPointerException();
    }
    this.mat = mat;
  }

  /**
   * Creates a matrix to rotate a vector by the given angle
   * @param theta angle in radians
   * @return a new {@code Matrix} to be used in linear transformations
   */
  public static Matrix newRotMat(double theta) {
    double cos = Math.cos(theta);
    double sin = Math.sin(theta);

    return new Matrix(
        new double[][] {{cos, -sin, 0}, {sin, cos, 0}, {0, 0, 1}}
    );
  }

  /**
   * Creates a matrix to scale a vector
   * @return a new {@code Matrix} to be used in linear transformations
   */
  public static Matrix newScalarMat(double scalar) {
    return new Matrix(
        new double[][] {{scalar, 0, 0}, {0, scalar, 0}, {0, 0, 1}}
    );
  }

  /**
   * Creates a matrix to translate a vector,  i.e. vector addition
   * @return a new {@code Matrix} to be used in linear transformations
   */
  public static Matrix newTransMat(Vec2D vec) {
    if (vec == null) {
      throw new NullPointerException();
    }

    return new Matrix(
        new double[][] {{1, 0, vec.get(0)}, {0, 1, vec.get(1)}, {0, 0, 1}}
    );
  }

  /**
   * Get the value at the specified index
   */
  public double get(int row, int col) {
    if (row < 0 || row >= 3 || col < 0 || col >= 3) {
      throw new IndexOutOfBoundsException();
    }

    return mat[row][col];
  }

  /**
   * Multiply {@code Matrix a} with {@code this}
   * <br/>
   * <br/>
   * {@code a} is on the left hand side and {@code this} is on the right
   * i.e. {@code b.mul(a) == a * b}
   * @param a left hand side of matrix product
   * @return result of the multiplication
   */
  public Matrix mul(Matrix a) {
    var retMat = new double[3][3];

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double sum = 0;

        for (int k = 0; k < 3; k++) {
          sum += a.get(i, k) * this.get(k, j);
        }

        retMat[i][j] = sum;
      }
    }

    return new Matrix(retMat);
  }

  @Override
  public String toString() {
    var s = new StringBuilder();
    for (var row : mat) {
      s.append(Arrays.toString(row)).append('\n');
    }

    return s.substring(0, s.length() - 1);
  }
}
