// maze.cc

#include "maze.h"
#include <stdio.h>
#include <fstream>
#include <iostream>
#include <math.h>

using std::ifstream;
using std::cerr;
using std::endl;

// ************** Class Point *************** //
Point::Point()
{
  this->x = 0;
  this->y = 0;
}

Point::Point(int x, int y) // int row, int col
{
  this->x = x;
  this->y = y;
}

int Point::getX() const 
{ 
  return x; 
}

int Point::getRow() const 
{ 
  return x; 
}

int Point::getY() const 
{ 
  return y; 
}

int Point::getCol() const 
{ 
  return y;
}

void Point::setRow(int row) 
{ 
  x = row; 
}

void Point::setCol(int col) 
{ 
  y = col; 
}

bool Point::operator==(const Point& p) const
{
  return (x == p.getX() && y == p.getY());
}

bool Point::operator!=(const Point& p) const
{
  return !(*this == p);
}

// ************** Class Maze **************** //
Maze::Maze(Maze& m)
{
    this->rows = m.getRows();
    this->cols = m.getCols();
    maze = m.maze;
    makeStart(m.getStart().getRow(), m.getStart().getCol());
    makeGoal(m.getGoal().getRow(), m.getGoal().getCol());
}

Maze::Maze(int rows, int cols) 
{
    this->rows = rows;
    this->cols = cols;
    vector<int> dv = vector<int>(ATTRIBUTE_BYTES, 0); 
    vector<vector<int> > cv = vector<vector<int> >(cols, dv);
    maze = new vector<vector<vector<int> > >(rows, cv);

    makeStart(0,0);
    makeGoal(rows-1, cols-1);
}

Maze::Maze(char* filename)
{
    /*
     *  Read the maze from a file:
     *  
     *  bytes 1-2: # of rows
     *  bytes 3-4: # of cols
     *  ---------------------------
     *  from byte 17:
     *  1 byte for description:
     *    bit 1 tells whether it's a wall or not
     *    bit 2 tells if it's the start
     *    bit 3 tells if it's the baddies' start
     *    bit 4 tells if it's food
     *    bit 5 empty
     *    bit 6 empty
     *    bit 7 empty
     *    bit 8 tells if it's the goal
     *  3 bytes for color: r g b
     *  4 bytes empty FOR LATER USE...
     */

    // in case the file is invalid, make a blank maze...
    rows = 50;
    cols = 50;
    vector<int> dv = vector<int>(ATTRIBUTE_BYTES, 0); 
    vector<vector<int> > cv = vector<vector<int> >(cols, dv);
    maze = new vector<vector<vector<int> > >(rows, cv);

    openFile(filename);
}

Maze::~Maze() 
{
    free(maze);
}

const int Maze::getRows() const
{
    return rows;
}

const int Maze::getCols() const
{
    return cols;
}

const Point& Maze::getStart() const
{
    return getStart(0);
}

const Point& Maze::getGoal() const
{
    return getGoal(0);
}

const Point& Maze::getStart(int n) const
{
    return start[n];
}

const Point& Maze::getGoal(int n) const
{
    return goal[n];
}

bool Maze::isWall(int row, int col) const
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return 1;
    return checkMask(row, col, WALL_MASK);
}

bool Maze::isFood(int row, int col) const
{
    return checkMask(row, col, FOOD_MASK);
}

bool Maze::isStart(int row, int col) const
{
    return checkMask(row, col, START_MASK);
}

bool Maze::isGoal(int row, int col) const
{
    return checkMask(row, col, GOAL_MASK);
}

bool Maze::isWall(const Point& p) const
{
    return isWall(p.getX(), p.getY());
}

bool Maze::isFood(const Point& p) const
{
    return isFood(p.getX(), p.getY());
}

bool Maze::isStart(const Point& p) const
{
    return isStart(p.getX(), p.getY());
}

bool Maze::isGoal(const Point& p) const
{
    return isGoal(p.getX(), p.getY());
}

vector<Point> Maze::getFloorNeighbors(const Point& p) const
{
  return getFloorNeighbors(p.getRow(), p.getCol());
}

vector<Point> Maze::getFloorNeighbors(int row, int col) const
{
  vector<Point> vec;
  vec.reserve(4);
  
  if (!isWall(row, col+1))
    vec.push_back(Point(row,col+1));
  if (!isWall(row+1,col))
    vec.push_back(Point(row+1,col));
  if (!isWall(row,col-1))
    vec.push_back(Point(row,col-1));
  if (!isWall(row-1,col))
    vec.push_back(Point(row-1,col));

  return vec;
}

vector<Point> Maze::getFloorNeighborsWithAttrib(const Point& p, int label, int n) const
{
  return getFloorNeighborsWithAttrib(p.getRow(), p.getCol(), label, n);
}

vector<Point> Maze::getFloorNeighborsWithAttrib(int row, int col, int label, int n) const
{
  vector<Point> vec;
  vec.reserve(4);
  
  if (!isWall(row, col+1) && (getAttrib(label, row, col+1)==n))
    vec.push_back(Point(row,col+1));
  if (!isWall(row+1,col) && (getAttrib(label, row+1, col)==n))
    vec.push_back(Point(row+1,col));
  if (!isWall(row,col-1) && (getAttrib(label, row, col-1)==n))
    vec.push_back(Point(row,col-1));
  if (!isWall(row-1,col) && (getAttrib(label, row-1, col)==n))
    vec.push_back(Point(row-1,col));

  return vec;
}
vector<Point> Maze::getFloorNeighborsWithoutAttrib(const Point& p, int label, int n) const
{
  return getFloorNeighborsWithoutAttrib(p.getRow(), p.getCol(), label, n);
}

vector<Point> Maze::getFloorNeighborsWithoutAttrib(int row, int col, int label, int n) const
{
  vector<Point> vec;
  vec.reserve(4);
  
  if (!isWall(row, col+1) && (getAttrib(label, row, col+1)!=n))
    vec.push_back(Point(row,col+1));
  if (!isWall(row+1,col) && (getAttrib(label, row+1, col)!=n))
    vec.push_back(Point(row+1,col));
  if (!isWall(row,col-1) && (getAttrib(label, row, col-1)!=n))
    vec.push_back(Point(row,col-1));
  if (!isWall(row-1,col) && (getAttrib(label, row-1, col)!=n))
    vec.push_back(Point(row-1,col));

  return vec;
}

// get an RGB array (values 0.0 to 1.0)
float* Maze::getColor(int row, int col) const
{
    float* colors = new float[3];

    colors[0] = 0.0;
    colors[1] = 0.0;
    colors[2] = 0.0;

    if (row >= rows || row < 0 ||
	col >= cols || col < 0)
	return colors;

    colors[0] = ((float)(*maze)[row][col][1] / 255.0);
    colors[1] = ((float)(*maze)[row][col][2] / 255.0);
    colors[2] = ((float)(*maze)[row][col][3] / 255.0);
    return colors;
}
float* Maze::getColor(const Point& p) const
{
    return getColor(p.getX(), p.getY());
}

// get user defined attribute for a point
// valid values for num are from 0 to ATTRIBUTE_BYTES-5 (= 3)
int Maze::getAttrib(int num, int row, int col) const
{
    if (row >= rows || row < 0 ||
	col >= cols || col < 0 ||
	num >= ATTRIBUTE_BYTES - 4 || num < 0)
	return 0;

    else return (*maze)[row][col][num+4];
}
int Maze::getAttrib(int num, const Point& p) const
{
    return getAttrib(num, p.getX(), p.getY());
}

void Maze::makeWall(int row, int col)
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return;
    
    makeClear(row, col);
    setMask(row, col, WALL_MASK);
}

void Maze::makeFood(int row, int col)
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return;
    
    makeClear(row, col);
    setMask(row, col, FOOD_MASK);
}

void Maze::makeStart(int row, int col)
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return;
    
    Point p(row, col);
    start.push_back(p);
    setMask(row, col, START_MASK);
}

void Maze::makeGoal(int row, int col)
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return;
    
    Point p(row, col);
    start.push_back(p);
    setMask(row, col, GOAL_MASK);
}

void Maze::makeClear(int row, int col)
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return;
    
    // clear food and wall
    int CLEAR_MASK = -1 ^ (FOOD_MASK | WALL_MASK);
    int descrip = (*maze)[row][col][0];
    int newDescrip = descrip & CLEAR_MASK;
    (*maze)[row][col][0] = newDescrip;
}

void Maze::makeWall(Point& p)
{
    makeWall(p.getX(), p.getY());
}
void Maze::makeFood(Point& p)
{
    makeFood(p.getX(), p.getY());
}
void Maze::makeStart(Point& p)
{    
    makeStart(p.getX(), p.getY());
}
void Maze::makeGoal(Point& p)
{    
    makeGoal(p.getX(), p.getY());
}
void Maze::makeClear(Point& p)
{
    makeClear(p.getX(), p.getY());
}

// set user defined attribute for a point
// valid values for num are from 0 to ATTRIBUTE_BYTES-5 (= 3)
void Maze::setAttrib(int attrib_val, int attrib_num, int row, int col)
{
    if (row >= rows || row < 0 ||
	col >= cols || col < 0 ||
	attrib_num >= ATTRIBUTE_BYTES - 4 || attrib_num < 0)
	return;
    (*maze)[row][col][attrib_num+4] = attrib_val;
}
void Maze::setAttrib(int attrib_val, int attrib_num, const Point& p)
{
    setAttrib(attrib_val, attrib_num, p.getX(), p.getY());
}

void Maze::clearAttributes()
{
    for (int r = 0; r < rows; r++)
    {
	for (int c = 0; c < cols; c++)
	{
	    for (int a = 0; a < ATTRIBUTE_BYTES - 4; a++)
	    {
		setAttrib(0, a, r, c);
	    }
	}
    }
}

void Maze::clearAttributes(int attrib_num)
{
    for (int r = 0; r < rows; r++)
    {
	for (int c = 0; c < cols; c++)
	{
	    setAttrib(0, attrib_num, r, c);
	}
    }
}

// private methods
int Maze::checkMask(int row, int col, int MASK) const
{
    if (row >= rows || col >= cols ||
	row < 0 || col < 0) return 0;
    int descrip = (*maze)[row][col][0];
    return descrip & MASK;
}

void Maze::setMask(int row, int col, int MASK)
{
    int descrip = (*maze)[row][col][0];
    int newDescrip = descrip | MASK;
    (*maze)[row][col][0] = newDescrip;
}

void Maze::openFile(char *filename)
{
    ifstream input(filename);
    if (! input)
    {
	cerr << "File Not Found: "<< filename <<endl;
	makeStart(0,0);
	makeGoal(rows-1, cols-1);
	return;
    }
    rows = input.get();
    rows = rows * 256 + input.get();
    cols = input.get();
    cols = cols * 256 + input.get();

    vector<int> av = vector<int>(ATTRIBUTE_BYTES, 0); 
    vector<vector<int> > cv = vector<vector<int> >(cols, av);
    maze = new vector<vector<vector<int> > >(rows, cv);

    // 12 empty bytes
    for (int b = 0; b < 12; b++)
	input.get();
    
    for (int r = 0; r < rows; r++)
    {
	for (int c = 0; c < cols; c++)
	{
	    for (int a = 0; a < ATTRIBUTE_BYTES; a++)
	    {
		(*maze)[r][c][a] = input.get();
	    }

	    if ((*maze)[r][c][0] & START_MASK)
	    {
		Point p(r, c);
		start.push_back(p);
	    }
	    else if ((*maze)[r][c][0] & GOAL_MASK)
	    {
		Point p(r, c);
		goal.push_back(p);
	    }
	}
    }
}

// *************End Class Maze*************** //
