package view;

import model.vector.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
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

    int winHeight = getHeight();
    int winWidth = getWidth();

    BufferedImage bufferedImage = new BufferedImage(winWidth, winHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D buffer = bufferedImage.createGraphics();

    List<ImmutableVec2D> rays = generateRays();
    for (int i = 0; i < winWidth; i++) {
      var rayInfo = castRay(rays.get(i));
      double lineHeight = winHeight / rayInfo.distance();
      double y1 = (winHeight + lineHeight) / 2.;
      Line2D line = new Line2D.Double(i, y1, i, y1 - lineHeight);
      buffer.setColor(rayInfo.color());
      buffer.draw(line);
    }

    buffer.dispose();
    g2.drawImage(bufferedImage, 0, 0, null);
  }

  private List<ImmutableVec2D> generateRays() {
    int winWidth = getWidth();
    ImmutableVec2D vp = model.getViewPort();

    List<ImmutableVec2D> rays = new ArrayList<>(winWidth);

    for (int i = 0; i < winWidth; i++) {
      double vpScalar = 2. * i / winWidth - 1;
      Vec2D ray = new Vec2D(model.getPlayerDir());
      ray.add(Vec2D.scaled(vp, vpScalar));

      rays.add(ray);
    }

    return rays;
  }

  /**
   * DDA algorithm, based on this <a href="https://lodev.org/cgtutor/raycasting.html">article</a>
   * by Lode Vandevenne
   */
  private RayInfo castRay(ImmutableVec2D rayDir) {
    double rayX = rayDir.get(0);
    double rayY = rayDir.get(1);
    double playerX = model.getPlayerPos().get(0);
    double playerY = model.getPlayerPos().get(1);

    int mapX = (int) playerX;
    int mapY = (int) playerY;

    // 1. / 0 == Double.INFINITY which is the desired behaviour
    double deltaDistX = Math.abs(1. / rayX);
    double deltaDistY = Math.abs(1. / rayY);

    double totalDistX = rayX < 0 ? (playerX - mapX) * deltaDistX : (mapX + 1 - playerX) * deltaDistX;
    double totalDistY = rayY < 0 ? (playerY - mapY) * deltaDistY : (mapY + 1 - playerY) * deltaDistY;
    int stepX = rayX < 0 ? -1 : 1;
    int stepY = rayY < 0 ? -1 : 1;

    boolean wallDirIsX = false; // only initialized for compiler
    boolean hitWall = false;
    while (!hitWall) {
      if (totalDistX < totalDistY) {
        totalDistX += deltaDistX;
        mapX += stepX;
        wallDirIsX = true;
      } else {
        totalDistY += deltaDistY;
        mapY += stepY;
        wallDirIsX = false;
      }

      hitWall = model.checkGridCell(mapY, mapX) != 0;
    }

    double rayLength = wallDirIsX ? totalDistX - deltaDistX : totalDistY - deltaDistY;
    Color color = cellCodeToColor.get(model.checkGridCell(mapY, mapX));
    if (wallDirIsX) {
      color = color.darker();
    }

    return new RayInfo(rayLength, color);
  }

  private record RayInfo(double distance, Color color) {}
}
