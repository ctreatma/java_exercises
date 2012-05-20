// maze.h

#include <vector>

#ifndef CS280_MAZE_H
#define CS280_MAZE_H

using std::vector;

#define ATTRIBUTE_BYTES 8

static const int BREADTH=0;
static const int DEPTH=1;
static const int BEST=2;
static const int ASTAR=3;

class Point {
public:
  // Constructors 
  Point();
  Point(int row, int col); // int row, int col

  // Inspectors
  int getRow() const;
  int getCol() const;
  
  int getX() const;   // = getRow()
  int getY() const;   // = getCol()


  // Mutators
  void setRow(int row);
  void setCol(int col);

  // operators
  bool operator==(const Point& p) const;
  bool operator!=(const Point& p) const;

protected:
    int x;
    int y;
};



class Maze {

public:
  // Constructors
  Maze(Maze& maze);            // Students will have no need to constrcut
  Maze(int rows, int cols);    // or destruct new mazes.
  Maze(char* filename);
  ~Maze();
  
  // inspectors
  const int getRows() const;
  const int getCols() const;
  
  const Point& getStart() const;
  const Point& getGoal() const;
  
  const Point& getStart(int n) const; // Used for multiple start/goal spaces.
  const Point& getGoal(int n) const;  // Not needed for this lab.
  
  bool isWall(const Point& p) const; // returns true if wall or not in maze
  bool isWall(int row, int col) const; 
  
  bool isStart(const Point& p) const;
  bool isStart(int row, int col) const;
  
  bool isGoal(const Point& p) const;
  bool isGoal(int row, int col) const;

  bool isFood(const Point& p) const;    // Not needed for this lab.
  bool isFood(int row, int col) const;

  vector<Point> getFloorNeighbors(const Point& p) const;    // Returns a neighboring points that are floor spaces (up to four).
  vector<Point> getFloorNeighbors(int row, int col) const;

  vector<Point> getFloorNeighborsWithAttrib(const Point& p, int label, int n) const;    // Returns a neighboring points that are floor spaces (up to four).
  vector<Point> getFloorNeighborsWithAttrib(int row, int col, int label, int n) const;  // and have the specified attribute set.

  vector<Point> getFloorNeighborsWithoutAttrib(const Point& p, int label, int n) const;    // Returns a neighboring points that are floor spaces (up to four).
  vector<Point> getFloorNeighborsWithoutAttrib(int row, int col,int label, int n) const;   // and do not have the specified attribute set.


  
  // get user defined attribute for a point
  // valid values for num are from 0 to ATTRIBUTE_BYTES-5 (= 3)
  int getAttrib(int label, int row, int col) const;
  int getAttrib(int num, const Point& p) const;
  
  
  // Mutators
  
  // set user defined attribute for a point
  // valid values for num are from 0 to ATTRIBUTE_BYTES-5 (= 3)
  void setAttrib(int newVal, int label, int row, int col);
  void setAttrib(int newVal, int label, const Point& p);
  

  

  //***************************************************************//
  //* Students have no need of the code from this point on.       *//
  //***************************************************************//
  
  // Inspectors

  // get an RGB array (values 0.0 to 1.0, red, green, blue)
  float* getColor(int row, int col) const;
  float* getColor(const Point& p) const;
  
  // get user defined attribute for a point  
  // Mutators
  void makeWall(int row, int col); // makeWall and makeFood are
  void makeFood(int row, int col); // mutually exclusive
  void makeStart(int row, int col);
  void makeGoal(int row, int col);
  void makeClear(int row, int col); // clears food and wall
  void makeWall(Point& p); // makeWall and makeFood are
  void makeFood(Point& p); // mutually exclusive
  void makeStart(Point& p);
  void makeGoal(Point& p);
  void makeClear(Point& p); // clears food and wall
  
  // clear all the user defined attributes
  void clearAttributes();
  void clearAttributes(int attrib_num);
  
  
public:
  // Statics
  static const int WALL_MASK = 1;
  static const int START_MASK = 2;
  static const int BSTART_MASK = 4;
  static const int FOOD_MASK = 8;
  static const int GOAL_MASK = 128;
  
private:
  int checkMask(int row, int col, int MASK) const;
  void setMask(int row, int col, int MASK);
  void openFile(char* fileName);
  
  vector<vector<vector<int> > > *maze; // 3-D maze
  int rows;
  int cols;
  vector<Point> start;
  vector<Point> goal;
  
};
#endif // CS280_MAZE_H



