CC = gcc
OPTS = -Wall
OBJS = cypher.o char_fun.o


cypher: ${OBJS}
	${CC} ${OPTS} -o cypher ${OBJS}


cypher.o: cypher.c
	${CC} ${OPTS} -c cypher.c


char_fun.o: char_fun.c char_fun.h
	${CC} ${OPTS} -c char_fun.c

clean:
	rm cypher *.o
