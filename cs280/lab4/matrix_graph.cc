//Charles Treatman ctreatma
//matrix_graph.cc

#include "matrix_graph.h"

using namespace std;

MatrixGraph::MatrixGraph() {
  num_nodes = 0;
  num_edges = 0;

  nodes = new vector<Node*>();
}

MatrixGraph::~MatrixGraph() {
  this->clear();

  delete &adjMat;
  delete [] nodes;
}

unsigned MatrixGraph::degree(Node* n) const {
  checkNode(n);
  int degree = 0;
  for (int i = 0; i < num_nodes; ++i) {
    if (adjMat[n->getIndex()][i] > 0)
      degree++;
  }

  return degree;
}

bool MatrixGraph::isAdj(Node* n1, Node* n2) const {
  checkNode(n1);
  checkNode(n2);

  return (adjMat[n1->getIndex()][n2->getIndex()] > 0);
}

double MatrixGraph::getWeight(Node* n1, Node* n2) const {
  if (isAdj(n1, n2))
    return adjMat[n1->getIndex()][n2->getIndex()];
  else {
    cerr << "Error:  getWeight: non-adjacent nodes.\n";
    exit(1);
  }
}

double MatrixGraph::totalWeight() const {
  double total = 0;
  for (int i = 0; i < num_nodes; ++i) {
    for (int j = 0; j < num_nodes; ++j)
      total += adjMat[i][j];
  }
  return total/2;
}

list<pair<Node*,double> > MatrixGraph::adjNodes(Node* n) const {
  using std::pair;
  checkNode(n);
  
  list<pair<Node*,double> >* temp = new list<pair<Node*, double> >();
  for (int i = 0; i < num_nodes; ++i) {
    if (adjMat[n->getIndex()][i] > 0) 
      temp->push_back(pair<Node*,double>((*nodes)[i],
					 adjMat[n->getIndex()][i]));
  }
  
  return *temp;
}

Node* MatrixGraph::addNode(char* label) {
  Node* temp = new Node(num_nodes, label, this);
  nodes->push_back(temp);
  num_nodes++;
  adjMat.resize(num_nodes);
  for (int i = 0; i < num_nodes; ++i) {
    adjMat[i].resize(num_nodes);
  }
  return temp;
}

void MatrixGraph::addEdge(Node* n1, Node* n2, double weight) {
  if (!isAdj(n1, n2)) {
    adjMat[n1->getIndex()][n2->getIndex()] = weight;
    adjMat[n2->getIndex()][n1->getIndex()] = weight;
    num_edges++;
  }
  else {
    cerr << "Error: addEdge: edge already exists.";
    exit(1);
  }
}

void MatrixGraph::changeWeight(Node* n1, Node* n2, double weight) {
  if (isAdj(n1, n2)) {
    adjMat[n1->getIndex()][n2->getIndex()] = weight;
    adjMat[n1->getIndex()][n2->getIndex()] = weight;
  }
  else {
    cerr << "Error: changeWeight: non-adjacent nodes.\n";
    exit(1);
  }
}
  
void MatrixGraph::removeEdge(Node* n1, Node* n2) {
  if (isAdj(n1, n2)) {
    this->changeWeight(n1, n2, 0);
    num_edges--;
  }
  else {
    cerr << "Error: removeEdge: non-adjacent nodes.\n";
    exit(1);
  }
}

void MatrixGraph::clearEdges() {
  for (int i = 0; i < num_nodes; ++i) {
    for (int j = 0; j < num_nodes; ++j)
      adjMat[i][j] = 0;
  }
  num_edges = 0;
}

void MatrixGraph::clear() {
  for (int i = 0; i < num_nodes; ++i) {
    adjMat[i].clear();
  }
  adjMat.clear();
  nodes->clear();
  num_edges = 0;
  num_nodes = 0;
}
