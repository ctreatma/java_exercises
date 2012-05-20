// graph.cc

#include "graph.h"
#include <vector>
#include <fstream>

using namespace::std;

unsigned Graph::numNodes() const {
  return num_nodes;
}

unsigned Graph::numEdges() const {
  return num_edges;
}

vector<Node*> Graph::nodeVector() const {
  return *nodes;
}

void Graph::checkNode(Node* n) const
{
  if (!n->isGraph(this)) {
    cerr << "Illegal operation: node not associated with graph." << endl;
    exit(1);
  }
}

void Graph::read(char* file)
{
  clear();

  int n;

  ifstream fin(file);
  fin >> n;

  vector<Node*> vec;
  vec.resize(n);
  char* temp = new char[1000];
  for (int i=0; i < n; i++) {
    fin >> temp;
    vec[i] = addNode(temp);
  }
  delete [] temp;

  int n1, n2;
  double w;
  while (fin >> n1 >> n2 >> w) {
    if ((n1 >= n) || (n2 >= n)) {
      cerr << "Bad graph file -- illegal edge." << endl;
      exit(1);
    }
    addEdge(vec[n1], vec[n2], w);
  }

  fin.close();
}

void Graph::write(char* file)
{
  ofstream fout(file);
  fout << numNodes() << "\n\n";
  
  vector<Node*> vec = nodeVector();
  for (vector<Node*>::iterator i=vec.begin(); i!=vec.end(); i++)
    fout << (*i)->getLabel() << "\n";
  fout << "\n";

  for (vector<Node*>::iterator i=vec.begin(); i!=vec.end(); i++) {
    list<pair<Node*,double> > adjLst = adjNodes(*i);
    for (list<pair<Node*,double> >::iterator j=adjLst.begin(); j!=adjLst.end(); j++)
      if ((*i)->getIndex() < (j->first)->getIndex())
	fout << (*i)->getIndex() << " " << (j->first)->getIndex() << " " << j->second << "\n";
  }
  fout << endl;
}

ostream& operator<<(ostream& sout, const Graph& G)
{
  vector<Node*> vec = G.nodeVector();
  for (vector<Node*>::iterator i = vec.begin(); i != vec.end(); i++) {
    list<pair<Node*, double> > adjNodes = G.adjNodes(*i);
    sout << (*i)->getIndex() << " (" << (*i)->getLabel() << "): ";
    for (list<pair<Node*,double> >::iterator j=adjNodes.begin(); j!=adjNodes.end(); j++)
      sout << (j->first)->getIndex() << " (" << j->second << ")  ";
    sout << endl;
  }

  return sout;
}

ostream& operator<<(ostream& sout, const Graph* G)
{
  sout << *G;
  return sout;
}










