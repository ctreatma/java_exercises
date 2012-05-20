#include <GL/glut.h>
#include <GL/glui.h>
#include <fstream>
#include <math.h>
#include <vector>
#include <string>

using namespace std;

#define ATTRIBUTE_BYTES 8  // 1 for flags, 3 for color, 4 empty for now...

#define PI 3.14159265

#define WALL_FLAG 1
#define START_FLAG 2
#define GOAL_FLAG 128

#define NEW_MENU_ENTRY 100 
#define OPEN_MENU_ENTRY 110
#define SAVE_MENU_ENTRY 120

#define ICON_RADIOGROUP_TAG 300

#define WALL_MENU_ENTRY 500
#define START_MENU_ENTRY 501
#define GOAL_MENU_ENTRY 502
#define CLEAR_MENU_ENTRY 550

#define drawBox(p1, p2) glBegin(GL_POLYGON); \
   glVertex2f((p1).getx(),(p1).gety()); \
   glVertex2f((p1).getx(),(p2).gety()); \
   glVertex2f((p2).getx(),(p2).gety()); \
   glVertex2f((p2).getx(),(p1).gety()); \
   glEnd();


class Point
{
private:
	float x,y;
public:
	Point(){}
	Point(float a,float b){ x=a; y=b; }
	float getx(){return x;}
	float gety(){return y;}
	void move(Point &p);
};

class Color // stores colors as ints from 0 to 255
{
private:
    int red;
    int green;
    int blue;
    float glColors[3];

public:
    Color()
    {
	this->red = this->green = this->blue = 0;
    }
    Color(int r, int g, int b)
    {
	this->red = r;
	this->green = g;
	this->blue = b;
    }

    static float colorIntToFloat(int color)
    {
	return (float) ((float)color / 255.0);
    }

    static int colorFloatToInt(float color)
    {
	return (int) (color * 255);
    }

    int getRed()
    {
	return red;
    }

    int getGreen()
    {
	return green;
    }

    int getBlue()
    {
	return blue;
    }

    float* getGLColorArray()
    {
	glColors[0] =colorIntToFloat(red);
	glColors[1] =colorIntToFloat(green);
	glColors[2] =colorIntToFloat(blue);
	return glColors;
    }

    void setRed(int r)
    {
	red = r;
    }

    void setGreen(int g)
    {
	green = g;

    }

    void setBlue(int b)
    {
	blue = b;
    }
};

class Icon
{
public:
    virtual void draw(Point& upper_left, Point& lower_right) = 0;
    virtual int getFlag() const = 0;
};

class EmptyIcon : public Icon
{
public:
    EmptyIcon() {}

    void draw(Point& upper_left, Point& lower_right)
    {
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	drawBox(upper_left, lower_right);
    }

    int getFlag() const
    {
	return 0;
    }
};

class WallIcon : public Icon
{
private:
    int x1, y1, x2, y2;

public:
    WallIcon() {}
    void draw(Point& upper_left, Point& lower_right)
    {
	x1 = (int)upper_left.getx();
	y1 = (int)upper_left.gety();
	x2 = (int)lower_right.getx();
	y2 = (int)lower_right.gety();
	
	glBegin(GL_LINES);
	glVertex2f((x1+x2)/2, y1);
	glVertex2f((x1+x2)/2, y2);
	glVertex2f(x1, (y1+y2)/2);
	glVertex2f(x2, (y1+y2)/2);
	glVertex2f(x1, y1);
	glVertex2f(x2, y2);
	glVertex2f(x2, y1);
	glVertex2f(x1, y2);
	glEnd();
    }

    int getFlag() const
    {
	return WALL_FLAG;
    }
};

class StartIcon : public Icon
{
private:
    int x1, y1, x2, y2;

public:
    StartIcon() {}

    void draw(Point& upper_left, Point& lower_right)
    {
	x1 = (int)upper_left.getx();
	y1 = (int)upper_left.gety();
	x2 = (int)lower_right.getx();
	y2 = (int)lower_right.gety();
	
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	glBegin(GL_POLYGON);
	glVertex2f((x1+x2)/2, y1);
	glVertex2f(x2, (y1+y2)/2);
	glVertex2f((x1+x2)/2, y2);
	glVertex2f(x1, (y1+y2)/2);
	glEnd();
    }

    int getFlag() const
    {
	return START_FLAG;
    }
};

class GoalIcon : public Icon
{
private:
    int x1, y1, x2, y2;

public:
    GoalIcon() {}

    void draw(Point& upper_left, Point& lower_right)
    {
	x1 = (int)upper_left.getx();
	y1 = (int)upper_left.gety();
	x2 = (int)lower_right.getx();
	y2 = (int)lower_right.gety();
	
	glBegin(GL_LINE_LOOP);
	glVertex2f(x1, y2);
	glVertex2f((x1+x2)/2, y1);
	glVertex2f(x2, y2);
	glVertex2f(x1,(2*y1+y2)/3);
	glVertex2f(x2,(2*y1+y2)/3);
	glEnd();
    }

    int getFlag() const
    {
	return GOAL_FLAG;
    }
};
// ****************************************** //

int main_window;
GLUI *glui, *saveFileWindow, *openFileWindow;
GLUI_Spinner *row_spinner;
GLUI_Spinner *col_spinner;
GLUI_RadioGroup *icon_rg;
GLUI_EditText *fileNameBox;

GLint ww=650,wh=600;
GLint wx=100, wy=100;

int rows = 25, cols = 30;

vector<vector<vector<int> > > maze(rows);

WallIcon wallIcon;
StartIcon startIcon;
GoalIcon goalIcon;
EmptyIcon emptyIcon;

Icon *drawIcon = &wallIcon;
int currentColor[] = {0, 255, 0};
int iconSelection = 0;

int isDown = 0;
int lastRow = -1, lastCol = -1;

int mouseMenu;

void myinit(void)
{
	glClearColor(0.0,0.0,0.0,0.0);
	gluOrtho2D(0.0,(GLdouble)ww,(GLdouble)wh, 0.0);
}


void display(void)
{ 
    glClear(GL_COLOR_BUFFER_BIT);

    float dx = ww / (float)cols;
    float dy = wh / (float)rows;

    // draw the grid
    for (int r = 0; r < rows; r++)
    {
	for(int c = 0; c < cols; c++)
	{
	    Point p1(dx*(float)c, dy*(float)r);
	    Point p2(dx*(float)(c+1), dy*(float)(r+1));
	    
	    Icon *tempIcon = &emptyIcon;

	    if (maze[r][c][0] & WALL_FLAG)
		tempIcon = &wallIcon;
	    else if (maze[r][c][0] & START_FLAG)
		tempIcon = &startIcon;
	    else if (maze[r][c][0] & GOAL_FLAG)
		tempIcon = &goalIcon;
	    
	    float red = Color::colorIntToFloat(maze[r][c][1]);
	    float green = Color::colorIntToFloat(maze[r][c][2]);
	    float blue = Color::colorIntToFloat(maze[r][c][3]);

	    glColor3f(red, green, blue);

	    tempIcon->draw(p1, p2);
	    
	    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	    glColor3f(0.0, 0.0, 1.0);
	    drawBox(p1, p2);
	}
    }
    glui->sync_live();
    glutSwapBuffers();
}

void mouse(int button, int state, int x, int y)
{
    if(button==GLUT_LEFT_BUTTON && state==GLUT_DOWN)
    {
	if (isDown) return;
	isDown = 1;
	int row = lastRow = (int)floor((float)y / ((float)wh / (float)rows));
	int col = lastCol = (int)floor((float)x / ((float)ww / (float)cols));
	maze[row][col][0] = drawIcon->getFlag();
	maze[row][col][1] = currentColor[0];
	maze[row][col][2] = currentColor[1];
	maze[row][col][3] = currentColor[2];
    }
    
    else if (button == GLUT_LEFT_BUTTON && state==GLUT_UP)
    {
	isDown = 0;
    }

    glutPostRedisplay();
}

void motion(int x,int y)
{
    if (x <= 0 || x > ww || y <= 0 || y > wh)
	return;

    if (isDown)
    {
	int row = (int)floor((float)y / ((float)wh / (float)rows));
	int col = (int)floor((float)x / ((float)ww / (float)cols));
	if (0 <= row && row < rows && 
	    0 <= col && col < cols &&
	    (row != lastRow || col != lastCol))
	{
	    maze[row][col][0] = drawIcon->getFlag();
	    maze[row][col][1] = currentColor[0];
	    maze[row][col][2] = currentColor[1];
	    maze[row][col][3] = currentColor[2];   
	    lastRow = row; lastCol = col;
	}
    }
    glutPostRedisplay();
}

void reshape(int w,int h)
{
    ww = w; wh = h;
    glViewport(0,0,ww,wh);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    gluOrtho2D(0.0,(GLdouble)ww,(GLdouble)wh,0.0);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    glutPostRedisplay();
}

void setSize(int id)
{
    while (rows > maze.size())
    {
	vector<int> stub(ATTRIBUTE_BYTES);
	vector<vector<int> > stub2(cols, stub);
	maze.push_back(stub2);
    }

    while (cols > maze[0].size())
    {
	for (int i = 0; i < maze.size(); i++)
	{
	    maze[i].push_back(vector<int>(ATTRIBUTE_BYTES));
	}
    }

    glutPostRedisplay();
}

void saveFile(int shouldSave)
{
    if (shouldSave)
    {
	ofstream output(fileNameBox->get_text());
	output.put( (char) (rows / 256));
	output.put( (char) rows);
	output.put( (char) (cols / 256));
	output.put( (char) cols);
	
	// 12 empty bytes for possible future use
	for (int b = 0; b < 12; b++)
	{
	    output.put(0);
	}
	
	for (int r = 0; r < rows; r++)
	{
	    for (int c = 0; c < cols; c++)
	    {
		for (int a = 0; a < ATTRIBUTE_BYTES; a++)
		{
		    output.put( (char) maze[r][c][a]);
		}
	    }
	}
    }
    saveFileWindow->close();
}

void openFile(int shouldOpen)
{
    if (shouldOpen)
    {
	ifstream input(fileNameBox->get_text());
	rows = input.get();
	rows = rows * 256 + input.get();
	cols = input.get();
	cols = cols * 256 + input.get();
	setSize(-1);
	
	// 12 empty bytes
	for (int b = 0; b < 12; b++)
	    input.get();
	
	for (int r = 0; r < rows; r++)
	{
	    for (int c = 0; c < cols; c++)
	    {
		for (int a = 0; a < ATTRIBUTE_BYTES; a++)
		{
		    maze[r][c][a] = input.get();
		}
	    }
	}
    }
    openFileWindow->close();
}

void userSelection(int id)
{
    if (id == ICON_RADIOGROUP_TAG)
    {
	int tool = icon_rg->get_int_val();
	if (tool == 0)
	    id = WALL_MENU_ENTRY;
	else if (tool == 1)
	    id = START_MENU_ENTRY;
	else if (tool == 2)
	    id = GOAL_MENU_ENTRY;
	else if (tool == 3)
	    id = CLEAR_MENU_ENTRY;
    }

    if (id == NEW_MENU_ENTRY)
    {
	for (int i = 0; i < maze.size(); i++)
	{
	    for (int j = 0; j < maze[i].size(); j++)
	    {
		for (int k = 0; k < maze[i][j].size(); k++)
		{
		    maze[i][j][k] = 0;
		}
	    }
	}
    }

    else if (id == OPEN_MENU_ENTRY)
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

    else if (id == SAVE_MENU_ENTRY)
    {
	saveFileWindow = GLUI_Master.create_glui("Save File", 0, wx+ww/2, wy+wh/2);
	fileNameBox = saveFileWindow->add_edittext( "Filename:",
								   GLUI_EDITTEXT_TEXT);
	fileNameBox->set_w(300);
	GLUI_Panel *panel = saveFileWindow->add_panel("", GLUI_PANEL_NONE); 
	saveFileWindow->add_button_to_panel(panel, "Save", 1, (GLUI_Update_CB) saveFile);
	saveFileWindow->add_column_to_panel(panel, false);
	saveFileWindow->add_button_to_panel(panel, "Cancel", 0, (GLUI_Update_CB) saveFile);
	saveFileWindow->set_main_gfx_window(main_window);	
    }

    else if (id == WALL_MENU_ENTRY)
    {
	drawIcon = &wallIcon;
	glutChangeToMenuEntry(5, "*Wall", WALL_MENU_ENTRY);
	glutChangeToMenuEntry(6, "Start", START_MENU_ENTRY);
	glutChangeToMenuEntry(7, "Goal", GOAL_MENU_ENTRY);
	glutChangeToMenuEntry(8, "Floor", CLEAR_MENU_ENTRY);
	iconSelection = 0;
	glui->sync_live();
	glui->enable();
    }
    else if (id == START_MENU_ENTRY)
    {
	drawIcon = &startIcon;
	glutChangeToMenuEntry(5, "Wall", WALL_MENU_ENTRY);
	glutChangeToMenuEntry(6, "*Start", START_MENU_ENTRY);
	glutChangeToMenuEntry(7, "Goal", GOAL_MENU_ENTRY);
	glutChangeToMenuEntry(8, "Floor", CLEAR_MENU_ENTRY);
	iconSelection = 1;
	glui->sync_live();
	glui->enable();
    }
    else if (id == GOAL_MENU_ENTRY)
    {
	drawIcon = &goalIcon;
	glutChangeToMenuEntry(5, "Wall", WALL_MENU_ENTRY);
	glutChangeToMenuEntry(6, "Start", START_MENU_ENTRY);
	glutChangeToMenuEntry(7, "*Goal", GOAL_MENU_ENTRY);
	glutChangeToMenuEntry(8, "Floor", CLEAR_MENU_ENTRY);
	iconSelection = 2;
	glui->sync_live();
	glui->enable();
    }
    else if (id == CLEAR_MENU_ENTRY)
    {
	drawIcon = &emptyIcon;
	glutChangeToMenuEntry(5, "Wall", WALL_MENU_ENTRY);
	glutChangeToMenuEntry(6, "Start", START_MENU_ENTRY);
	glutChangeToMenuEntry(7, "Goal", GOAL_MENU_ENTRY);
	glutChangeToMenuEntry(8, "*Floor", CLEAR_MENU_ENTRY);
	iconSelection = 3;
	glui->sync_live();
	glui->enable();
    }
    glutPostRedisplay();
}

int main(int argc,char**argv)
{
	glutInit(&argc,argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);
	glutInitWindowPosition(wx,wy);
	glutInitWindowSize(ww,wh);

	main_window = glutCreateWindow("CS280 -- Maze Maker");
	glutSetCursor(GLUT_CURSOR_SPRAY);
	glutDisplayFunc(display);
	glutMotionFunc(motion);
	
	// GLUI Code
	glui= GLUI_Master.create_glui("Tools", 0, wx+ww, wy);
	glui->set_main_gfx_window(main_window);
	GLUI_Master.set_glutReshapeFunc(reshape);
	GLUI_Master.set_glutMouseFunc(mouse);

	// maze size
	row_spinner = glui->add_spinner("Rows:", GLUI_SPINNER_INT,
					&rows, 0, (GLUI_Update_CB) setSize);
	row_spinner->set_float_limits(1, 511);
	row_spinner->set_speed(0.1);
	row_spinner->set_alignment(GLUI_ALIGN_RIGHT);

	col_spinner = glui->add_spinner("Columns:", GLUI_SPINNER_INT,
					&cols, 1, (GLUI_Update_CB) setSize);
	col_spinner->set_float_limits(1, 511);
	col_spinner->set_speed(1.0);
	col_spinner->set_alignment(GLUI_ALIGN_RIGHT);

	// color
	GLUI_Panel *colors_panel = glui->add_rollout("Colors:");
	GLUI_Spinner *red_spinner = glui->add_spinner_to_panel(colors_panel, "Red:",
							       GLUI_SPINNER_INT,
							       &currentColor[0], 0, 0);
	red_spinner->set_int_limits(0, 255, GLUI_LIMIT_CLAMP);
	GLUI_Spinner *green_spinner = glui->add_spinner_to_panel(colors_panel, "Green:",
								 GLUI_SPINNER_INT,
								 &currentColor[1], 1, 0);
	green_spinner->set_int_limits(0, 255, GLUI_LIMIT_CLAMP);
	GLUI_Spinner *blue_spinner = glui->add_spinner_to_panel(colors_panel, "Blue:",
								GLUI_SPINNER_INT,
								&currentColor[2], 2, 0);
	blue_spinner->set_int_limits(0, 255, GLUI_LIMIT_CLAMP);

	// Icon chooser
	GLUI_Panel *icon_panel = glui->add_rollout("Object:");
	icon_rg = glui->add_radiogroup_to_panel(icon_panel, &iconSelection, 
						ICON_RADIOGROUP_TAG,
						(GLUI_Update_CB) userSelection);
	glui->add_radiobutton_to_group(icon_rg, "Wall");
	glui->add_radiobutton_to_group(icon_rg, "Start");
	glui->add_radiobutton_to_group(icon_rg, "Goal");
	glui->add_radiobutton_to_group(icon_rg, "Floor");

	// exit button
	glui->add_button("Quit", 0, (GLUI_Update_CB) exit);

	
	// MENUS
	mouseMenu = glutCreateMenu(userSelection);
	glutAddMenuEntry("New", NEW_MENU_ENTRY);
	glutAddMenuEntry("Open", OPEN_MENU_ENTRY);
	glutAddMenuEntry("Save", SAVE_MENU_ENTRY);
	glutAddMenuEntry("", -1);
	glutAddMenuEntry("*Wall", WALL_MENU_ENTRY); // selected by default
	glutAddMenuEntry("Start", START_MENU_ENTRY);
	glutAddMenuEntry("Goal", GOAL_MENU_ENTRY);
	glutAddMenuEntry("Floor", CLEAR_MENU_ENTRY);
	glutAttachMenu(GLUT_RIGHT_BUTTON);

	setSize(-1);
	myinit();
	glutMainLoop();
	return 0;
}
