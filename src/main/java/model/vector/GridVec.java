package model.vector;

/**
 * A discrete and less featured version of the {@code Vec2D class}, needed for accurate equality and hashing
 */
public record GridVec (int x, int y) {
  public GridVec(Vec2D v) {
    this((int) v.x(), (int) v.y());
  }

  public static GridVec add(GridVec a, GridVec b) {
    return new GridVec(a.x + b.x, a.y + b.y);
  }

  /**
   * Euclidean distance to given position
   */
  public double distance(GridVec vec) {
    var diff = new GridVec(vec.x - x, vec.y - y);
    return Math.sqrt(diff.x * diff.x + diff.y * diff.y);
  }
}
