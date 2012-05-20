// graph.h

#include "node.h"
#include <utility>
#include <iostream>
#include <list>
#include <vector>

using std::list;
using std::vector;
using std::pair;
using std::ostream;


#ifndef CS280_GRAPH_H
#define CS280_GRAPH_H

class Graph {

public:

  //destructor
  virtual ~Graph() {}

  // inspectors
  virtual unsigned numNodes() const; 
  virtual unsigned numEdges() const; 
  virtual unsigned degree(Node* n) const = 0;

  virtual bool isAdj(Node* n1, Node* n2) const = 0;
  virtual double getWeight(Node* n1, Node* n2) const = 0;
  virtual double totalWeight() const = 0;

  virtual list<pair<Node*,double> > adjNodes(Node* n) const = 0;
  virtual vector<Node*> nodeVector() const;


  // mutators
  virtual Node* addNode(char* label) = 0;
  virtual void addEdge(Node* n1, Node* n2, double weight) = 0;
  virtual void addEdgeSafe(Node* n1, Node* n2, double weight) = 0;
  virtual void changeWeight(Node* n1, Node* n2, double weight) = 0;
  virtual void removeEdge(Node* n1, Node* n2) = 0;
  virtual void clearEdges() = 0;
  virtual void clear() = 0;

  // file interface functions 
  virtual void read(char* file);
  virtual void write(char* file);

protected:
  void checkNode(Node* n) const;
  int num_nodes;
  int num_edges;
  vector<Node*>* nodes;
};

ostream& operator<<(ostream& sout, const Graph& G);
ostream& operator<<(ostream& sout, const Graph* G);



#endif
