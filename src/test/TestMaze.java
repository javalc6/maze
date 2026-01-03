package test;
/*building and testing mazes without using recursive functions

build: ant compile

usage: java -cp classes test.TestMaze [width [height]]

optional parameters width and height must be odd values greater than 3

*/

import java.util.*;

import maze.Maze;
import maze.Cell;

public class TestMaze {

	public final static int EMPTY = 0;
	public final static int WALL = 1;

	public static void main (String[] args) {
		try {
			int maze_width = 51;
			int maze_height = 51;
			if (args.length > 0) {
				maze_width = Integer.parseInt(args[0]);
				if (args.length > 1) {
					maze_height = Integer.parseInt(args[1]);
				} else maze_height = maze_width;
			}
			TestMaze testMaze = new TestMaze();
			testMaze.test_maze(maze_width, maze_height);
		} catch (IllegalArgumentException e) {
			System.out.println("usage: java TestMaze [size]");
			System.out.println("optional parameter size must be an odd value greater than 3");
			System.exit(1);
		}
	}
	
	public void test_maze(int maze_width, int maze_height) throws IllegalArgumentException {
		Random random = new Random();
//out door shall be placed on the enclosure wall
		Cell out_door = new Cell(0, 1 + 2 * random.nextInt(maze_height / 2));
		Maze maze = new Maze(maze_width, maze_height, out_door);
//inner cell is the typical place where a player is put at the beginning of a game
		Cell inner_cell = maze.getInnerCell();
//check if there are cells not connected to the out door
		System.out.println("Check cells reachability: " + maze.checkReachability(out_door));
//find a path from inner cell to out door
		ArrayList<Cell> path = maze.findPathMaze(inner_cell, out_door);
//'color' path with value 2
		maze.setMaze(path, 2);
//show maze with exit path
		maze.printMaze();
		if (!path.isEmpty())
			System.out.println("Length of shortest path="+path.size());
		else System.out.println("Error: there is no path between inner cell and out");
	}

}
		