package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import model.vector.GridVec;
import model.vector.Matrix;
import model.vector.Vec2D;
import view.View;

import javax.swing.*;

public class KeyController implements KeyListener, ActionListener {
  private final static double MOVE_AMOUNT = 0.05;
  private final static Map<Direction, Matrix> MOVE_MATRICES = Map.of(
      Direction.FRONT, Matrix.newScalarMat(1),
      Direction.BACK, Matrix.newScalarMat(-1),
      Direction.LEFT, Matrix.newRotMat(Math.PI / 2.),
      Direction.RIGHT, Matrix.newRotMat(Math.PI / -2.),
      Direction.ROT_LEFT, Matrix.newRotMat(MOVE_AMOUNT),
      Direction.ROT_RIGHT, Matrix.newRotMat(-MOVE_AMOUNT)
  );

  private final Map<Direction, Boolean> moving;
  private final ControllableModel model;
  private final View view;
  private final Vec2D pos;
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
    pos = model.getPos();

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
    Vec2D toMove = new Vec2D(0, 0);

    boolean isMoving = false;
    for (var dir : Direction.values()) {
      if (moving.get(dir)) {
        if (dir != Direction.ROT_LEFT && dir != Direction.ROT_RIGHT) {
          Vec2D moveDir = new Vec2D(model.getDir());
          moveDir.transform(MOVE_MATRICES.get(dir));
          toMove.add(moveDir);
          isMoving = true;
        } else {
          model.rotatePlayerDir(MOVE_MATRICES.get(dir));
        }
      }
    }

    if (isMoving) {
      toMove.scale(MOVE_AMOUNT / toMove.length());
      pos.add(toMove);

      // bounds checking
      if (model.checkGridCell(new GridVec(pos)) != 0) {
        toMove.scale(-1);
        pos.add(toMove);
      }
    }

    view.repaint();
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) { /* unused method */ }

  private enum Direction {
    FRONT, BACK, LEFT, RIGHT, ROT_LEFT, ROT_RIGHT
  }
}
