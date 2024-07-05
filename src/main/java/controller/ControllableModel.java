package controller;

import model.vector.Matrix;
import model.vector.Vec2D;
import model.vector.ImmutableVec2D;

public interface ControllableModel {
  Vec2D getPos();

  ImmutableVec2D getDir();

  void rotatePlayerDir(Matrix rotMat);
}
