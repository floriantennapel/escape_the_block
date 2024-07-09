package model.vector;

public record Vec2D(double x, double y) {
  public static Vec2D add(Vec2D a, Vec2D b) {
    return new Vec2D(a.x + b.x, a.y + b.y);
  }

  /**
   * dot product of two vectors
   */
  public static double dotProduct(Vec2D a, Vec2D b) {
    return a.x * b.x + a.y * b.y;
  }

  /**
   * @return result of scaling {@code this}
   */
  public Vec2D scale(double scalar) {
    return new Vec2D(x * scalar, y * scalar);
  }

  /**
   * Create a rotated version of {@code this}
   * @param theta angle in radians
   */
  public Vec2D rotate(double theta) {
    double cos = Math.cos(theta);
    double sin = Math.sin(theta);

    return new Vec2D(
        x * cos - y * sin,
        x * sin + y * cos
    );
  }

  /**
   * length of vector
   */
  public double length() {
    return Math.sqrt(x * x + y * y);
  }
}
