package model;

import model.vector.GridVec;

import java.util.*;

public class GridMap {
  public static int MIN_MAP_SIZE = 10;
  public static int MAX_MAP_SIZE = 1000;

  // chance of each grid cell being a wall when generating map
  private static final double WALL_SPAWN_RATE = 0.3;

  private static final List<GridVec> MOVE_DIRECTIONS = List.of(
      new GridVec(0, 1),
      new GridVec(0, -1),
      new GridVec(1, 0),
      new GridVec(-1, 0)
  );

  private List<List<Integer>> gridMap;
  private final int rows;
  private final int cols;

  /**
   * @param mapSize must be in range {@code MIN_MAP_SIZE} to {@code MAX_MAP_SIZE}
   */
  GridMap(int mapSize, GridVec blockPos, GridVec playerPos) {
    if (mapSize < MIN_MAP_SIZE || mapSize > MAX_MAP_SIZE) {
      throw new IllegalArgumentException("invalid map size");
    }
    if (blockPos == null || playerPos == null) {
      throw new NullPointerException();
    }

    rows = mapSize;
    cols = mapSize;

    if (!validPos(blockPos)) {
      throw new IllegalArgumentException("invalid blockPos");
    } else if (!validPos(playerPos)) {
      throw new IllegalArgumentException("invalid playerPos");
    }

    var reserved = new ArrayList<GridVec>();
    reserved.add(blockPos);

    // keeping starting area clear of blocks
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        reserved.add(GridVec.add(playerPos, new GridVec(j, i)));
      }
    }

    do {
      gridMap = generateRandomMap(mapSize, reserved);
      gridMap.get(blockPos.y()).set(blockPos.x(), 2);
    } while (findPath(blockPos, playerPos) == null);
  }

  /**
   * checks if position is on gridMap
   */
  boolean validPos(GridVec pos) {
    if (pos == null) {
      throw new NullPointerException();
    }

    return pos.x() >= 0 && pos.x() < cols && pos.y() >= 0 && pos.y() < rows;
  }

  /**
   * @return value stored at given position
   */
  int get(GridVec pos) throws IndexOutOfBoundsException {
    if (pos == null) {
      throw new NullPointerException();
    }
    if (!validPos(pos)) {
      throw new IndexOutOfBoundsException();
    }

    return gridMap.get(pos.y()).get(pos.x());
  }

  /**
   * Set value at given position
   * @param val must be in range 0 to 99
   */
  void set(GridVec pos, int val) throws IndexOutOfBoundsException {
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

  /**
   * A* algorithm
   * approx. dist. == steps taken + Euclidean dist. to player
   * @return the first step in the path
   */
  GridVec findPath(GridVec from, GridVec to) {
    Queue<QueueElem> queue = new PriorityQueue<>();
    queue.add(new QueueElem(from, null, 0, from.distance(to)));
    Set<GridVec> visited = new HashSet<>();

    while (!queue.isEmpty()) {
      var current = queue.poll();
      if (visited.contains(current.pos)) {
        continue;
      }
      visited.add(current.pos);

      if (current.pos.equals(to)) {
        var step = getFirstStep(current);
        if (step == null) {
          return from;
        }
        return step;
      }

      addNextMoves(queue, current, to);
    }

    System.err.println("No path found");
    return null;
  }

  private void addNextMoves(Queue<QueueElem> queue, QueueElem current, GridVec playerPos) {
    for (var dir : MOVE_DIRECTIONS) {
      var nextBlock = GridVec.add(current.pos, dir);
      if (validPos(nextBlock) && get(nextBlock) == 0) {
        queue.add(new QueueElem(nextBlock, current, current.steps + 1, nextBlock.distance(playerPos)));
      }
    }
  }

  private GridVec getFirstStep(QueueElem qe) {
    var current = qe;
    GridVec after = null;

    while (current.prev != null) {
      after = current.pos;
      current = current.prev;
    }

    return after;
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

  private record QueueElem(GridVec pos, QueueElem prev, int steps, double heuristic) implements Comparable<QueueElem> {
    @Override
    public int compareTo(QueueElem o) {
      if (o == null) {
        throw new NullPointerException();
      }

      return Double.compare(steps + heuristic, o.steps + o.heuristic);
    }
  }
}
