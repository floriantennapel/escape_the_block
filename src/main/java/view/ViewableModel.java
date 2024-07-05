package view;

import model.vector.ImmutableVec2D;

public interface ViewableModel {
  int checkGridCell(int row, int col);

  ImmutableVec2D getPlayerPos();

  ImmutableVec2D getPlayerDir();

  ImmutableVec2D getViewPort();
}

