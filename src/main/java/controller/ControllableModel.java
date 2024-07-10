package controller;

import model.GameState;
import model.vector.GridVec;
import model.vector.Vec2D;

public interface ControllableModel {

  /**
   * put player at given position
   */
  void setPlayerPos(Vec2D pos) throws IndexOutOfBoundsException;

  /**
   * @return current player position
   */
  Vec2D getPlayerPos();

  /**
   * @return direction vector of player
   */
  Vec2D getPlayerDir();

  /**
   * Rotate player by given angle
   * @param theta angle in radians
   */
  void rotatePlayerDir(double theta);

  /**
   * @return current position of "the block"
   */
  GridVec getBlockPos();

  /**
   * @return value stored at given grid position
   */
  int checkGridCell(GridVec pos) throws IndexOutOfBoundsException;

  /**
   * move "the block" to the given position
   */
  void setBlockPos(GridVec pos) throws IndexOutOfBoundsException;

  /**
   * get the current game state
   */
  GameState getGameState();

  /**
   * set current game state
   */
  void setGameOver();

  /**
   * number of milliseconds since the game was started
   */
  long timeSinceStart();

  /**
   * set gameState to active and reset all need attributes for new game
   */
  void startNewGame();

  /**
   * Find first step block must take to reach player
   */
  GridVec findBlockPath();
}
