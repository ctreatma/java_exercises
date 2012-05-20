// generate_graph.h

#include "graph.h"
#include "graph_factory.h"

#ifndef CS280_GENERATE_GRAPH_H
#define CS280_GENERATE_GRAPH_H

Graph* completeGraph(unsigned size, double weight, GraphFactory* GF);
Graph* randomCompleteGraph(unsigned size, double minWeight, double maxWeight, GraphFactory* GF);
Graph* randomSparseGraph(unsigned size, double minWeight, double maxWeight, GraphFactory* GF);

#endif
