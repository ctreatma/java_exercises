CC = g++
OPTS = -Wall -g

SORTERS_H = sorter.h bubble_sorter.h merge_sorter.h insertion_sorter.h \
	    quick_sorter.h selection_sorter.h sorter_factory.h
SORTERS_O = sorter.o bubble_sorter.o merge_sorter.o insertion_sorter.o \
	    quick_sorter.o selection_sorter.o sorter_factory.o
check: check.o ${SORTERS_O} random_fun.o
	${CC} ${OPTS} -o check check.o ${SORTERS_O} random_fun.o

check.o: check.cc ${SORTERS_H} random_fun.h
	${CC} ${OPTS} -c check.cc

analyze2: analyze2.o ${SORTERS_O} random_fun.o
	${CC} ${OPTS} -o analyze2 analyze2.o ${SORTERS_O} random_fun.o

analyze2.o: analyze2.cc ${SORTERS_H} random_fun.h
	${CC} ${OPTS} -c analyze2.cc

analyze: analyze.o ${SORTERS_O} random_fun.o
	${CC} ${OPTS} -o analyze analyze.o ${SORTERS_O} random_fun.o

analyze.o: analyze.cc ${SORTERS_H} random_fun.h
	${CC} ${OPTS} -c analyze.cc

quick_sorter.o: sorter.h quick_sorter.h quick_sorter.cc
	${CC} ${OPTS} -c quick_sorter.cc

merge_sorter.o: sorter.h merge_sorter.h merge_sorter.cc
	${CC} ${OPTS} -c merge_sorter.cc

selection_sorter.o: sorter.h selection_sorter.h selection_sorter.cc
	${CC} ${OPTS} -c selection_sorter.cc

insertion_sorter.o: sorter.h insertion_sorter.h insertion_sorter.cc
	${CC} ${OPTS} -c insertion_sorter.cc

bubble_sorter.o: sorter.h bubble_sorter.h bubble_sorter.cc
	${CC} ${OPTS} -c bubble_sorter.cc

sorter_factory.o:${SORTERS_H} sorter_factory.h sorter_factory.cc
	${CC} ${OPTS} -c sorter_factory.cc

sorter.o: sorter.h sorter.cc
	${CC} ${OPTS} -c sorter.cc

random_fun.o: random_fun.c random_fun.h
	${CC} ${OPTS} -c random_fun.c

clean: 
	rm *.o
