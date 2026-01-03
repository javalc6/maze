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
	private final int maze_width = 29;//must be odd
	private final int maze_height = 21;//must be odd

    private final int TILE_SIZE = 30;
    private Maze mymaze;
    private int playerX, playerY;
    private int exitX, exitY;
	private javax.swing.Timer hintFadeTimer;

    public MazeGame() {
		mymaze = generateMaze(maze_width, maze_height);

        setTitle("Escape!");
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
            mymaze = generateMaze(maze_width, maze_height);
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

	private Maze generateMaze(int maze_width, int maze_height) {
		Random random = new Random();
//out door shall be placed on the enclosure wall
		Cell out_door = new Cell(0, 1 + 2 * random.nextInt(maze_height / 2));
		Maze maze = new Maze(maze_width, maze_height, out_door);
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
        public MazePanel() {
			int[][] maze = mymaze.getMaze();
            setPreferredSize(new Dimension(maze_width * TILE_SIZE, maze_height * TILE_SIZE));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);			

			int[][] maze = mymaze.getMaze();
			// Draw maze
            for (int y = 0; y < maze_height; y++) {
                for (int x = 0; x < maze_width; x++) {
                    if (maze[x][y] == Maze.WALL) g.setColor(Color.BLACK);
                    else if (maze[x][y] == Maze.EMPTY) g.setColor(Color.WHITE);
                    else g.setColor(Color.GRAY);

                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
//                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }

            // Draw player
			g.setColor(Color.RED);
			g.fillOval(playerX * TILE_SIZE + 5, playerY * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
            
            // Draw exit
            g.setColor(Color.GREEN);
            g.fillRect(exitX * TILE_SIZE + 2, exitY * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        }
    }

    public static void main(String[] args) {
		new MazeGame();
    }
}