// graph_factory.cc
using namespace std;

#include "graph_factory.h"
#include <fstream>

class MatrixGraph;
class ListGraph;

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

Graph* GraphFactory::createGraph(unsigned capacity) {

  Graph* G;
  switch (graphType) {
    case 0 : G = new MatrixGraph(capacity);
             break;
    case 1 : G = new ListGraph(capacity);
             break;
    default : cerr << "Bad input for graph factory." << endl;
              exit(1);
  }

  return G;
}

Graph* GraphFactory::createGraph(char* file) {

  Graph* G;

  ifstream fin(file);
  int n;
  fin >> n;

  switch (graphType) {
    case 0 : G = new MatrixGraph(n);
             break;
    case 1 : G = new ListGraph(n);
             break;
    default : cerr << "Bad input for graph factory." << endl;
              exit(1);
  }
  fin.close();

  G->read(file);
  return G;
}





