//Charles Treatman ctreatma
//graph_algs.h

#include "graph.h"
#include "graph_factory.h"

#ifndef GRAPH_ALGS_H
#define GRAPH_ALGS_H

Graph* Kruskal(Graph* G, GraphFactory* GF);
void updateFlags(unsigned p, unsigned q, vector<unsigned>& flags);
Graph* Prim(Graph* G, GraphFactory* GF);
int minDistance(vector<double>& distance, vector<long>& parent);

#endif
