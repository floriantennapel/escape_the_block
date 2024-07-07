package view;

import model.vector.GridVec;
import model.vector.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class View extends JPanel {
  private static final Map<Integer, Color> cellCodeToColor = Map.of(
      1, Color.GRAY,
      2, Color.YELLOW
  );

  private final ViewableModel model;
  private Color blockColor = new Color(255, 255, 0);

  public View(ViewableModel model) {
    this.model = model;
    this.setPreferredSize(new Dimension(800, 600));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    switch (model.getGameState()) {
      case ACTIVE -> paintWorld(g2);
      case GAME_OVER -> {
        paintWorld(g2);
        paintGameOver(g2);
      }
    }
  }

  /**
   * change color of "the block"
   */
  public void setBlockColor(Color blockColor) {
    this.blockColor = blockColor;
  }

  private void paintWorld(Graphics2D g2) {
    int winHeight = getHeight();
    int winWidth = getWidth();

    Rectangle2D sky = new Rectangle2D.Double(0., 0., winWidth, winHeight / 2.);
    g2.setColor(new Color(240, 255, 255));
    g2.fill(sky);

    Rectangle2D floor = new Rectangle2D.Double(0., winHeight / 2., winWidth, winHeight / 2.);
    g2.setColor(new Color(100, 200, 150));
    g2.fill(floor);

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

  private void paintGameOver(Graphics2D g2) {
    int width = getWidth();
    int height = getHeight();

    Rectangle2D foreground = new Rectangle2D.Double(0, 0, width, height);
    g2.setColor(new Color(150, 0, 0, 170));
    g2.fill(foreground);

    g2.setFont(new Font("Arial", Font.BOLD, getHeight() / 10));
    g2.setColor(new Color(25, 25, 25));
    drawVertCenteredString(g2, "GAME OVER", width / 2, height / 2);

    g2.setFont(new Font("Arial", Font.BOLD, getHeight() / 20));
    drawVertCenteredString(g2, "YOU STAYED ALIVE FOR " + model.getScore() + " SECONDS", width / 2, (int) (height * 0.75));
  }

  private void drawVertCenteredString(Graphics2D g2, String text, int x, int y) {
    int stringWidth = g2.getFontMetrics().stringWidth(text);
    g2.drawString(text, x - stringWidth / 2, y);
  }

  private List<Vec2D> generateRays() {
    int winWidth = getWidth();
    Vec2D vp = model.getViewport();
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
    Color color;

    int cellValue = model.checkGridCell(mapPos);
    if (cellValue == 2) {
      color = blockColor;
    } else {
      color = cellCodeToColor.get(cellValue);
    }
    if (wallDirIsX) {
      color = color.darker();
    }

    return new RayInfo(rayLength, color);
  }

  private record RayInfo(double distance, Color color) {}
}
