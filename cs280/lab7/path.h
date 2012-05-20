// path.h
#include "maze.h"
#include "container/ra_container_factory.h"
#include "container/ra_container.h"
#include <math.h>
#include <queue>
#include <stack>

#ifndef CS280_PATH_H
#define CS280_PATH_H

using std::queue;
using std::stack;
using std::pair;

//I was going to call this class PreparationH, because it's not
//very nice-looking but it does make this lab less of a pain in
//my ass...in case you couldn't tell, I've spent most of the time
//working on this lab tracking down mosquito-sized warnings.
class LabPair {
 public:
  LabPair() {}
  LabPair(int l, Point p) {
    first = l;
    second = p;
  }
  
  bool operator<(const LabPair& pair) const {
    return this->first < pair.first;
  }
  bool operator>(const LabPair& pair) const {
    return this->first > pair.first;
  }
  bool operator==(const LabPair& pair) const {
    return this->first == pair.first;
  }
  
  int first;
  Point second;
};

class Path
{
public:
  // Constructors
  Path() {} // the null path -- Don't call this!
  Path(Maze* maze);
  Path(Maze* maze, const Point& starting_point);
  Path(Maze* maze, int con_type);
  Path(Maze* maze, const Point& starting_point, int con_type);
  virtual ~Path() {}

  // inspectors
  virtual bool hasMorePoints() const;
  virtual unsigned getLength() const;
  virtual bool goalFound() const;

  virtual bool showAllPoints() const;   // Not needed for the lab.

  
  // mutators
  virtual Point& getNextPoint() = 0;

  void draw(float red, float green, float blue);
  virtual void draw() = 0;
  virtual void setShowAllPoints(bool show_all);  // Not needed for the lab.

  
protected:
  Point current_point;
  unsigned length;
  Maze* maze;
  bool no_goal;
  bool goal_found;
  RAContainer<LabPair>* bucket;

  bool show_all;    // Student's should not touch this property.
};


class DepthSearchPath : public Path
{
public:
  DepthSearchPath(Maze *maze);
  DepthSearchPath(Maze *maze, const Point& starting_point);
  virtual ~DepthSearchPath() {}
  
  // Inspectors
  virtual void draw();
  virtual Point& getNextPoint();
};

class BreadthSearchPath : public Path
{
public:
  BreadthSearchPath(Maze *maze);
  BreadthSearchPath(Maze *maze, const Point& starting_point);
  virtual ~BreadthSearchPath() {}
  
  // Inspectors
  virtual void draw();
  virtual Point& getNextPoint();
};

class BestSearchPath : public Path
{
public:
  // Constructors
  BestSearchPath(Maze *maze);
  BestSearchPath(Maze *maze, const Point& starting_point);
  virtual ~BestSearchPath() {}

  // Inspectors
  Point& getNextPoint();
  virtual void draw();
};

class AStarSearchPath : public Path
{
public:
  // Constructors
  AStarSearchPath(Maze *maze);
  AStarSearchPath(Maze *maze, const Point& starting_point);
  virtual ~AStarSearchPath() {}

  // Inspectors
  Point& getNextPoint();
  virtual void draw();
};






//************************************************************//
// Students do not need to look at any code past this point.  //
//************************************************************//
class Bubble
{
private:
    float initial_radius;
    float radius;
    float min_radius;
    float max_radius;
    float increment;
public:
    virtual ~Bubble() {}
    Bubble(float initial_radius, float min_radius, float max_radius, float increment)
    {
	radius = this->initial_radius = initial_radius;
	this->min_radius = min_radius;
	this->max_radius = max_radius;
	this->increment = increment;
    }
    virtual void incrementRadius()
    {
	radius += increment;
	if (radius > max_radius)
	    radius = min_radius;
    }
    float getRadius()
    {
	return radius;
    }
};


class ControlledPath : public Path
{
private:
    vector<Bubble*> bubbles;
public:
    ~ControlledPath()
    {
	for (int i = 0; i < bubbles.size(); i++)
	{
	    delete bubbles[i];
	}
    }

    ControlledPath(Maze *maze);
    ControlledPath(Maze *maze, Point& starting_point);
    virtual void draw();
    virtual bool hasMorePoints() const;
    virtual Point& getNextPoint();
    virtual Point& getCurrentPoint();
    virtual unsigned getLength();
    virtual void setCurrentPoint(Point& p);
    virtual void incrementBubbles();
};

#endif // CS280_PATH_H

