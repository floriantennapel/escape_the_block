import controller.BlockController;
import controller.KeyController;
import model.Model;
import view.View;

import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    var model = new Model(30);
    var view = new View(model);
    new KeyController(model, view);
    new BlockController(model, view);

    JFrame frame = new JFrame("escape the block");
    frame.setContentPane(view);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    model.setStartTime();
  }
}
