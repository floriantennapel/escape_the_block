package view;

import model.vector.GridVec;
import model.vector.Vec2D;

public interface ViewableModel {
  int checkGridCell(GridVec pos);

  Vec2D getPlayerPos();

  Vec2D getPlayerDir();

  Vec2D getViewPort();
}

