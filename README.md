# Maze
This java application generates and tests square mazes. 
This class implement a maze as an array of cells, each cell can be an empty place or a wall or a colored place (used to perform flooding and other tasks).
Java 8 or later is required to run the application.

The maze solver function findPathMaze() uses the flood fill algorithm to find the way out of the maze.

# Compile
Run following command to build the application:
```
javac TestMaze.java
```

# Run
Just use the following command to run the application:
```
usage: java TestMaze [size]

optional parameter size must be an odd value greater than 3
```

# Example

Running the application with command "java TestMaze 15" may provide following output:
```
checkReachability: true
***************
....*  ...*...*
***.***.*.*.*.*
* *.*...*.*.*.*
* *.*.***.*.*.*
*...*.* *...*.*
*.***.* *****.*
*.....*     *.*
******* *** *.*
*...*...*   *.*
*.*.*.*.*****.*
*.*...*.*...*.*
*.*****.*.*.*.*
*.... *...*...*
***************
length of shortest path=81
```
Note that in the diagram, the asterisks represent the wall and the dots represent the path to exit from maze.
