package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.vector.GridVec;
import model.vector.Vec2D;
import view.View;

import javax.swing.*;

public class KeyController implements KeyListener, ActionListener {
  private final static double BOUNDING_RADIUS = 0.3;
  private final static double MOVE_AMOUNT = 0.05;
  private final static Map<Direction, Double> MOVE_ANGLES = Map.of(
      Direction.FRONT, 0.,
      Direction.BACK, Math.PI,
      Direction.LEFT, Math.PI / 2.,
      Direction.RIGHT, - Math.PI / 2.,
      Direction.ROT_LEFT, MOVE_AMOUNT,
      Direction.ROT_RIGHT, -MOVE_AMOUNT
  );

  private final Map<Direction, Boolean> moving;
  private final ControllableModel model;
  private final View view;
  private final Timer timer;

  public KeyController(ControllableModel model, View view) {
    moving = new HashMap<>(Direction.values().length);
    for (var dir : Direction.values()) {
      moving.put(dir, false);
    }

    view.setFocusable(true);
    view.addKeyListener(this);

    this.model = model;
    this.view = view;

    timer = new Timer(1000 / 60, this);
    timer.start();
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    switch (keyEvent.getKeyCode()) {
      case KeyEvent.VK_W     -> moving.put(Direction.FRONT, true);
      case KeyEvent.VK_S     -> moving.put(Direction.BACK, true);
      case KeyEvent.VK_A     -> moving.put(Direction.LEFT, true);
      case KeyEvent.VK_D     -> moving.put(Direction.RIGHT, true);
      case KeyEvent.VK_LEFT  -> moving.put(Direction.ROT_LEFT, true);
      case KeyEvent.VK_RIGHT -> moving.put(Direction.ROT_RIGHT, true);
    }
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    switch (keyEvent.getKeyCode()) {
      case KeyEvent.VK_W     -> moving.put(Direction.FRONT, false);
      case KeyEvent.VK_S     -> moving.put(Direction.BACK, false);
      case KeyEvent.VK_A     -> moving.put(Direction.LEFT, false);
      case KeyEvent.VK_D     -> moving.put(Direction.RIGHT, false);
      case KeyEvent.VK_LEFT  -> moving.put(Direction.ROT_LEFT, false);
      case KeyEvent.VK_RIGHT -> moving.put(Direction.ROT_RIGHT, false);
    }
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    switch (model.getGameState()) {
      case ACTIVE -> activeGameController();
      case GAME_OVER -> {}
      case START_MENU -> {}
    }

    view.repaint();
  }

  private void activeGameController() {
    Vec2D currentPos = model.getPlayerPos();
    Vec2D toMove = new Vec2D(0, 0);

    boolean isMoving = false;
    for (var dir : Direction.values()) {
      if (moving.get(dir)) {
        if (dir != Direction.ROT_LEFT && dir != Direction.ROT_RIGHT) {
          Vec2D moveDir = model.getPlayerDir();
          moveDir = moveDir.rotate(MOVE_ANGLES.get(dir));
          toMove = Vec2D.add(toMove, moveDir);
          isMoving = true;
        } else {
          model.rotatePlayerDir(MOVE_ANGLES.get(dir));
        }
      }
    }

    if (isMoving) {
      toMove = toMove.scale(MOVE_AMOUNT / toMove.length());
      var nextPos = Vec2D.add(currentPos, toMove);

      // bounds checking
      if (!isValidPlayerPos(nextPos)) {
        toMove = wallSlide(currentPos, toMove);
        if (toMove == null) {
          return;
        }
        nextPos = Vec2D.add(currentPos, toMove);
      }

      model.setPlayerPos(nextPos);

      if (model.checkGridCell(new GridVec(nextPos)) == 2) {
        model.setGameOver();
      }
    }
  }

  private Vec2D wallSlide(Vec2D pos, Vec2D dir) {
    Vec2D rotLeft = dir;
    Vec2D rotRight = dir;

    double rotAmount = 0.1;

    double cos = cosAngleDiff(dir, rotLeft);
    while (cos > 0.25) {
      rotLeft = rotLeft.rotate(rotAmount);
      rotRight = rotRight.rotate(-rotAmount);

      cos = cosAngleDiff(dir, rotLeft);
      double scalar = cos * MOVE_AMOUNT / rotLeft.length();
      rotLeft = rotLeft.scale(scalar);
      rotRight = rotRight.scale(scalar);

      if (isValidPlayerPos(Vec2D.add(pos, rotLeft))) {
        return rotLeft;
      } else if (isValidPlayerPos(Vec2D.add(pos, rotRight))) {
        return rotRight;
      }
    }

    return null;
  }

  private static double cosAngleDiff(Vec2D a, Vec2D b) {
    return Vec2D.dotProduct(a, b) / a.length() / b.length();
  }

  private boolean isValidPlayerPos(Vec2D pos) {
    var directions = List.of(
        new Vec2D(BOUNDING_RADIUS, 0),
        new Vec2D(-BOUNDING_RADIUS, 0),
        new Vec2D(0, BOUNDING_RADIUS),
        new Vec2D(0, -BOUNDING_RADIUS)
    );

    for (var dir : directions) {
      if (model.checkGridCell(new GridVec(Vec2D.add(pos, dir))) == 1) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) { /* unused method */ }

  private enum Direction {
    FRONT, BACK, LEFT, RIGHT, ROT_LEFT, ROT_RIGHT
  }
}
