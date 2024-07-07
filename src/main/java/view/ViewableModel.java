package view;

import model.GameState;
import model.vector.GridVec;
import model.vector.Vec2D;

public interface ViewableModel {
  /**
   * get value stored at given index
   */
  int checkGridCell(GridVec pos) throws IndexOutOfBoundsException;

  /**
   * @return current position of player
   */
  Vec2D getPlayerPos();

  /**
   * @return current direction vector of the player
   */
  Vec2D getPlayerDir();

  /**
   * Get the current viewport vector of the player
   * <br/>
   * Vector perpendicular to direction, corresponding to right edge of screen,
   * rays should be made ranging from direction + -viewport to direction + viewport
   */
  Vec2D getViewport();

  /**
   * get the current game state
   */
  GameState getGameState();
}

