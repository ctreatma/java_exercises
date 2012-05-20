// node.h

#ifndef CS280_NODE_H
#define CS280_NODE_H

class Graph;
class MatrixGraph;
class ListGraph;

class Node {
  
  friend class Graph;
  friend class MatrixGraph;
  friend class ListGraph;

public:
  int getIndex() const; 
  char* getLabel() const;
  bool isGraph(const Graph* G) const;  // Returns true iff G is the graph
                                       // with which the node is associated.
private:
  Node(int index, char* label, const Graph* G);
  ~Node();

  unsigned index;
  char* label;
  const Graph* graph;
};

#endif

