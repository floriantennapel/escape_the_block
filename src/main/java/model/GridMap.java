package model;

import model.vector.GridVec;

import java.util.ArrayList;
import java.util.List;

public class GridMap {
  // chance of each grid cell being a wall when generating map
  private static final double WALL_SPAWN_RATE = 0.2;

  private final List<List<Integer>> gridMap;
  private final int rows;
  private final int cols;

  public GridMap(int mapSize, GridVec blockPos, GridVec playerPos) {
    if (mapSize < 3 || mapSize > 1000) {
      throw new IllegalArgumentException("invalid map size");
    }

    rows = mapSize;
    cols = mapSize;

    var reserved = new ArrayList<GridVec>();
    reserved.add(blockPos);

    // keeping starting area clear of blocks
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        reserved.add(GridVec.add(playerPos, new GridVec(j, i)));
      }
    }

    gridMap = generateRandomMap(mapSize, reserved);
    gridMap.get(blockPos.y()).set(blockPos.x(), 2);
  }

  /**
   * checks if position is on gridMap
   */
  public boolean validPos(GridVec pos) {
    return pos.x() >= 0 && pos.x() < cols && pos.y() >= 0 && pos.y() < rows;
  }

  /**
   * @return value stored at given position
   */
  public int get(GridVec pos) throws IndexOutOfBoundsException {
    if (!validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    return gridMap.get(pos.y()).get(pos.x());
  }

  /**
   * Set value at given position
   * @param val must be in range 0 to 99
   */
  public void set(GridVec pos, int val) throws IndexOutOfBoundsException {
    if (pos == null) {
      throw new NullPointerException();
    }
    if (val < 0 || val > 100) {
      throw new IllegalArgumentException("Invalid cell value");
    }
    if (!validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    gridMap.get(pos.y()).set(pos.x(), val);
  }

  private List<List<Integer>> generateRandomMap(int mapSize, List<GridVec> reserved) {
    List<List<Integer>> retList = new ArrayList<>(mapSize);

    for (int i = 0; i < mapSize; i++) {
      List<Integer> row = new ArrayList<>(mapSize);
      row.add(1);
      for (int j = 1; j < mapSize - 1; j++) {
        if (i == 0 || i == mapSize - 1) {
          row.add(1);
        } else if (Math.random() < WALL_SPAWN_RATE && !occupied(i, j, reserved)) {
          row.add(1);
        } else {
          row.add(0);
        }
      }
      row.add(1);

      retList.add(row);
    }
    return retList;
  }

  private boolean occupied(int r, int c, List<GridVec> reserved) {
    for (var p : reserved) {
      if (p.x() == c && p.y() == r) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String toString() {
    var s = new StringBuilder();

    for (var r : gridMap) {
      for (var d : r) {
        s.append(d != 0 ? d : ".");
      }
      s.append('\n');
    }

    return s.substring(0, s.length() - 1);
  }
}
