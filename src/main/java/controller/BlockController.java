package controller;

import model.GameState;
import model.vector.GridVec;
import view.View;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlockController implements ActionListener {
  private static final int MIN_BLOCK_DELAY = 300; // max speed, time between each block movement in ms
  private static final int MAX_BLOCK_DELAY = 1500; // min speed, time between each block movement in ms
  private static final int TIME_AT_MAX_SPEED = 30; // seconds it takes block to reach max speed

  private final ControllableModel model;
  private final View view;
  private final Timer timer;

  public BlockController(ControllableModel model, View view) {
    if (model == null || view == null) {
      throw new NullPointerException();
    }

    this.model = model;
    this.view = view;

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

    GridVec path = model.findBlockPath();
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
}
