package maze;
/*class Maze generates mazes without using recursive functions

Note: a maze is an array of cells, each cell can be an empty place or a wall or a colored place (used to perform flooding and other tasks)

maze[] contains the following values:
0: empty place
1: wall
>=2: color (used to perform flooding and other tasks)
*/

import java.util.*;

public class Maze {

	public final static int EMPTY = 0;
	public final static int WALL = 1;

	final int N;//size of maze, including walls, must be an odd value greater than 3
	final int[][] maze;
	final Cell inner_cell;
	final Random random = new Random();

//	buildMaze(): builds a maze
	public Maze(int n, Cell out_door) throws IllegalArgumentException {
		if ((n % 2 == 0) || (n < 5))
			throw new IllegalArgumentException("Maze: n must be an odd value greater than 3");
		N = n;
		maze = new int[N][N]; //maze
//build walls both horizontal and vertical
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if ((i % 2 == 0) || (j % 2 == 0))
					maze[i][j] = WALL;
				else maze[i][j] = EMPTY;
//"extrude" blocks to form maze
		Stack<Cell> queue = new Stack<>();
		int x = out_door.x + 1, y = out_door.y;
		Cell point = new Cell(x, y);
		queue.push(point);
		maze[x][y] = 2;
		while (!queue.empty()) {
			int i = point.x;
			int j = point.y;
			boolean found = false;

			int[] dirs = new int[4];
			// 4 random directions
			for (int k = 0; k < 4; k++) {
				dirs[k] = random.nextInt(4 - k);
				for (int l = 0; l < k; l++)
					if (dirs[k] == dirs[l])
						dirs[k]++;
				int dir = dirs[k];
				if ((dir == 0) && (i < N - 2) && (maze[i + 2][j] == 0)) {
					maze[++i][j] = 2;
					maze[++i][j] = 2;
					found = true;
					break;
				} else if ((dir == 1) && (j < N - 2) && (maze[i][j + 2] == 0)) {
					maze[i][++j] = 2;
					maze[i][++j] = 2;
					found = true;
					break;
				} else if ((dir == 2) && (i > 2) && (maze[i - 2][j] == 0)) {
					maze[--i][j] = 2;
					maze[--i][j] = 2;
					found = true;
					break;
				} else if ((j > 2) && (maze[i][j - 2] == 0)) {
					maze[i][--j] = 2;
					maze[i][--j] = 2;
					found = true;
					break;
				}
			}
			if (found) {
				point = new Cell(i, j);
				queue.push(point);
			} else point = queue.pop();
		}
		setMaze(out_door, EMPTY);//out_door door
//now find proper exit door, maximize distance
		int max = 2, target_i = -1, target_j = -1;
		floodMazeDistance(out_door);
		for (int i = 1; i < N - 1; i++) {
			for (int j = 1; j < N - 1; j++) {
				if (maze[i][j] > max) {
					max = maze[i][j];
					target_i = i;
					target_j = j;
				}
			}
		}
		inner_cell = new Cell(target_i, target_j);
		clearMaze();
	}

//get_inner_cell(): returns the position of the inner cell, the typical place where a player is put at the beginning of a game
	public Cell get_inner_cell() {
		return inner_cell;
	}

//	findPathMaze(): finds path between in and out
	public ArrayList<Cell> findPathMaze(Cell in, Cell out) {
		floodMazeDistance(in);
		ArrayList<Cell> path = new ArrayList<>();
		int dist = maze[out.x][out.y] - 1;
		Cell point = out;
//backtrack path
		while (dist > 1) {
			int i = point.x;
			int j = point.y;
			if ((i < N - 1) && (j > 0) && (maze[i + 1][j] == dist)) {
				point = new Cell(i + 1, j);
				path.add(point);
				dist--;
			} else if ((i > 0) && (j < N - 1) && (maze[i][j + 1] == dist)) {
				point = new Cell(i, j + 1);
				path.add(point);
				dist--;
			} else if ((i > 0) && (j > 0) && (maze[i - 1][j] == dist)) {
				point = new Cell(i - 1, j);
				path.add(point);
				dist--;
			} else if ((i > 0) && (j > 0) && (maze[i][j - 1] == dist)) {
				point = new Cell(i, j - 1);
				path.add(point);
				dist--;
			}
		}
		clearMaze();
		return path;
	}

//	checkReachability(): check if all empty cells are reachable starting from point in
	public boolean checkReachability(Cell in) {
		floodMazeDistance(in);
		for (int i = 1; i < N - 1; i++) {
			for (int j = 1; j < N - 1; j++) {
				if (maze[i][j] == EMPTY)
					return false;
			}
		}
		return true;
	}

//	printMazeValues(): prints cell values
	public void printMazeValues() {
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < N; i++)
				System.out.print(maze[i][j]);
			System.out.println();
		}
	}

//	printMaze(): prints maze using * and . characters
	public void printMaze() {
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < N; i++)
				if (maze[i][j] == EMPTY) {
					System.out.print(" ");
				} else if (maze[i][j] == WALL) {
					System.out.print("*");
				} else System.out.print(".");
				
			System.out.println();
		}
	}

//	setMaze(): set val in cell located at point p
	public void setMaze(Cell p, int val) {
		maze[p.x][p.y] = val;
	}
	
//	getMaze(): get val in cell located at point p
	public int getMaze(Cell p) {
		return maze[p.x][p.y];
	}
	
//	setMaze(): set val along path
	public void setMaze(ArrayList<Cell> path, int val) {
		for (Cell p: path)
			maze[p.x][p.y] = val;
	}

//	clearMaze(): clear all cells different from WALL
	public void clearMaze() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (maze[i][j] > WALL)
					maze[i][j] = EMPTY;
		}
	}
	
	private void floodMaze(int i, int j, int color) {
		if (maze[i][j] == EMPTY) {//free place?
			maze[i][j] = color;
			if ((i < N - 1) && (j > 0)) floodMaze(i + 1, j, color);
			if ((i > 0) && (j < N - 1)) floodMaze(i, j + 1, color);
			if ((i > 0) && (j > 0)) floodMaze(i - 1, j, color);
			if ((i > 0) && (j > 0)) floodMaze(i, j - 1, color);
		}
	}

	@SuppressWarnings("unchecked")
	private void floodMazeDistance(Cell p) {
		clearMaze();
		int distance = 2;
		Stack<Cell> nextwave = new Stack<>();
		nextwave.push(p);
		while (!nextwave.empty()) {
			Stack<Cell> wave = (Stack<Cell>) nextwave.clone();
			nextwave.clear();
			while (!wave.empty()) {
				p = wave.pop();
				int i = p.x;
				int j = p.y;
				if (maze[i][j] == EMPTY) {//free place?
					maze[i][j] = distance;
					if ((i < N - 1) && (j > 0)) nextwave.push(new Cell(i + 1, j));
					if ((i > 0) && (j < N - 1)) nextwave.push(new Cell(i, j + 1));
					if ((i > 0) && (j > 0)) nextwave.push(new Cell(i - 1, j));
					if ((i > 0) && (j > 0)) nextwave.push(new Cell(i, j - 1));
				}
			}
			distance++;
		}
	}
}