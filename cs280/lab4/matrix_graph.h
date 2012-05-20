//Charles Treatman ctreatma
//matrix_graph.h

#ifndef MATRIX_GRAPH_H
#define MATRIX_GRAPH_H

#include "graph.h"
#include <utility>
#include <vector>
#include <list>

class MatrixGraph : public Graph {
  
 public:
  
  //Constructors
  MatrixGraph();
  virtual ~MatrixGraph();
  
  // inspectors
  virtual unsigned degree(Node* n) const;

  virtual bool isAdj(Node* n1, Node* n2) const;
  virtual double getWeight(Node* n1, Node* n2) const;
  virtual double totalWeight() const;

  virtual list<pair<Node*,double> > adjNodes(Node* n) const;

  // mutators
  virtual Node* addNode(char* label);
  virtual void addEdge(Node* n1, Node* n2, double weight);
  virtual void changeWeight(Node* n1, Node* n2, double weight);
  virtual void removeEdge(Node* n1, Node* n2);
  virtual void clearEdges();
  virtual void clear();

 protected:
  vector<vector<double> > adjMat;
};

#endif
