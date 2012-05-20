CC=g++
FLAGS= -g

LIBS=-L/usr/X11R6/lib -L/usr/public/lib -lglui -lglut -lGL -lGLU -lm -lXmu
INCS=-I/usr/public/include

maze_viewer: maze_viewer.o path.o maze.o
	${CC} ${FLAGS} path.o maze.o maze_viewer.o ${LIBS} -o maze_viewer

maze.o: maze.cc maze.h
	${CC} ${FLAGS} -c maze.cc

maze_maker: maze_maker.o
	${CC} ${FLAGS} maze_maker.o ${LIBS} -o maze_maker 

maze_maker.o: maze_maker.cc
	${CC} ${FLAGS} ${INCS} -c maze_maker.cc

maze_viewer.o: maze_viewer.cc path.h maze.h
	${CC} ${FLAGS} ${INCS} -c maze_viewer.cc

path.o: path.cc path.h maze.o
	${CC} ${FLAGS} ${INCS} maze.cc -c path.cc
clean:
	rm *.o
	rm maze_viewer
	rm maze_maker
