// path.cc
#include "path.h"
#include <math.h>
#include <vector>
#include <queue>
#include <GL/gl.h>
#include <GL/glut.h>

using namespace std;

Path::Path(Maze* maze)
{
    this->maze = maze;
    current_point = maze->getStart();
    show_all = false;
    no_goal = false;
    length = 0;
}

Path::Path(Maze* maze, const Point& starting_point)
{
    this->maze = maze;
    current_point = starting_point;
    show_all = false;
    no_goal = false;
    length = 0;
}

//Two new constructors to make the code for derived 
//classes look a slight bit prettier...
Path::Path(Maze* maze, int con_type)
{
    this->maze = maze;
    current_point = maze->getStart();
    show_all = false;
    no_goal = false;
    length = 0;
    RAContainerFactory<LabPair> factory(con_type);
    bucket = factory.createContainer();
}

Path::Path(Maze* maze, const Point& starting_point, int con_type)
{
    this->maze = maze;
    current_point = starting_point;
    show_all = false;
    no_goal = false;
    length = 0;
    RAContainerFactory<LabPair> factory(con_type);
    bucket = factory.createContainer();
}

bool Path::hasMorePoints() const
{
    return (!(goalFound() || no_goal) );
}

bool Path::goalFound() const
{
  return goal_found;
}

void Path::draw(float red, float green, float blue)
{
    glColor3f(red, green, blue);
    glutSolidSphere(0.5, 16, 16);
}


//  Path& Path::operator=(const Path& p)
//  {
//      this->maze = p.maze;
//      return *this;
//  }

unsigned Path::getLength() const
{
    return length;
}


bool Path::showAllPoints() const
{ 
  return show_all; 
}

void Path::setShowAllPoints(bool show_all)
{
  this->show_all = show_all;
}


DepthSearchPath::DepthSearchPath(Maze *maze) : Path(maze, 0) 
{
}

DepthSearchPath::DepthSearchPath(Maze *maze, const Point& starting_point) : Path(maze, starting_point, 0)
{
}

void DepthSearchPath::draw()
{
  Path::draw(0.0, 1.0, 0.0);
}

Point& DepthSearchPath::getNextPoint()
{
  if(maze->isGoal(current_point)){
    goal_found=true;
    return current_point;
  }

  vector<Point> n = maze->getFloorNeighborsWithAttrib(current_point, DEPTH, 0);
  
  while(!bucket->empty() && n.size() == 0) {
    current_point = bucket->top().second;
    bucket->pop();
    return current_point;
  }
  if(bucket->empty() && n.size()==0) {
    no_goal = true;
    return current_point;
  }
                                                                         

  for (int i=0; i < n.size(); ++i) {
    int path_length = maze->getAttrib(DEPTH, current_point) + 1;
    bucket->push(LabPair(path_length, n[i]));
    maze->setAttrib(path_length, DEPTH, n[i]);
  }

  current_point = bucket->top().second;
  length = maze->getAttrib(DEPTH, current_point);
  bucket->pop();

  return current_point;
}

BreadthSearchPath::BreadthSearchPath(Maze *maze) : Path(maze, 1)
{  
}

BreadthSearchPath::BreadthSearchPath(Maze *maze, const Point& starting_point) : Path(maze, starting_point, 1)
{
}

void BreadthSearchPath::draw()
{
  Path::draw(0, 1.0, 1.0);
}

Point& BreadthSearchPath::getNextPoint()
{
  if(maze->isGoal(current_point)){
    goal_found=true;
    return current_point;
  }

  vector<Point> n = maze->getFloorNeighborsWithAttrib(current_point, BREADTH, 0);
  
  while(!bucket->empty() && n.size() == 0){
    current_point = bucket->top().second;
    bucket->pop();
    return current_point;
  }
  
  if(bucket->empty() && n.size()==0) {
    no_goal = true;
    return current_point;
  }
  
  for (int i=0; i<n.size(); ++i) {
    int path_length = maze->getAttrib(BREADTH, current_point) + 1;
    bucket->push(LabPair(path_length, n[i]));
    maze->setAttrib(path_length, BREADTH, n[i]);
  }

  current_point = bucket->top().second;
  length = maze->getAttrib(BREADTH, current_point);
  bucket->pop();
  
  return current_point;
}


BestSearchPath::BestSearchPath(Maze *maze) : Path(maze, 2)
{ 
}

BestSearchPath::BestSearchPath(Maze *maze, const Point& starting_point) : Path(maze, starting_point, 2)
{
}

void BestSearchPath::draw()
{
  Path::draw(1.0, 0.0, 1.0);
}

Point& BestSearchPath::getNextPoint()
{
  if(maze->isGoal(current_point)){
    goal_found=true;
    return current_point;
  }
  vector<Point> n = maze->getFloorNeighborsWithAttrib(current_point, BEST, 0);

  while(!bucket->empty() && n.size() == 0){
    current_point = bucket->top().second;
    bucket->pop();
    return current_point;
  }
 
  if(bucket->empty() && n.size()==0){
    no_goal = true;
    return current_point;
  }
                                                                                   
  for (int i=0; i<n.size(); ++i){
    int path_length = maze->getAttrib(BEST, current_point) + 1;
    maze->setAttrib(path_length, BEST, n[i]);
    bucket->push(LabPair(path_length, n[i]));
  }

  current_point = bucket->top().second;
  length = maze->getAttrib(BEST, current_point);
  bucket->pop();

  return current_point;
}

AStarSearchPath::AStarSearchPath(Maze *maze) : Path(maze, 2)
{
}

AStarSearchPath::AStarSearchPath(Maze *maze, const Point& starting_point) : Path(maze, starting_point, 2)
{
}

void AStarSearchPath::draw()
{
  Path::draw(1.0, 1.0, 0.0);
}

Point& AStarSearchPath::getNextPoint()
{
  if(maze->isGoal(current_point)) {
    goal_found=true;
    return current_point;
  }
  vector<Point> n = maze->getFloorNeighborsWithAttrib(current_point, ASTAR, 0);
                                                   
  while(!bucket->empty() && n.size() == 0){
    current_point = bucket->top().second;
    bucket->pop();
    return current_point;
  }
                     
  if(bucket->empty() && n.size()==0) {
    no_goal = true;
    return current_point;
  }
 
  Point goal = maze->getGoal();        
  for (int i=0; i<n.size(); ++i){
    int path_length = maze->getAttrib(ASTAR, current_point) + abs(n[i].getRow() - goal.getRow()) + abs(n[i].getCol() - goal.getCol());
    maze->setAttrib(maze->getAttrib(ASTAR, current_point) + 1, ASTAR, n[i]);
    bucket->push(LabPair(path_length, n[i]));
  }

  current_point = bucket->top().second;
  length = maze->getAttrib(ASTAR, current_point);
  bucket->pop();

  return current_point;
}





//******************************************************//
//* Do not modify code past this point.                *//
//******************************************************//
ControlledPath::ControlledPath(Maze *maze) : Path(maze)
{
    Bubble *bubble1 = new Bubble(0.0, 0.0, 1.0, 0.05);
    Bubble *bubble2 = new Bubble(0.5, 0.0, 1.0, 0.05);
    bubbles.push_back(bubble1);
    bubbles.push_back(bubble2);
}

ControlledPath::ControlledPath(Maze *maze, Point& starting_point) : Path(maze, starting_point)
{
    Bubble *bubble1 = new Bubble(0.0, 0.0, 1.0, 0.05);
    Bubble *bubble2 = new Bubble(0.5, 0.0, 1.0, 0.05);
    bubbles.push_back(bubble1);
    bubbles.push_back(bubble2);
}

void ControlledPath::draw()
{
    glEnable(GL_BLEND);
    glBlendFunc (GL_ONE_MINUS_DST_COLOR, GL_ONE);
    glDepthMask(GL_FALSE);
    glColor3f(0.9, 0.9, 0.9);
    glPushMatrix();

    for (int i = 0; i < bubbles.size(); i++)
    {
	glPopMatrix();
	glPushMatrix();
	glTranslatef(0.6, 0.0, 0.5+3*bubbles[i]->getRadius());
	glutSolidSphere(bubbles[i]->getRadius(), 16, 16);
    }

    glDepthMask(GL_TRUE);
    glDisable(GL_BLEND);

    glPopMatrix();
    glColor3f(0.0, 0.0, 1.0);
    glTranslatef(0.0, 0.0, 0.2);
    glRotatef(90, 1.0, 0.0, 0.0);
    glutSolidTeapot(0.5);
}

bool ControlledPath::hasMorePoints() const
{
    return !(maze->isGoal(current_point));
}

Point& ControlledPath::getNextPoint()
{
    return current_point;
}

Point& ControlledPath::getCurrentPoint()
{
    return current_point;
}

void ControlledPath::setCurrentPoint(Point& p)
{
    length++;
    current_point = p;
}

unsigned ControlledPath::getLength()
{
    return length;
}

void ControlledPath::incrementBubbles()
{
    for (int i = 0; i < bubbles.size(); i++)
	bubbles[i]->incrementRadius();
}
