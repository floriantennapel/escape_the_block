package model;

import controller.ControllableModel;
import model.vector.GridVec;
import model.vector.Vec2D;
import view.ViewableModel;

import java.util.Random;

public class Model implements ViewableModel, ControllableModel {
  private long gameStart;

  private GameState gameState;
  private Vec2D playerPos;
  private Vec2D playerDir;
  private Vec2D viewPort;
  private GridVec blockPos;

  // new map is generated if block cannot reach player
  private GridMap map;
  private final int mapSize;

  private int score;

  public Model(int mapSize) {
    if (mapSize < 3 || mapSize > 1000) {
      throw new IllegalArgumentException("invalid map size");
    }

    playerDir = new Vec2D(0, 1);
    viewPort = new Vec2D(0.7, 0);

    var rand = new Random();

    playerPos = new Vec2D(rand.nextInt(2, mapSize - 2) + 0.5, rand.nextInt(2, mapSize - 2) + 0.5);
    var discretePlayerPos = new GridVec(playerPos);

    // making sure block is not spawned too close to player
    int minStartDist = 10;
    int maxStartDist = 20;
    do {
      blockPos = new GridVec(rand.nextInt(1, mapSize - 1), rand.nextInt(1, mapSize - 1));
    } while (blockPos.distance(discretePlayerPos) < minStartDist && blockPos.distance(discretePlayerPos) > maxStartDist);

    map = new GridMap(mapSize, blockPos, discretePlayerPos);

    this.mapSize = mapSize;
    gameState = GameState.ACTIVE;
  }

  @Override
  public int checkGridCell(GridVec pos) throws IndexOutOfBoundsException {
    if (!map.validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    return map.get(pos);
  }

  @Override
  public void generateNewMap() {
    map = new GridMap(mapSize, blockPos, new GridVec(playerPos));
  }

  @Override
  public void setBlockPos(GridVec pos) throws IndexOutOfBoundsException {
    if (!map.validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    map.set(blockPos, 0);
    blockPos = pos;
    map.set(blockPos, 2);
  }

  @Override
  public boolean isValidPos(GridVec pos) {
    return map.validPos(pos);
  }

  @Override
  public Vec2D getPlayerPos() {
    return playerPos;
  }

  @Override
  public void setPlayerPos(Vec2D pos) throws IndexOutOfBoundsException {
    if (!map.validPos(new GridVec(pos))) {
      throw new IndexOutOfBoundsException();
    }

    playerPos = pos;
  }

  @Override
  public Vec2D getViewport() {
    return viewPort;
  }

  @Override
  public Vec2D getPlayerDir() {
    return playerDir;
  }

  @Override
  public void rotatePlayerDir(double theta) {
    playerDir = playerDir.rotate(theta);
    viewPort = viewPort.rotate(theta);
  }

  @Override
  public GridVec getBlockPos() {
    return blockPos;
  }

  @Override
  public GameState getGameState() {
    return gameState;
  }

  @Override
  public void setGameOver() {
    gameState = GameState.GAME_OVER;
    map.set(blockPos, 0);
    score = (int) (timeSinceStart() / 1000);
  }

  @Override
  public long timeSinceStart() {
    return System.currentTimeMillis() - gameStart;
  }

  @Override
  public int getScore() {
    return score;
  }

  /**
   * sets the start of the game to the current time, should be called when starting a new game
   */
  public void setStartTime() {
    gameStart = System.currentTimeMillis();
  }


}
