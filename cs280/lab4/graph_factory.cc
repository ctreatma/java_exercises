#include "graph_factory.h"

class MatrixGraph;
class ListGraph;

using namespace std;

GraphFactory::GraphFactory(int graphType) {
  this->graphType = graphType;
}


Graph* GraphFactory::createGraph() {

  Graph* G;
  switch (graphType) {
    case 0 : G = new MatrixGraph();
             break;
    case 1 : G = new ListGraph();
             break;
    default : cerr << "Bad input for graph factory." << endl;
              exit(1);
  }

  return G;
}

Graph* GraphFactory::createGraph(char* file) {

  Graph* G;
  switch (graphType) {
    case 0 : G = new MatrixGraph();
             break;
    case 1 : G = new ListGraph();
             break;
    default : cerr << "Bad input for graph factory." << endl;
              exit(1);
  }

  G->read(file);
  return G;
}



