# Maze
This java application generates and tests square mazes. 
This class implement a maze as an array of cells, each cell can be an empty place or a wall or a colored place (used to perform flooding and other tasks).
Java 8 or later is required to run the application.

# Compile
Run following command to build the application:
```
ant compile
```

# Run
Just use the following command to run the application:
```
usage: java -cp classes test.TestMaze [size]

optional parameter size must be an odd value greater than 3
```

# Example

Running the application with command TestMaze 15 may provide following output:
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
