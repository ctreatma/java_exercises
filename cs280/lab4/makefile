CC = g++
OPTS = -Wall -g

GRAPH_OBJECT_CODE = graph.o matrix_graph.o list_graph.o node.o graph_factory.o
RANDOM_GRAPH_OBJECT_CODE = generate_graph.o random_fun.o

test_code: test-code.o ${GRAPH_OBJECT_CODE} graph_algs.o
	${CC} ${OPTS} -o test_code test-code.o ${GRAPH_OBJECT_CODE} graph_algs.o

test-code.o: test-code.cc graph_factory.h graph.h graph_algs.h
	${CC} ${OPTS} -c test-code.cc

sec_net: sec_net.o ${GRAPH_OBJECT_CODE} graph_algs.o
	${CC} ${OPTS} -o sec_net sec_net.o ${GRAPH_OBJECT_CODE} graph_algs.o

sec_net.o: sec_net.cc graph_factory.h graph.h graph_algs.h
	${CC} ${OPTS} -c sec_net.cc

graph_algs.o: graph_algs.cc graph_algs.h graph.h graph_factory.h
	${CC} ${OPTS} -c graph_algs.cc

basic_test: basic_test.o ${GRAPH_OBJECT_CODE}
	${CC} ${OPTS} -o basic_test basic_test.o ${GRAPH_OBJECT_CODE}

basic_test.o: basic_test.cc graph.h graph_factory.h
	${CC} ${OPTS} -c basic_test.cc

graph_gen: graph_gen.o ${RANDOM_GRAPH_OBJECT_CODE} ${GRAPH_OBJECT_CODE}
	${CC} ${OPTS} -o graph_gen graph_gen.o ${RANDOM_GRAPH_OBJECT_CODE} ${GRAPH_OBJECT_CODE}

graph_gen.o: graph_gen.cc graph.h graph_factory.h generate_graph.h
	${CC} ${OPTS} -c graph_gen.cc

generate_graph.o: generate_graph.cc generate_graph.h graph.h graph_factory.h random_fun.h
	${CC} ${OPTS} -c generate_graph.cc

graph_factory.o: graph_factory.cc graph_factory.h graph.h
	${CC} ${OPTS} -c graph_factory.cc

list_graph.o: list_graph.cc list_graph.h graph.h node.h
	${CC} ${OPTS} -c list_graph.cc

matrix_graph.o: matrix_graph.cc matrix_graph.h graph.h node.h
	${CC} ${OPTS} -c matrix_graph.cc

graph.o: graph.cc graph.h node.h
	${CC} ${OPTS} -c graph.cc

node.o: node.cc node.h
	${CC} ${OPTS} -c node.cc

random_fun.o: random_fun.c random_fun.h
	${CC} ${OPTS} -c random_fun.c

clean: 
	rm *.o


