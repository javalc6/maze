package demo;
/*play with mazes without using recursive functions

version 1.0.1, 03-01-2026, interactive demo game

build: ant compile

usage: java -cp classes demo.MazeGame

*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import maze.Maze;
import maze.Cell;

public class MazeGame extends JFrame {
	private final int MAZE_WIDTH = 29;//must be odd
	private final int MAZE_HEIGHT = 21;//must be odd
    private final int TILE_SIZE = 30;

    private Maze mymaze;
    private int playerX, playerY;
    private int exitX, exitY;

	private javax.swing.Timer hintFadeTimer;

    public MazeGame() {
		mymaze = generateMaze(MAZE_WIDTH, MAZE_HEIGHT);

        setTitle("Escape! " + MAZE_WIDTH + " x " + MAZE_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MazePanel mazePanel = new MazePanel();
        add(mazePanel);

        JPanel buttonPanel = new JPanel();
        
        JButton solveBtn = new JButton("Auto-Solve");
        JButton hintBtn = new JButton("Show Hint");
        JButton clearBtn = new JButton("Clear Path");
        JButton newMazeBtn = new JButton("New Maze");

        solveBtn.setFocusable(false);
        hintBtn.setFocusable(false);
        clearBtn.setFocusable(false);
        newMazeBtn.setFocusable(false);

		solveBtn.addActionListener(e -> {
            if (hintFadeTimer != null) hintFadeTimer.stop();
            autoSolve(0);
        });
		hintBtn.addActionListener(e -> {
            autoSolve(3);
            startFadeTimer(3000);
        });
        clearBtn.addActionListener(e -> clearPath());
		newMazeBtn.addActionListener(e -> {
            mymaze = generateMaze(MAZE_WIDTH, MAZE_HEIGHT);
			mazePanel.repaint();
        });

        buttonPanel.add(hintBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(newMazeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int dx = 0, dy = 0;
                if (e.getKeyCode() == KeyEvent.VK_UP) dy = -1;
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) dy = 1;
                else if (e.getKeyCode() == KeyEvent.VK_LEFT) dx = -1;
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) dx = 1;

                movePlayer(dx, dy);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

	private Maze generateMaze(int MAZE_WIDTH, int MAZE_HEIGHT) {
		Random random = new Random();
//out door shall be placed on the enclosure wall
		Cell out_door = new Cell(0, 1 + 2 * random.nextInt(MAZE_HEIGHT / 2));
		Maze maze = new Maze(MAZE_WIDTH, MAZE_HEIGHT, out_door);
//inner cell is the typical place where a player is put at the beginning of a game
		Cell inner_cell = maze.getInnerCell();

        playerX = inner_cell.getX();
        playerY = inner_cell.getY();
        exitX = out_door.getX();
        exitY = out_door.getY();
		return maze;
	}

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (isValidMove(newX, newY)) {
            playerX = newX;
            playerY = newY;
            checkExit();
        }
        repaint();
    }

    private boolean isValidMove(int x, int y) {
		int[][] maze = mymaze.getMaze();
        return x >= 0 && x < maze.length && y >= 0 && y < maze[0].length && maze[x][y] != 1;
    }

    private void checkExit() {
        if (playerX == exitX && playerY == exitY) {
            JOptionPane.showMessageDialog(this, "You found the exit!");
        }
    }

	private void startFadeTimer(int delay) {
        if (hintFadeTimer != null) hintFadeTimer.stop();
        hintFadeTimer = new javax.swing.Timer(delay, e -> clearPath());
        hintFadeTimer.setRepeats(false);
        hintFadeTimer.start();
    }

    private void clearPath() {
		int[][] maze = mymaze.getMaze();
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 2) maze[i][j] = Maze.EMPTY;
            }
        }
        repaint();
    }

    private void autoSolve(int steps_to_show) {
		ArrayList<Cell> path = mymaze.findPathMaze(new Cell(playerX, playerY), new Cell(exitX, exitY));
		Collections.reverse(path); // From player to exit

		int[][] maze = mymaze.getMaze();
		for (Cell cell: path) {
			maze[cell.getX()][cell.getY()] = 2;
			if (steps_to_show != 0 && --steps_to_show == 0)
				break;
		}
		repaint();
    }

    class MazePanel extends JPanel {
		private int tile_size;
		private int oval_size, oval_offset;
    
		public MazePanel() {
			int max = Math.max(MAZE_WIDTH, MAZE_HEIGHT);
			if (max * TILE_SIZE > 900) {
				tile_size = 900 / max;
				oval_offset = 2;
				oval_size = tile_size - 4;
			} else {
				tile_size = TILE_SIZE;
				oval_offset = 4;
				oval_size = tile_size - 8;
			}
			int[][] maze = mymaze.getMaze();
            setPreferredSize(new Dimension(MAZE_WIDTH * tile_size, MAZE_HEIGHT * tile_size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);			

			int[][] maze = mymaze.getMaze();
			// Draw maze
            for (int y = 0; y < MAZE_HEIGHT; y++) {
                for (int x = 0; x < MAZE_WIDTH; x++) {
                    if (maze[x][y] == Maze.WALL) g.setColor(Color.BLACK);
                    else if (maze[x][y] == Maze.EMPTY) g.setColor(Color.WHITE);
                    else g.setColor(Color.GRAY);

                    g.fillRect(x * tile_size, y * tile_size, tile_size, tile_size);
//                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x * tile_size, y * tile_size, tile_size, tile_size);
                }
            }

            // Draw player
			g.setColor(Color.RED);
			g.fillOval(playerX * tile_size + oval_offset, playerY * tile_size + oval_offset, oval_size, oval_size);
            
            // Draw exit
            g.setColor(Color.GREEN);
            g.fillRect(exitX * tile_size + 2, exitY * tile_size + 2, tile_size - 4, tile_size - 4);
        }
    }

    public static void main(String[] args) {
		new MazeGame();
    }
}