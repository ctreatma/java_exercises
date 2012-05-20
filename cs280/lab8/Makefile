CC=g++
#FLAGS=-D_REENTRANT -g \
#	-DMANCALA_SERVER_DEBUG -DMANCALA_IO_DEBUG \
#	-DMANCALA_MESSAGE_DEBUG #-DMANCALA_NET_DEBUG
FLAGS=-D_REENTRANT -g

LIBS=-L/usr/public/lib -lSDL -lSDL_net -lpthread -Wl,-R/usr/public/lib
INCS=-I/usr/public/include

mancala: server.o io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o \
	mancala.cc
	${CC} ${FLAGS} ${INCS} -o mancala ${LIBS} \
	mancala.cc \
	server.o io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o

server_test: server.o io.o messages.o server_test.cc \
	game_board.o rule_set.o tcputil.o exceptions.o player.o minimax.o
	${CC} ${FLAGS} ${INCS} ${LD_FLAGS} -o server_test ${LIBS} \
	server_test.cc \
	server.o io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o

random_fun.o: Makefile random_fun.c random_fun.h
	${CC} ${FLAGS} ${INCS} -c random_fun.c

exceptions.o: Makefile exceptions.cc exceptions.cc exceptions.h
	${CC} ${FLAGS} ${INCS} -c exceptions.cc

io.o: Makefile io.h io.cc messages.h player.h
	${CC} ${FLAGS} ${INCS} -c io.cc

messages.o: Makefile messages.h messages.cc
	${CC} ${FLAGS} ${INCS} -c messages.cc

server.o: Makefile server.h server.cc io.h messages.h
	${CC} ${FLAGS} ${INCS} -c server.cc

player.o: Makefile player.h player.cc
	${CC} ${FLAGS} ${INCS} -c player.cc

minimax.o: Makefile minimax.h minimax.cc player.h
	${CC} ${FLAGS} ${INCS} -c minimax.cc

game_board.o: Makefile rule_set.cc rule_set.h game_board.h \
	game_board.cc
	${CC} ${FLAGS} ${INCS} -c game_board.cc

rule_set.o: Makefile rule_set.cc rule_set.h exceptions.cc exceptions.h
	${CC} ${FLAGS} ${INCS} -c rule_set.cc

tcputil.o: Makefile tcputil.h tcputil.cc
	${CC} ${FLAGS} ${INCS} -c tcputil.cc


client_test: io.o messages.o client_test.cc \
	game_board.o rule_set.o tcputil.o exceptions.o player.o minimax.o
	${CC} ${FLAGS} ${INCS} ${LD_FLAGS} -o client_test ${LIBS} \
	client_test.cc \
	io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o

client1_test: io.o messages.o client1_test.cc \
	game_board.o rule_set.o tcputil.o exceptions.o player.o minimax.o
	${CC} ${FLAGS} ${INCS} ${LD_FLAGS} -o client1_test ${LIBS} \
	client1_test.cc \
	io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o

client2_test: io.o messages.o client2_test.cc \
	game_board.o rule_set.o tcputil.o exceptions.o player.o minimax.o
	${CC} ${FLAGS} ${INCS} ${LD_FLAGS} -o client2_test ${LIBS} \
	client2_test.cc \
	io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o

client3_test: io.o messages.o client3_test.cc \
	game_board.o rule_set.o tcputil.o exceptions.o player.o minimax.o
	${CC} ${FLAGS} ${INCS} ${LD_FLAGS} -o client3_test ${LIBS} \
	client3_test.cc \
	io.o messages.o game_board.o rule_set.o \
	tcputil.o exceptions.o player.o minimax.o random_fun.o

clean:
	rm *.o
	cleanemacs
	rm mancala
