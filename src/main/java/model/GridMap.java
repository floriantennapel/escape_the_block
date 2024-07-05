package model;

import model.vector.GridVec;

import java.util.ArrayList;
import java.util.List;

public class GridMap {
  // chance of each grid cell being a wall when generating map
  private static final double WALL_SPAWN_RATE = 0.15;

  private final List<List<Integer>> gridMap;

  public GridMap(int mapSize, GridVec blockStartPos) {
    if (mapSize < 3 || mapSize > 1000) {
      throw new IllegalArgumentException("invalid map size");
    }

    gridMap = generateRandomMap(mapSize);
    gridMap.get(blockStartPos.y()).set(blockStartPos.x(), 2);
  }

  public int get(int row, int col) {
    if (row < 0 || row >= gridMap.size() || col < 0 || col >= gridMap.get(0).size()) {
      throw new IndexOutOfBoundsException();
    }

    return gridMap.get(row).get(col);
  }

  public void set(GridVec pos, int val) {
    if (pos == null) {
      throw new NullPointerException();
    }
    if (pos.y() < 0 || pos.y() >= gridMap.size() || pos.x() < 0 || pos.x() >= gridMap.get(0).size()) {
      throw new IndexOutOfBoundsException();
    }

    gridMap.get(pos.y()).set(pos.x(), val);
  }

  private List<List<Integer>> generateRandomMap(int mapSize) {
    List<List<Integer>> retList = new ArrayList<>(mapSize);

    for (int i = 0; i < mapSize; i++) {
      List<Integer> row = new ArrayList<>(mapSize);
      row.add(1);
      for (int j = 1; j < mapSize - 1; j++) {
        if (i == 0 || i == mapSize - 1 || Math.random() < WALL_SPAWN_RATE) {
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
