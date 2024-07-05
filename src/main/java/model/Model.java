package model;

import controller.ControllableModel;
import model.vector.GridVec;
import model.vector.Matrix;
import model.vector.Vec2D;
import model.vector.ImmutableVec2D;
import view.ViewableModel;

public class Model implements ViewableModel, ControllableModel {
  private final Vec2D playerPos;
  private final Vec2D playerDir;
  private final Vec2D viewPort;
  private GridVec blockPos;

  private final GridMap map;
  private final int mapSize;

  public Model(int mapSize) {
    if (mapSize < 3 || mapSize > 1000) {
      throw new IllegalArgumentException("invalid map size");
    }

    blockPos = new GridVec(1, 1);

    playerPos = new Vec2D(10, 10);
    playerDir = new Vec2D(0, 1);
    viewPort = new Vec2D(0.7, 0);
    map = new GridMap(mapSize, blockPos);
    this.mapSize = mapSize;
  }

  @Override
  public int checkGridCell(int row, int col) {
    return map.get(row, col);
  }

  @Override
  public int checkGridCell(GridVec gv) {
    return map.get(gv.y(), gv.x());
  }

  @Override
  public void setBlockPos(GridVec pos) {
    map.set(blockPos, 0);
    blockPos = pos;
    map.set(blockPos, 2);
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

  @Override
  public GridVec getBlockPos() {
    return blockPos;
  }
}
