package model;

import controller.ControllableModel;
import model.vector.Matrix;
import model.vector.Vec2D;
import model.vector.ImmutableVec2D;
import view.ViewableModel;

public class Model implements ViewableModel, ControllableModel {
  private final Vec2D playerPos;
  private final Vec2D playerDir;
  private final Vec2D viewPort;

  private final GridMap map;
  private final int mapSize;

  public Model(int mapSize) {
    if (mapSize < 3 || mapSize > 1000) {
      throw new IllegalArgumentException("invalid map size");
    }

    playerPos = new Vec2D(10, 10);
    playerDir = new Vec2D(0, 1);
    viewPort = new Vec2D(0.7, 0);
    map = new GridMap(mapSize);
    this.mapSize = mapSize;
  }

  @Override
  public int checkGridCell(int row, int col) {
    return map.get(row, col);
  }

  @Override
  public ImmutableVec2D getPlayerPos() {
    return playerPos;
  }

  @Override
  public ImmutableVec2D getPlayerDir() {
    return playerDir;
  }

  @Override
  public ImmutableVec2D getViewPort() {
    return viewPort;
  }

  @Override
  public Vec2D getPos() {
    return playerPos;
  }

  @Override
  public Vec2D getDir() {
    return playerDir;
  }

  @Override
  public void rotatePlayerDir(Matrix rotMat) {
    playerDir.transform(rotMat);
    viewPort.transform(rotMat);
  }
}
