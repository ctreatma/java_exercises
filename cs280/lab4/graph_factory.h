// graphFactory.h

#include "matrix_graph.h"
#include "list_graph.h"

#include <iostream>

#ifndef CS280_GRAPH_FACTORY_H
#define CS280_GRAPH_FACTORY_H

class GraphFactory {
public:
  GraphFactory(int type);
  Graph* createGraph();
  Graph* createGraph(char* file);

private:
  int graphType;
};

#endif



