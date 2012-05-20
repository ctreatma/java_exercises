//Charles Treatman ctreatma
//list_graph.cc

#include "list_graph.h"
using namespace std;

ListGraph::ListGraph() {
  num_nodes = 0;
  num_edges = 0;

  nodes = new vector<Node*>();
  adjVec = new vector<list<pair<Node*,double> > >();
}

ListGraph::ListGraph(int capacity) {
  num_nodes = 0;
  num_edges = 0;
  
  nodes = new vector<Node*>();
  adjVec = new vector<list<pair<Node*,double> > >();
  nodes->reserve(capacity);
  cap = capacity;
}

ListGraph::~ListGraph() {
  this->clear();
  delete [] nodes;
  delete [] adjVec;
}

unsigned ListGraph::degree(Node* n) const {
  checkNode(n);
  return (*adjVec)[n->getIndex()].size();
}

bool ListGraph::isAdj(Node* n1, Node* n2) const {
  checkNode(n1);
  checkNode(n2);
  list<pair<Node*,double> >::iterator start, finish;

  start = (*adjVec)[n1->getIndex()].begin();
  finish = (*adjVec)[n1->getIndex()].end();
  while(start != finish) {
    if(start->first == n2) {
      return true;
      }
    ++start;
  }
  
  return false;
}

double ListGraph::getWeight(Node* n1, Node* n2) const {
  checkNode(n1);
  checkNode(n2);

  list<pair<Node*,double> >::iterator start = (*adjVec)[n1->getIndex()].begin();
  list<pair<Node*,double> >::iterator finish = (*adjVec)[n1->getIndex()].end();

  while(start != finish) {
    if(start->first == n2)	{
      return start->second;
    }
    ++start;
  }
  cerr << "Error: getWeight: Non-adjacent nodes.\n";
  exit(1);
}

double ListGraph::totalWeight() const {
  double total = 0;
  list<pair<Node*,double> >::iterator start, finish;
  for(int i = 0; i < num_nodes; i++) {
    start = (*adjVec)[i].begin();
    finish = (*adjVec)[i].end();
    while(start != finish) {
      total += start->second;
      ++start;
    }
  }
  return total/2;
}

list<pair<Node*,double> > ListGraph::adjNodes(Node* n) const {
  checkNode(n);
  return (*adjVec)[n->getIndex()];
}

Node* ListGraph::addNode(char* label) {
  Node* temp = new Node(num_nodes, label, this);
  nodes->push_back(temp);
  num_nodes++;
  adjVec->resize(num_nodes);
  return temp;
}

void ListGraph::addEdge(Node* n1, Node* n2, double weight) {
  (*adjVec)[n1->getIndex()].push_back(pair<Node*,double>(n2, weight));
  (*adjVec)[n2->getIndex()].push_back(pair<Node*,double>(n1, weight));
  num_edges++;
}

void ListGraph::addEdgeSafe(Node* n1, Node* n2, double weight) {
  if (!isAdj(n1, n2)) {
    (*adjVec)[n1->getIndex()].push_back(pair<Node*,double>(n2, weight));
    (*adjVec)[n2->getIndex()].push_back(pair<Node*,double>(n1, weight));
    num_edges++;
  }
  else {
    cerr << "Error: addEdge: edge already exists.";
    exit(1);
  }
}

void ListGraph::changeWeight(Node* n1, Node* n2, double weight) {
  if (isAdj(n1, n2)) {
    list<pair<Node*,double> >::iterator i = (*adjVec)[n1->getIndex()].begin();
    while (i->first != n2)
      ++i;
    i->second = weight;
    i = (*adjVec)[n2->getIndex()].begin();
    while (i->first != n1)
      ++i;
    i->second = weight;
  }
  else {
    cerr << "Error: changeWeight: non-adjacent nodes.";
    exit(1);
  }
}

void ListGraph::removeEdge(Node* n1, Node* n2) {
  if (isAdj(n1, n2)) {
    list<pair<Node*,double> >::iterator i = (*adjVec)[n1->getIndex()].begin();
    while (i->first != n2)
      ++i;
    (*adjVec)[n1->getIndex()].erase(i);
    i = (*adjVec)[n2->getIndex()].begin();
    while (i->first != n1)
      ++i;
    (*adjVec)[n2->getIndex()].erase(i); 
    num_edges--;
  }
  else {
    cerr << "Error: removeEdge: non-adjacent nodes.";
    exit(1);
  }
}

void ListGraph::clearEdges() {
  for (int i = 0; i < num_nodes; ++i) {
    (*adjVec)[i].clear();
  }
}

void ListGraph::clear() {
  this->clearEdges();
  adjVec->clear();
  nodes->clear();
  num_nodes = 0;
  num_edges = 0;
}
