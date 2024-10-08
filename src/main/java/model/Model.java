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

  private GridMap map;
  private final int mapSize;

  private int score;

  /**
   * @param mapSize must be in range {@code MIN_MAP_SIZE} to {@code MAX_MAP_SIZE}
   */
  public Model(int mapSize) {
    if (mapSize < GridMap.MIN_MAP_SIZE || mapSize > GridMap.MAX_MAP_SIZE) {
      throw new IllegalArgumentException("Invalid map size");
    }

    this.mapSize = mapSize;
    startNewGame();
  }

  @Override
  public int checkGridCell(GridVec pos) throws IndexOutOfBoundsException {
    if (pos == null) {
      throw new NullPointerException();
    }
    if (!map.validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    return map.get(pos);
  }

  @Override
  public void setBlockPos(GridVec pos) throws IndexOutOfBoundsException {
    if (pos == null) {
      throw new NullPointerException();
    }
    if (!map.validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    map.set(blockPos, 0);
    blockPos = pos;
    map.set(blockPos, 2);
  }

  @Override
  public Vec2D getPlayerPos() {
    return playerPos;
  }

  @Override
  public void setPlayerPos(Vec2D pos) throws IndexOutOfBoundsException {
    if (pos == null) {
      throw new NullPointerException();
    }

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
  public GridVec findBlockPath() {
    return map.findPath(blockPos, new GridVec(playerPos));
  }

  @Override
  public void startNewGame() {
    playerDir = new Vec2D(0, 1);
    viewPort = new Vec2D(0.7, 0);

    var rand = new Random();

    playerPos = new Vec2D(rand.nextInt(2, mapSize - 2) + 0.5, rand.nextInt(2, mapSize - 2) + 0.5);
    var discretePlayerPos = new GridVec(playerPos);

    // making sure block is not spawned too close nor too far from player
    int minStartDist = 5;
    int maxStartDist = 10;
    do {
      blockPos = new GridVec(rand.nextInt(1, mapSize - 1), rand.nextInt(1, mapSize - 1));
    } while (blockPos.distance(discretePlayerPos) < minStartDist || blockPos.distance(discretePlayerPos) > maxStartDist);

    map = new GridMap(mapSize, blockPos, discretePlayerPos);
    gameState = GameState.ACTIVE;

    gameStart = System.currentTimeMillis();
  }

  @Override
  public int getScore() {
    return score;
  }
}
