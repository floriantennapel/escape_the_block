package controller;

import model.vector.GridVec;
import model.vector.Vec2D;

public interface ControllableModel {
  Vec2D getPos();

  void setPlayerPos(Vec2D pos);

  void generateNewMap();

  Vec2D getDir();

  void rotatePlayerDir(double theta);

  GridVec getBlockPos();

  int checkGridCell(GridVec pos);

  void setBlockPos(GridVec pos);
}
