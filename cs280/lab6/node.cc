#include "node.h"
#include <string>

Node::Node(int index, char* label, const Graph* G) : graph(G)
{
  this->index = index;

  this->label = new char[strlen(label)+1];
  strcpy(this->label, label);
}

Node::~Node()
{
  delete [] label;
}

int Node::getIndex() const
{
  return index;
}

char* Node::getLabel() const
{
  return label;
}

bool Node::isGraph(const Graph* g) const 
{
  return graph == g;
}








