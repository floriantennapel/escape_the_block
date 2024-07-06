package view;

import model.vector.GridVec;
import model.vector.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class View extends JPanel {
  private static final Map<Integer, Color> cellCodeToColor = Map.of(
      1, Color.GRAY,
      2, Color.YELLOW
  );

  private final ViewableModel model;

  public View(ViewableModel model) {
    this.model = model;
    this.setPreferredSize(new Dimension(800, 600));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    paintWorld(g2);
  }

  private void paintWorld(Graphics2D g2) {
    int winHeight = getHeight();
    int winWidth = getWidth();

    var rays = generateRays();
    for (int i = 0; i < winWidth; i++) {
      var rayInfo = castRay(rays.get(i));
      double lineHeight = winHeight / rayInfo.distance();
      double y1 = (winHeight + lineHeight) / 2.;
      Line2D line = new Line2D.Double(i, y1, i, y1 - lineHeight);
      g2.setColor(rayInfo.color());
      g2.draw(line);
    }
  }

  private List<Vec2D> generateRays() {
    int winWidth = getWidth();
    Vec2D vp = model.getViewPort();
    Vec2D dir = model.getPlayerDir();

    List<Vec2D> rays = new ArrayList<>(winWidth);

    for (int i = 0; i < winWidth; i++) {
      double vpScalar = 2. * i / winWidth - 1;
      var ray = Vec2D.add(dir, vp.scale(vpScalar));

      rays.add(ray);
    }

    return rays;
  }

  /**
   * DDA algorithm, based on this <a href="https://lodev.org/cgtutor/raycasting.html">article</a>
   * by Lode Vandevenne
   */
  private RayInfo castRay(Vec2D rayDir) {
    var playerPos = model.getPlayerPos();

    var mapPos = new GridVec(playerPos);

    // 1. / 0 == Double.INFINITY which is the desired behaviour
    double deltaDistX = Math.abs(1. / rayDir.x());
    double deltaDistY = Math.abs(1. / rayDir.y());

    double totalDistX = rayDir.x() < 0 ? (playerPos.x() - mapPos.x()) * deltaDistX : (mapPos.x() + 1 - playerPos.x()) * deltaDistX;
    double totalDistY = rayDir.y() < 0 ? (playerPos.y() - mapPos.y()) * deltaDistY : (mapPos.y() + 1 - playerPos.y()) * deltaDistY;

    var stepX = new GridVec(rayDir.x() < 0 ? -1 : 1, 0);
    var stepY = new GridVec(0, rayDir.y() < 0 ? -1 : 1);

    boolean wallDirIsX = false; // only initialized for compiler
    boolean hitWall = false;
    while (!hitWall) {
      if (totalDistX < totalDistY) {
        totalDistX += deltaDistX;
        mapPos = GridVec.add(mapPos, stepX);
        wallDirIsX = true;
      } else {
        totalDistY += deltaDistY;
        mapPos = GridVec.add(mapPos, stepY);
        wallDirIsX = false;
      }

      hitWall = model.checkGridCell(mapPos) != 0;
    }

    double rayLength = wallDirIsX ? totalDistX - deltaDistX : totalDistY - deltaDistY;
    Color color = cellCodeToColor.get(model.checkGridCell(mapPos));
    if (wallDirIsX) {
      color = color.darker();
    }

    return new RayInfo(rayLength, color);
  }

  private record RayInfo(double distance, Color color) {}
}
