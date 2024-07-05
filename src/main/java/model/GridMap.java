package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridMap {
  // chance of each grid cell being a wall when generating map
  private static final double WALL_SPAWN_RATE = 0.15;

  private final List<List<Integer>> gridMap;

  public GridMap(int mapSize) {
    if (mapSize < 3 || mapSize > 1000) {
      throw new IllegalArgumentException("invalid map size");
    }

    gridMap = generateRandomMap(mapSize);
  }

  public int get(int row, int col) {
    if (row < 0 || row >= gridMap.size() || col < 0 || col >= gridMap.get(0).size()) {
      throw new IndexOutOfBoundsException();
    }

    return gridMap.get(row).get(col);
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

    // placing "THE BLOCK"
    Random r = new Random();
    retList.get(r.nextInt(1, retList.size() - 1)).set(r.nextInt(1, retList.get(0).size() - 1), 2);
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
