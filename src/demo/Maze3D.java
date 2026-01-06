package demo;
/*
Demo 3D mini Game using DDA (Digital Differential Analysis) algorithm for raycasting

build: ant compile

usage: java -cp classes demo.Maze3D

*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.*;

import java.util.Random;

import maze.Maze;
import maze.Cell;

public class Maze3D extends JFrame {
	private final static int MAZE_HEIGHT = 13;//must be odd
	private final static int MAZE_WIDTH = 11;//must be odd
	private final static Color floorColor = new Color(15, 15, 15);
	private final static Color ceilingColor = new Color(40, 40, 40);
    private final static double FOV = Math.PI / 3;
    private final static double depthOfField = 16;

    private final static double MiniMAP_x = 20;
    private final static double MiniMAP_y = 20;
    private final static double MiniMAP_size = 150;
    private final static int WIDTH = 900;
    private final static int HEIGHT = 600;

	private final Maze mymaze;
    private final int[][] maze;

	private double px, py; 
	private double playerAngle = 0;
    private int dirIndex = 1;          

	private boolean exitFound = false;

	private final int exitX, exitY;

    public Maze3D() {
		Random random = new Random();
//out door shall be placed on the enclosure wall
        exitX = 0;
        exitY = 1 + 2 * random.nextInt(MAZE_WIDTH / 2);
		
//out door shall be placed on the enclosure wall
		Cell out_door = new Cell(exitX, exitY);
		mymaze = new Maze(MAZE_HEIGHT, MAZE_WIDTH, out_door);
//inner cell is the typical place where a player is put at the beginning of a game
		Cell inner_cell = mymaze.getInnerCell();

        px = inner_cell.getX() + 0.5;
        py = inner_cell.getY() + 0.5;

		maze = mymaze.getMaze();
//adjust angle to avoid looking a wall
		if (maze[inner_cell.getX() + 1][inner_cell.getY()] == Maze.WALL) {
			if (maze[inner_cell.getX()][inner_cell.getY() + 1] == Maze.WALL) {
				if (maze[inner_cell.getX() - 1][inner_cell.getY()] == Maze.WALL) {
					playerAngle = Math.toRadians(270);
				} else playerAngle = Math.toRadians(180);
			} else playerAngle = Math.toRadians(90);
		}

        setTitle("3D Maze - Use arrows key to move player");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new GamePanel());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e.getKeyCode());
                repaint();
            }
        });
    }

	private void handleInput(int code) {
		double moveStep = 0.2;
		if (code == KeyEvent.VK_LEFT) { playerAngle -= Math.toRadians(5); return; }
		if (code == KeyEvent.VK_RIGHT) { playerAngle += Math.toRadians(5); return; }

		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
			if (code == KeyEvent.VK_DOWN)
				moveStep = -moveStep; 
			double nextX = px + Math.cos(playerAngle) * moveStep;
			double nextY = py + Math.sin(playerAngle) * moveStep;

			boolean collisionX = collisionDetection(nextX, py);
			boolean collisionY = collisionDetection(px, nextY);

			if (!collisionX && !collisionY) {
				px = nextX;
				py = nextY;
			} else if (collisionX && !collisionY) {
				py = nextY;
				if (code == KeyEvent.VK_UP) //in case of frontal collision change angle to avoid obstacle
					playerAngle = (Math.sin(playerAngle) > 0) ? Math.PI / 2 : 3 * Math.PI / 2;
			} else if (!collisionX && collisionY) {
				px = nextX;
				if (code == KeyEvent.VK_UP) //in case of frontal collision change angle to avoid obstacle
					playerAngle = (Math.cos(playerAngle) > 0) ? 0 : Math.PI;
			}
			checkExit();
		}
	}

    private void checkExit() {
        if ((int)px == exitX && (int)py == exitY && !exitFound) {
            exitFound = true;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Escaped!");
            });
        }
    }

    private boolean collisionDetection(double nx, double ny) {
        int x = (int)nx, y = (int)ny;
        if (y < 0 || y >= MAZE_WIDTH || x < 0 || x >= MAZE_HEIGHT) 
            return x != exitX || y != exitY;
        return maze[x][y] == Maze.WALL && (x != exitX || y != exitY);
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            draw3DView(g2);
            drawMiniMapOverlay(g2, MiniMAP_x, MiniMAP_y, MiniMAP_size);
        }

		private void draw3DView(Graphics2D g2) {
			int w = getWidth();
			int h = getHeight();

			double startAngle = playerAngle - FOV / 2;
			double stepAngle = FOV / w;

//	DDA (Digital Differential Analysis) algorithm for raycasting
			for (int i = 0; i < w; i++) {
				double rayAngle = startAngle + (double)i * stepAngle;
				double rayDirX = Math.cos(rayAngle);
				double rayDirY = Math.sin(rayAngle);   

				double deltaDistX = Math.abs(1 / rayDirX);
				double deltaDistY = Math.abs(1 / rayDirY);    
				int mapX = (int)px;
				int mapY = (int)py;
				double sideDistX, sideDistY;
				int stepX, stepY;    

				if (rayDirX < 0) {
					stepX = -1;
					sideDistX = (px - mapX) * deltaDistX;
				} else {
					stepX = 1;
					sideDistX = (mapX + 1.0 - px) * deltaDistX;
				}   
				if (rayDirY < 0) {
					stepY = -1;
					sideDistY = (py - mapY) * deltaDistY;
				} else {
					stepY = 1;
					sideDistY = (mapY + 1.0 - py) * deltaDistY;
				}    

				boolean hitExit = false;
				boolean sideHit = false;
				double distance = 0;

				while (distance < depthOfField) {
					if (sideDistX < sideDistY) {
						sideDistX += deltaDistX;
						mapX += stepX;
						distance = sideDistX - deltaDistX;
						sideHit = false;
					} else {
						sideDistY += deltaDistY;
						mapY += stepY;
						distance = sideDistY - deltaDistY;
						sideHit = true;
					}
					if (mapX == exitX && mapY == exitY) {
						hitExit = true;
						break;
					} else if (mapX < 0 || mapX >= MAZE_HEIGHT || mapY < 0 || mapY >= MAZE_WIDTH || maze[mapX][mapY] == Maze.WALL)
						break;
				}

				// Fish-eye correction
				double correctedDist = distance * Math.cos(rayAngle - playerAngle);
				double wallHeight = h / (correctedDist + 0.0001);
				int yTop = (int)((h - wallHeight) / 2.0);
				int yBottom = (int)((h + wallHeight) / 2.0);

				// Perform rendering
				g2.setColor(ceilingColor);
				g2.drawLine(i, 0, i, yTop);

				g2.setColor(floorColor);
				g2.drawLine(i, yBottom, i, h);

				float b = (float) Math.max(0, Math.min(1, 1.8 / (1 + distance * 0.6)));
				if (hitExit) {
					g2.setColor(new Color(0, 0, (int)(255 * b)));
				} else {
					int v = (int)(180 * b);
					// enhance contrast between lateral and frontal walls
					if (sideHit) v *= 0.7; 
					g2.setColor(new Color(v, v, v));
				}
				
				g2.drawLine(i, yTop, i, yBottom);

			}
		}

        private final Path2D.Double triangle = new Path2D.Double();
        private void drawMiniMapOverlay(Graphics2D g2, double ox, double oy, double size) {
            double s = size / Math.max(MAZE_HEIGHT, MAZE_WIDTH); 
            for(int y = 0; y < MAZE_WIDTH; y++) {
                for(int x = 0; x < MAZE_HEIGHT; x++) {
                    g2.setColor(x == exitX && y == exitY ? Color.BLUE : (maze[x][y] == Maze.WALL ? Color.DARK_GRAY : Color.LIGHT_GRAY));
					g2.fillRect((int)(x * s + ox), (int)(y * s + oy), (int)s - 1, (int)s - 1);
                }
            }
            g2.setColor(Color.RED);
            double centerX = (px * s) + ox;
            double centerY = (py * s) + oy;
			double a = s * 0.6;
			double b = s * 0.4;

			double cos = Math.cos(playerAngle), sin = Math.sin(playerAngle);
			triangle.reset();
			triangle.moveTo(centerX + a * cos, centerY + a * sin);
			triangle.lineTo(centerX - b * (cos + sin), centerY + b * (cos - sin));
			triangle.lineTo(centerX + b * (sin - cos), centerY - b * (cos + sin));
			triangle.closePath();
            g2.fill(triangle);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Maze3D().setVisible(true));
    }
}