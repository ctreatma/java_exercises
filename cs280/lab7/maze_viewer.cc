/* maze_viewer.cc
 *
 * Ben Taitelbaum, Spring 2002
 *
 * an OpenGL program to view mazes
 * for CS280
 */

#include "maze.h"
#include "path.h"
#include <math.h>
#include <vector>
#include <fstream>
#include <GL/glui.h>
#include <GL/glut.h>
#include <GL/glu.h>
#include <GL/gl.h>

using namespace std;

class PathWrapper
{
    int path_id;
    float direction; // from 0 to 2*PI
    Point curr_point;
    Path* path;
    vector<Point*> all_points;
public:
    ~PathWrapper()
     {
	 delete path;
	 for (int i = 0; i < all_points.size(); i++)
	     delete all_points[i];
     }
    PathWrapper(int path_id, Path* path, const Point& curr_point)
    {
	this->path_id = path_id;
	this->path = path;
	this->curr_point = curr_point;
	all_points.push_back(new Point(curr_point.getRow(), curr_point.getCol()));
	direction = 0;
    }
    int getId()
    {
	return path_id;
    }
    float getDirection()
    {
	return direction;
    }
    int getApproximateDirection() 
        // returns 0, 45, 90, 135, 180, 225, 270, 315
    {
	if (fabs(180.0 / M_PI * direction - 0.0) <= 22.5)
	    return 0;
	else if (fabs(180.0 / M_PI * direction - 45.0) <= 22.5)
	    return 45;
	else if (fabs(180.0 / M_PI * direction - 90.0) <= 22.5)
	    return 90;
	else if (fabs(180.0 / M_PI * direction - 135.0) <= 22.5)
	    return 135;
	else if (fabs(180.0 / M_PI * direction - 180.0) <= 22.5)
	    return 180;
	else if (fabs(180.0 / M_PI * direction - 225.0) <= 22.5)
	    return 225;
	else if (fabs(180.0 / M_PI * direction - 270.0) <= 22.5)
	    return 270;
	else if (fabs(180.0 / M_PI * direction - 315.0) <= 22.5)
	    return 315;
	else return -1;
    }
    Path* getPath()
    {
	return path;
    }
    vector<Point*>& getAllPoints()
    {
	return all_points;
    }
    Point& getCurrPoint()
    {
	return curr_point;
    }
    void setDirection(float direction)
    {
	this->direction = direction;
    }
    void setCurrPoint(Point& p)
    {
	curr_point = p;
	all_points.push_back(new Point(p.getRow(), p.getCol()));
    }

    void update()
    {
	setCurrPoint(path->getNextPoint());
    }
};

Maze *maze;
int main_window;
bool local_view = 0;
int local_view_id = -1;
int view_id = 0;
int controlled_path_id = -1; 
int breadth_path_id = -1;
int depth_path_id = -1;
int best_path_id = -1;
int a_star_path_id = -1;
vector<PathWrapper*> paths;
GLint MAZE = 100;
GLint START = 200;
GLint GOAL = 300;
GLint ww=500, wh=500;
GLint wx=100, wy=100;
GLfloat fovy=135.0;
GLfloat centerx = (GLfloat)ww/2.0;
GLfloat centery = (GLfloat)wh/2.0;
ControlledPath *controlledPath; 
BreadthSearchPath *breadthPath; 
DepthSearchPath *depthPath;
BestSearchPath *bestPath;
AStarSearchPath *aStarPath;
int showBreadth = 0, showDepth = 0, showBest = 0, showAStar = 0;
int stopBreadth = 1, stopBest = 1, stopDepth = 1, stopAStar = 1;
int timer_speed = 150;
GLUI *glui;
GLUI *openFileWindow;
GLUI_EditText *fileNameBox;

void drawStart()
{
    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    glBegin(GL_TRIANGLE_FAN);
    glVertex3f(0.0, 0.0, 0.5);
    for (float theta = 0.0; theta < 2*M_PI+0.5; theta += M_PI / 16)
    {
	glVertex3f(0.5*cos(theta), 0.5*sin(theta), 0.5);
    }
    glEnd();
}

void drawGoal()
{
    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    glBegin(GL_POLYGON);
    for (float theta = 0.0; theta <= 2*M_PI; theta += M_PI / 16)
    {
	glVertex3f(0.5*cos(theta), 0.5*sin(theta), 0.5);
    }
    glEnd();

    glBegin(GL_POLYGON);
    for (float theta = 0.0; theta <= 2*M_PI; theta += M_PI / 16)
    {
	glVertex3f(0.3*cos(theta), 0.3*sin(theta), 0.5);
    }
    glEnd();

    glBegin(GL_POLYGON);
    for (float theta = 0.0; theta <= 2*M_PI; theta += M_PI / 16)
    {
	glVertex3f(0.1*cos(theta), 0.1*sin(theta), 0.5);
    }
    glEnd();
}

void goalFound(int length)
{
    glMatrixMode(GL_PROJECTION);
    glPushMatrix();
    glLoadIdentity();
    gluOrtho2D(0.0, (GLfloat) ww, (GLfloat) wh, 0.0);

    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    glLoadIdentity();
    char* goalStr1 = "Goal Found!";
    char goalStr2[30];
    sprintf(goalStr2, "Path Length: %d", length);
    glRasterPos2f(ww/2 - 
		  glutBitmapLength(GLUT_BITMAP_HELVETICA_18, (unsigned char*)goalStr1) / 2, 22.0);
    for (int i = 0; i < strlen(goalStr1); i++)
	glutBitmapCharacter( GLUT_BITMAP_HELVETICA_18, goalStr1[i]);

    glRasterPos2f(ww/2 - 
		  glutBitmapLength(GLUT_BITMAP_HELVETICA_18, (unsigned char*)goalStr2) / 2, 44.0);
    for (int i = 0; i < strlen(goalStr2); i++)
	glutBitmapCharacter( GLUT_BITMAP_HELVETICA_18, goalStr2[i]);
    glPopMatrix();
    glMatrixMode(GL_PROJECTION);
    glPopMatrix();
    glMatrixMode(GL_MODELVIEW);
}

void drawMaze()
{
    int rows = maze->getRows();
    int cols = maze->getCols();

    glMatrixMode(GL_MODELVIEW);

    glPushMatrix();
    for (int r = 0; r < rows; r++)
    {	
	for (int c = 0; c < cols; c++)
	{
	    glColor3fv(maze->getColor(r, c));
	    glPopMatrix();
	    glPushMatrix();
	    //glLoadIdentity();
	    if (maze->isWall(r, c))
	    {
		glTranslatef((GLfloat) c, (GLfloat) -r, -9.5);
		glutSolidCube(1.0);
	    }
	    else
	    {
		glTranslatef((GLfloat) c, (GLfloat) -r, -10.0);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
		  glVertex2f(-0.5, -0.5);
		  glVertex2f(-0.5, 0.5);
		  glVertex2f(0.5, 0.5);
		  glVertex2f(0.5, -0.5);
		glEnd();
	    }
	    if (maze->isStart(r, c))
	    {
		glCallList(START);
	    }
	    if (maze->isGoal(r, c))
	    {
		glCallList(GOAL);
	    }
	    glPopMatrix();
	}
    }
}

void myInit()
{
    glClearColor(0.0, 0.0, 0.0, 0.0);

    GLfloat specular[] = {1.0, 1.0, 1.0, 1.0};
    GLfloat shininess[] = {500.0};
    GLfloat position[] = { 0.0, 0.0, 3.0, 1.0};

    glShadeModel(GL_SMOOTH);
    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    glEnable(GL_DEPTH_TEST);
    glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);

    glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, specular);
    glMaterialfv(GL_FRONT_AND_BACK, GL_SHININESS, shininess);
    glLightfv(GL_LIGHT0, GL_POSITION, position);
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_COLOR_MATERIAL);

    glNewList(MAZE, GL_COMPILE);
    drawMaze();
    glEndList();

    glNewList(START, GL_COMPILE);
    drawStart();
    glEndList();

    glNewList(GOAL, GL_COMPILE);
    drawGoal();
    glEndList();
}

void display()
{
    float cols = (float) maze->getCols();
    float rows = (float) maze->getRows();

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    Point p;
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    if (local_view && local_view_id >= 0)
    {
	PathWrapper* pw = paths[local_view_id];
	float direction = pw->getDirection();

	p = pw->getCurrPoint();
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	gluPerspective(130.0, (GLfloat) ww / (GLfloat) wh, 0.2, 100.0);
	glRotatef(- 90.0, 1.0, 0.0, 0.0);
	glRotatef(90 - direction * 180.0 / M_PI, 0.0, 0.0, 1.0);
	glTranslatef(- p.getCol(), p.getRow(), 9.2);
	glMatrixMode(GL_MODELVIEW);    
    }

    //drawMaze();
    glCallList(MAZE);

    for (int i = 0; i < paths.size(); i++) //display with proper priority
    {
	PathWrapper* pw = paths[i];
	
	if (pw->getPath()->showAllPoints())
	{
	    vector<Point*> all_pts = pw->getAllPoints();
	    for (int i = 0; i < all_pts.size(); i++)
	    {	    
		glLoadIdentity();
		glTranslatef(all_pts[i]->getCol(), -all_pts[i]->getRow(), -10.0);
		pw->getPath()->draw();
	    }
	    p = pw->getCurrPoint();
	}
	else
	{
	    p = pw->getCurrPoint();
	    
	    glLoadIdentity();
	    glTranslatef(p.getCol(), -p.getRow(), -10.0);
	    glRotatef(pw->getDirection() * 180.0 / M_PI,
		      0.0, 0.0, 1.0);
	    pw->getPath()->draw();
	}
	if (maze->isGoal(p)) 
	{
	    if (local_view)
	    {
		if (local_view_id == pw->getId())
		    goalFound(pw->getPath()->getLength());
	    }
	    else
	    {
		goalFound(pw->getPath()->getLength());
	    }
	}
    }

    glutSwapBuffers();
}

void reshape(int w, int h)
{
    ww = w; wh = h;

    glViewport(0, 0, (GLint) w, (GLint) h);

    if (! local_view)
    {
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	float c = (centerx / ww)*maze->getCols();
	float r = (centery / wh)*maze->getRows();

	gluPerspective(fovy, (GLfloat) w / (GLfloat) h, 1.0, 500.0);
	glTranslatef(-c, r, -5.0);
   
	glMatrixMode(GL_MODELVIEW);
    }
}

void pathUpdated(Point& next_point, int path_id)
{
    PathWrapper* pw = paths[path_id];
    pw->setCurrPoint(next_point);
    // here's where we can determine if we need to rotate the viewing area
    if (path_id != controlled_path_id)
    {
    Point last_point = pw->getCurrPoint();
	int direction = 0;
	if (last_point.getCol() < next_point.getCol())
	    direction = 0;
	else if (last_point.getRow() > next_point.getRow())
	    direction = 90;
	else if (last_point.getCol() > next_point.getCol())
	    direction = 180;
	else if (last_point.getRow() < next_point.getRow())
	    direction = 270;
	pw->setDirection((float)direction * M_PI / 180.0);
    }
}

void setupPaths()
{
    controlledPath = new ControlledPath(maze);
    controlledPath->setShowAllPoints(false);
    controlled_path_id = 0;
    paths.push_back(new PathWrapper(controlled_path_id, 
				    controlledPath, 
				    maze->getStart()));
    breadthPath = new BreadthSearchPath(maze, maze->getStart());
    breadthPath->setShowAllPoints(false);
    breadth_path_id = 1;
    stopBreadth = 1;
    paths.push_back(new PathWrapper(breadth_path_id, 
				    breadthPath, 
				    maze->getStart()));
    depthPath = new DepthSearchPath(maze, maze->getStart()); 
    depthPath->setShowAllPoints(false);
    depth_path_id = 2;
    stopDepth = 1;
    paths.push_back(new PathWrapper(depth_path_id, 
				    depthPath, 
				    maze->getStart()));
    bestPath = new BestSearchPath(maze, maze->getStart());
    bestPath->setShowAllPoints(false);
    best_path_id = 3;
    stopBest = 1;
    paths.push_back(new PathWrapper(best_path_id, 
				    bestPath, 
				    maze->getStart()));
    aStarPath = new AStarSearchPath(maze, maze->getStart());
    aStarPath->setShowAllPoints(false);
    a_star_path_id = 4;
    stopAStar = 1;
    paths.push_back(new PathWrapper(a_star_path_id,
				    aStarPath,
				    maze->getStart()));
}

void clearAll()
{
    for (int i = 0; i < paths.size(); i++)
    {
	delete paths[i];
    }
    paths.erase(paths.begin(), paths.end());

    maze->clearAttributes();

    if (! local_view)
    {
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	centerx = ww / 2.0;
	centery = wh / 2.0;
	fovy = 135.0;

	float c = (centerx / ww)*maze->getCols();
	float r = (centery / wh)*maze->getRows();

	gluPerspective(fovy, (GLfloat) ww / (GLfloat) wh, 1.0, 500.0);
	glTranslatef(-c, r, -5.0);
   
	glMatrixMode(GL_MODELVIEW);
    }

}

void clearPaths(int button_id)
{
    if (showBreadth)
    {
	bool show = paths[breadth_path_id]->getPath()->showAllPoints();
	delete paths[breadth_path_id];
	breadthPath = new BreadthSearchPath(maze, maze->getStart());
	breadthPath->setShowAllPoints(show);
	paths[breadth_path_id] = new PathWrapper(breadth_path_id, 
						 breadthPath, 
						 maze->getStart());
	maze->clearAttributes(BREADTH);
	stopBreadth = 1;
    }
    if (showDepth)
    {
	bool show = paths[depth_path_id]->getPath()->showAllPoints();
	delete paths[depth_path_id];
	depthPath = new DepthSearchPath(maze, maze->getStart());
	depthPath->setShowAllPoints(show);
	paths[depth_path_id] = new PathWrapper(depth_path_id, 
					       depthPath, 
					       maze->getStart());
	maze->clearAttributes(DEPTH);
	stopDepth = 1;
    }
    if (showBest)
    {
	bool show = paths[best_path_id]->getPath()->showAllPoints();
	delete paths[best_path_id];
	bestPath = new BestSearchPath(maze, maze->getStart());
	bestPath->setShowAllPoints(show);
	paths[best_path_id] = new PathWrapper(best_path_id, 
					      bestPath, 
					      maze->getStart());
	maze->clearAttributes(BEST);
	stopBest = 1;
    }

    if (showAStar)
    {
	bool show = paths[a_star_path_id]->getPath()->showAllPoints();
	delete paths[a_star_path_id];
	aStarPath = new AStarSearchPath(maze, maze->getStart());
	aStarPath->setShowAllPoints(show);
	paths[a_star_path_id] = new PathWrapper(a_star_path_id, 
						aStarPath, 
						maze->getStart());
	maze->clearAttributes(ASTAR);
	stopAStar = 1;
    }
}

void clearAllRestart(int button_id)
{
    clearAll();
    setupPaths();
}

void selectView(int id)
{
    switch(view_id)
    {
    case 0:
	local_view = 0;
	break;
    case 1:
	local_view = 1;
	local_view_id = controlled_path_id;
	break;
    case 2:
	local_view = 1;
	local_view_id = breadth_path_id;
	break;
    case 3:
	local_view = 1;
	local_view_id = depth_path_id;
	break;
    case 4:
	local_view = 1;
	local_view_id = best_path_id;
	break;
    case 5:
	local_view = 1;
	local_view_id = a_star_path_id;
	break;
    }
    reshape(ww, wh);
    glutSetWindow(main_window);
    glutPostRedisplay();
}
void keyboard(unsigned char key, int x, int y)
{
    switch(key)
    {
    case ' ':
	local_view = (!local_view || (local_view && !(local_view_id == controlled_path_id)));
	local_view_id = controlled_path_id;
	view_id = 1;
	reshape(ww, wh);
	break;
    case '1':
	local_view = (!local_view || (local_view && !(local_view_id == breadth_path_id)));
	local_view_id = breadth_path_id;
	view_id = 2;
	reshape(ww, wh);
	break;
    case '2':
	local_view = (!local_view || (local_view && !(local_view_id == depth_path_id)));
	local_view_id = depth_path_id;
	view_id = 3;
	reshape(ww, wh);
	break;
    case '3':
	local_view = (!local_view || (local_view && !(local_view_id == best_path_id)));
	local_view_id = best_path_id;
	view_id = 4;
	reshape(ww, wh);
	break;
    case '4':
	local_view = (!local_view || (local_view && !(local_view_id == a_star_path_id)));
	local_view_id = a_star_path_id;
	view_id = 5;
	reshape(ww, wh);
	break;
    }
    if (! local_view) view_id = 0;
    glui->sync_live();
    glui->enable();
    glutSetWindow(main_window);
    glutPostRedisplay();
}

void special(int key, int x, int y)
{
    if (controlled_path_id < 0) return;
    PathWrapper* pw = paths[controlled_path_id];

    Point current_point = pw->getCurrPoint();
    int direction = pw->getApproximateDirection();

    if (key == GLUT_KEY_UP)
    {
	if (direction == 0)
	{
	    if (! maze->isWall(current_point.getRow(), current_point.getCol()+1))
	    {
		current_point = Point(current_point.getRow(), current_point.getCol()+1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	if (direction == 45)
	{
	    if (! maze->isWall(current_point.getRow()-1, current_point.getCol()+1))
	    {
		current_point = Point(current_point.getRow()-1, current_point.getCol()+1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 90)
	{
	    if (! maze->isWall(current_point.getRow()-1, current_point.getCol()))
	    {
		current_point = Point(current_point.getRow()-1, current_point.getCol());
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 135)
	{
	    if (! maze->isWall(current_point.getRow()-1, current_point.getCol()-1))
	    {
		current_point = Point(current_point.getRow()-1, current_point.getCol()-1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 180)
	{
	    if (! maze->isWall(current_point.getRow(), current_point.getCol()-1))
	    {
		current_point = Point(current_point.getRow(), current_point.getCol()-1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 225)
	{
	    if (! maze->isWall(current_point.getRow()+1, current_point.getCol()-1))
	    {
		current_point = Point(current_point.getRow()+1, current_point.getCol()-1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 270)
	{
	    if (! maze->isWall(current_point.getRow()+1, current_point.getCol()))
	    {
		current_point = Point(current_point.getRow()+1, current_point.getCol());
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 315)
	{
	    if (! maze->isWall(current_point.getRow()+1, current_point.getCol()+1))
	    {
		current_point = Point(current_point.getRow()+1, current_point.getCol()+1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	pw->setDirection((float)direction * M_PI / 180.0);
	glutSetWindow(main_window);
	glutPostRedisplay();
    }
    else if (key == GLUT_KEY_DOWN)
    {
	if (direction == 0)
	{
	    if (! maze->isWall(current_point.getRow(), current_point.getCol()-1))
	    {
		current_point = Point(current_point.getRow(), current_point.getCol()-1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	if (direction == 45)
	{
	    if (! maze->isWall(current_point.getRow()+1, current_point.getCol()-1))
	    {
		current_point = Point(current_point.getRow()+1, current_point.getCol()-1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 90)
	{
	    if (! maze->isWall(current_point.getRow()+1, current_point.getCol()))
	    {
		current_point = Point(current_point.getRow()+1, current_point.getCol());
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 135)
	{
	    if (! maze->isWall(current_point.getRow()+1, current_point.getCol()+1))
	    {
		current_point = Point(current_point.getRow()+1, current_point.getCol()+1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 180)
	{
	    if (! maze->isWall(current_point.getRow(), current_point.getCol()+1))
	    {
		current_point = Point(current_point.getRow(), current_point.getCol()+1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 225)
	{
	    if (! maze->isWall(current_point.getRow()-1, current_point.getCol()+1))
	    {
		current_point = Point(current_point.getRow()-1, current_point.getCol()+1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 270)
	{
	    if (! maze->isWall(current_point.getRow()-1, current_point.getCol()))
	    {
		current_point = Point(current_point.getRow()-1, current_point.getCol());
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	else if (direction == 315)
	{
	    if (! maze->isWall(current_point.getRow()-1, current_point.getCol()-1))
	    {
		current_point = Point(current_point.getRow()-1, current_point.getCol()-1);
		controlledPath->setCurrentPoint(current_point);
		pw->update();
	    }
	}
	pw->setDirection((float)direction * M_PI / 180.0);
	glutSetWindow(main_window);
	glutPostRedisplay();
    }
    else if (key == GLUT_KEY_LEFT)
    {
	float d = pw->getDirection();
	d += M_PI / 4.0;
	while (d >= 2*M_PI) d -= 2*M_PI;

	pw->setDirection(d);
	
	if (local_view)
	{
	    float dx = cos(d);
	    float dy = sin(d);

	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	    gluLookAt(current_point.getCol(), current_point.getRow(), -10.0,
		      current_point.getCol()+dx, current_point.getRow()+dy, -10.0,
		      0.0, 0.0, 1.0);
	}
	glutSetWindow(main_window);
	glutPostRedisplay();
    }
    else if (key == GLUT_KEY_RIGHT)
    {
	float d = pw->getDirection();
	d -= M_PI / 4.0;
	while (d < 0) d += 2*M_PI;

	pw->setDirection(d);
	
	if (local_view)
	{
	    float dx = cos(d);
	    float dy = sin(d);

	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	    gluLookAt(current_point.getCol(), current_point.getRow(), -10.0,
		      current_point.getCol()+dx, current_point.getRow()+dy, -10.0,
		      0.0, 0.0, 1.0);
	}
	glutSetWindow(main_window);
	glutPostRedisplay(); 
    }
}

void mouse(int button, int state, int x, int y)
{

    if (state == GLUT_DOWN) return;

    if (state == GLUT_UP && button == GLUT_LEFT_BUTTON)
    {
	centerx += (float) x - (float)ww/2;
	centery += (float)y - (float) wh/2;

	float c = (centerx / ww)*maze->getCols();
	float r = (centery / wh)*maze->getRows();

	fovy -= 2.0;

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(fovy, (GLfloat) ww / (GLfloat) wh, 1.0, 500.0);
	glTranslatef(-c, r, -5.0); 
	glMatrixMode(GL_MODELVIEW);
	glutSetWindow(main_window);
	glutPostRedisplay();
	return;
    }

    else if (state==GLUT_UP && button == GLUT_RIGHT_BUTTON);
    {
	float c = (centerx / ww)*maze->getCols();
	float r = (centery / wh)*maze->getRows();

	fovy += 2.0;

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(fovy, (GLfloat) ww / (GLfloat) wh, 1.0, 500.0);
	glTranslatef(-c, r, -5.0);
	glMatrixMode(GL_MODELVIEW);
    }
    glutSetWindow(main_window);
    glutPostRedisplay();
}

void idle()
{
//    glutSetWindow(main_window);
//    glutPostRedisplay();
}

void openFile(int shouldOpen)
{
    if (shouldOpen)
    {
	clearAll();
	delete maze;
	maze = new Maze(fileNameBox->get_text());

	glNewList(MAZE, GL_COMPILE);
	drawMaze();
	glEndList();

	reshape(ww, wh);
	setupPaths();
    }
    openFileWindow->close();
}

void openFileClicked(int button_id)
{
    openFileWindow = GLUI_Master.create_glui("Open File", 0, wx+ww/2, wy+wh/2);
    fileNameBox = openFileWindow->add_edittext( "Filename:",
						GLUI_EDITTEXT_TEXT);
    fileNameBox->set_w(300);
    GLUI_Panel *panel = openFileWindow->add_panel("", GLUI_PANEL_NONE); 
    openFileWindow->add_button_to_panel(panel, "Open", 1, (GLUI_Update_CB) openFile);
    openFileWindow->add_column_to_panel(panel, false);
    openFileWindow->add_button_to_panel(panel, "Cancel", 0, (GLUI_Update_CB) openFile);
    openFileWindow->set_main_gfx_window(main_window); 
}

void stopPaths(int param)
{
    if (showBreadth) { stopBreadth = true; }
    if (showDepth) { stopDepth = true; }
    if (showBest) { stopBest = true; }
    if (showAStar) { stopAStar = true; }
}

void startPaths(int param)
{
    if (showBreadth) { stopBreadth = false; }
    if (showDepth) { stopDepth = false; }
    if (showBest) { stopBest = false; }
    if (showAStar) { stopAStar = false; }
}

void togglePoints(int param)
{
    if (showBreadth) 
    {
	Path* pth = paths[breadth_path_id]->getPath();
	pth->setShowAllPoints(! pth->showAllPoints() );
    }
    if (showBest) 
    {
	Path* pth = paths[best_path_id]->getPath();
	pth->setShowAllPoints(! pth->showAllPoints() );
    }
    if (showDepth)
    {
	Path* pth = paths[depth_path_id]->getPath();
	pth->setShowAllPoints(! pth->showAllPoints() );
    }
    if (showAStar)
    {
	Path* pth = paths[a_star_path_id]->getPath();
	pth->setShowAllPoints(! pth->showAllPoints() );
    }
}

void timer(int param)
{
    Point last_point;

    if (local_view && local_view_id != controlled_path_id)
	last_point = paths[local_view_id]->getCurrPoint();

    if (!stopBreadth) 
    {
	if (paths[breadth_path_id]->getPath()->hasMorePoints())
	    paths[breadth_path_id]->update(); 
    }
    if (!stopDepth) 
    { 
	if (paths[depth_path_id]->getPath()->hasMorePoints())
	    paths[depth_path_id]->update(); 
    }
    if (!stopBest) 
    { 
	if (paths[best_path_id]->getPath()->hasMorePoints())
	    paths[best_path_id]->update(); 
    }
    if (!stopAStar) 
    { 
	if (paths[a_star_path_id]->getPath()->hasMorePoints())
	    paths[a_star_path_id]->update(); 
    }

    if (local_view && local_view_id != controlled_path_id)
    {
	int direction = 0;
	Point next_point = paths[local_view_id]->getCurrPoint();
	if (last_point.getCol() < next_point.getCol())
	    direction = 0;
	else if (last_point.getRow() > next_point.getRow())
	    direction = 90;
	else if (last_point.getCol() > next_point.getCol())
	    direction = 180;
	else if (last_point.getRow() < next_point.getRow())
	    direction = 270;
	paths[local_view_id]->setDirection((float)direction * M_PI / 180.0);
    }

    glutTimerFunc(timer_speed, timer, timer_speed);
    glutSetWindow(main_window);
    glutPostRedisplay();
}

void bubbleTimer(int param)
{
    ((ControlledPath*) paths[controlled_path_id]->getPath())->incrementBubbles();
    glutTimerFunc(param, bubbleTimer, param);
    glutSetWindow(main_window);
    glutPostRedisplay();
}

int main(int argc, char** argv)
{
    char* filename;

    if (argc == 2)
    {
	filename = argv[1];
    }

/*    else
    {
	cout << "Filename: ";
	cin >> filename;
    }
*/
    maze = new Maze(filename);
    
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH );
    glutInitWindowSize(ww, wh);
    glutInitWindowPosition(wx,wy);

    main_window = glutCreateWindow("A - Maze - ing!");

    myInit();
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    
    glutKeyboardFunc(&keyboard);
    glutSpecialFunc(&special);
    glutMouseFunc(&mouse);

    glutTimerFunc(100, timer, 100);
    glutTimerFunc(60, bubbleTimer, 60);

    setupPaths();

    // GLUI Stuff...
    GLUI_Master.set_glutIdleFunc(&idle);
    
    glui = GLUI_Master.create_glui("Paths", 0, ww+wx, wy);
    glui->add_checkbox("Breadth", &showBreadth);
    glui->add_checkbox("Depth", &showDepth);
    glui->add_checkbox("Best", &showBest);
    glui->add_checkbox("A*", &showAStar);
    glui->add_button("Start", 1, startPaths);

    glui->add_button("Stop", 0, stopPaths);
    glui->add_button("Clear", 1, clearPaths);
    glui->add_button("Toggle All Points", 1, togglePoints);

    glui->add_separator();
    glui->add_button("Clear All", -1, clearAllRestart);

    glui->add_separator();

    GLUI_Panel *views_panel = glui->add_panel("View", GLUI_PANEL_EMBOSSED);
    GLUI_RadioGroup *views_group = 
	glui->add_radiogroup_to_panel( views_panel,
				       &view_id,
				       -1,
				       (GLUI_Update_CB) selectView );
    glui->add_radiobutton_to_group(views_group, "Full View");
    glui->add_radiobutton_to_group(views_group, "Teapot");
    glui->add_radiobutton_to_group(views_group, "Breadth First");
    glui->add_radiobutton_to_group(views_group, "Depth First");
    glui->add_radiobutton_to_group(views_group, "Best First");
    glui->add_radiobutton_to_group(views_group, "A*");

    glui->add_separator();

    GLUI_Spinner* speed_spinner = glui->add_spinner("Speed (ms / move)", GLUI_SPINNER_INT,
						    &timer_speed);
    speed_spinner->set_int_limits(10, 10000);
    speed_spinner->set_speed( 0.2 );

    glui->add_separator();

    glui->add_button("Open File", 1, openFileClicked);

    glui->set_main_gfx_window(main_window);
    glutSetWindow(main_window);
    glutShowWindow();
    glutMainLoop();

    return 0;
}
