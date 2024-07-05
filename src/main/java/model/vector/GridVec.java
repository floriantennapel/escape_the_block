package model.vector;

public record GridVec (int x, int y) {
  public GridVec(ImmutableVec2D v) {
    this((int) v.get(0), (int) v.get(1));
  }

  public static GridVec add(GridVec a, GridVec b) {
    return new GridVec(a.x + b.x, a.y + b.y);
  }
}
