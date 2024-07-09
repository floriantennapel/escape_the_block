package controller;

import model.GameState;
import model.vector.GridVec;
import view.View;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class BlockController implements ActionListener {
  private static final int MIN_BLOCK_DELAY = 350; // max speed, time between each block movement in ms
  private static final int MAX_BLOCK_DELAY = 1500; // min speed, time between each block movement in ms
  private static final int TIME_AT_MAX_SPEED = 180; // seconds it takes block to reach max speed

  private static final List<GridVec> MOVE_DIRECTIONS = List.of(
      new GridVec(0, 1),
      new GridVec(0, -1),
      new GridVec(1, 0),
      new GridVec(-1, 0)
  );

  private final ControllableModel model;
  private final View view;
  private final Timer timer;

  public BlockController(ControllableModel model, View view) {
    this.model = model;
    this.view = view;

    // checking if player is reachable
    while (findPath() == null) {
      model.generateNewMap();
    }

    timer = new Timer(MAX_BLOCK_DELAY, this);
    timer.start();
  }

  private void setTimerDelay() {
    int sinceStart = (int) (model.timeSinceStart() / 1000L);

    int delay = Math.max(-sinceStart * (MAX_BLOCK_DELAY - MIN_BLOCK_DELAY) / TIME_AT_MAX_SPEED + MAX_BLOCK_DELAY, MIN_BLOCK_DELAY);
    timer.setDelay(delay);

    // making block progressively more red as speed increases
    int green = Math.max(255 - (int) (255 * ((double) sinceStart / TIME_AT_MAX_SPEED)), 0);
    view.setBlockColor(new Color(255, green, 0));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (model.getGameState() != GameState.ACTIVE) {
      return;
    }

    GridVec path = findPath();
    if (path == null) {
      path = model.getBlockPos();
    }
    model.setBlockPos(path);
    if (path.equals(new GridVec(model.getPlayerPos()))) {
      model.setGameOver();
    }

    setTimerDelay();
    view.repaint();
  }

  /**
   * A* algorithm
   * approx. dist. == steps taken + Euclidean dist. to player
   * @return the first step in the path
   */
  private GridVec findPath() {
    var playerPos = new GridVec(model.getPlayerPos());
    var blockPos = model.getBlockPos();

    Queue<QueueElem> queue = new PriorityQueue<>();
    queue.add(new QueueElem(blockPos, null, 0, blockPos.distance(playerPos)));
    Set<GridVec> visited = new HashSet<>();

    while (!queue.isEmpty()) {
      var current = queue.poll();
      if (visited.contains(current.pos)) {
        continue;
      }
      visited.add(current.pos);

      if (current.pos.equals(playerPos)) {
        var step = getFirstStep(current);
        if (step == null) {
          return model.getBlockPos();
        }
        return step;
      }

      addNextMoves(queue, current, playerPos);
    }

    System.err.println("No path found");
    return null;
  }

  private void addNextMoves(Queue<QueueElem> queue, QueueElem current, GridVec playerPos) {
    for (var dir : MOVE_DIRECTIONS) {
      var nextBlock = GridVec.add(current.pos, dir);
      if (model.isValidPos(nextBlock) && model.checkGridCell(nextBlock) == 0) {
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
