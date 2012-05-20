//Charles Treatman ctreatma
//list_graph.h

#ifndef LIST_GRAPH_H
#define LIST_GRAPH_H

#include "graph.h"
#include <vector>
#include <list>
#include <iostream>

class ListGraph : public Graph {
  
 public:
  
  //Constructors
  ListGraph();
  virtual ~ListGraph();
  
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
  vector<list<pair<Node*,double> > >* adjVec;
};

#endif
