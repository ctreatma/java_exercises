//Charles Treatman ctreatma
//graph_algs.cc

#include "graph_algs.h"
#include <algorithm>

using namespace std;

Graph* Kruskal(Graph* G, GraphFactory* GF) {
  //Variables
  vector<Node*> nodes = G->nodeVector();
  unsigned numNodes = G->numNodes();
  vector<unsigned> flags(numNodes);
  vector< pair <double, pair<Node*, Node*> > > edgeVector;
  Graph* MST = GF->createGraph();
  
  //Add nodes and add tree merge flags
  for(int i=0; i<(int)numNodes; ++i) {
    flags[i] = i;
    MST->addNode(nodes[i]->getLabel());
  }
  vector<Node*> treenodes = MST->nodeVector();

  //Build a vector of pairs representing edges: first is a double representing
  //edge weight, second is a pair containing the nodes in the edge
  for(int i=0; i<(int)numNodes;++i) {
    for(int j = i + 1; j < (int)numNodes;++j) {
      if (G->isAdj(nodes[i], nodes[j])) {
	edgeVector.push_back(pair<double, pair<Node*, Node*> >(G->getWeight(nodes[j], nodes[i]),pair<Node*,Node*>(treenodes[i],treenodes[j])));
      }
    }
  }
  //Sort the vector of edges
  sort(edgeVector.begin(), edgeVector.end());  
  
  vector<pair<double, pair<Node*, Node*> > >::iterator i = edgeVector.begin();
  
  //Add n - 1 edges to the MST...once we've gone through this loop n-1 times
  //we must be done, so return the MST
  for(unsigned n = 0; n < numNodes - 1; n++) {
    while(flags[i->second.first->getIndex()] == flags[i->second.second->getIndex()])
      ++i;
    unsigned p = i->second.first->getIndex();
    unsigned q = i->second.second->getIndex();
    //update the flags to show that trees have been merged
    updateFlags(flags[p], flags[q], flags);
    MST->addEdge(i->second.first, i->second.second, i->first);
    if(i == edgeVector.end()) {
      //If there are no more edges to add, then we can't build an MST
      cerr << "Error: Kruskal: Disconnected input graph. No MST found!\n";
      exit(1);
    }
    ++i; 
  } 
  return MST; 
} 

void updateFlags(unsigned p, unsigned q, vector<unsigned>& flags) {
  for(unsigned i = 0; i < flags.size(); ++i) {
    if(flags[i] == q)
      flags[i] = p;
  }
}

Graph* Prim(Graph* G, GraphFactory* GF) {
  unsigned n = G->numNodes();
  Graph* MST = GF->createGraph();
  vector<double> distance(n);
  vector<long> parent(n);
  vector<Node*> tree_nodes;
  vector<Node*> nodes = G->nodeVector();

  Node* r = nodes[0];
  for(unsigned i = 1; i < n; ++i) {
    if(G->isAdj(r, nodes[i])) {
      distance[i] = G->getWeight(r, nodes[i]);
      parent[i] = 0;
    }
    else {
      distance[i] = -1;  //these values act as infinity, since we'll never
      parent[i] = -2;   //get a negative node index or edge weight
    }
  }
  parent[0] = -1;
 
  //Add the first node to the MST
  tree_nodes.push_back(MST->addNode(r->getLabel()));
  //Now we add n - 1 more nodes.  When we finish this loop we have an MST.
  for(unsigned i = 1; i < n; ++i) {
    int index = minDistance(distance, parent);
    if(index == -1){
      cerr << "Error: Prim: Input graph disconnected. No MST found!\n";
      exit(1);
    }
    
    Node* k = nodes[index];    
    Node* temp = MST->addNode(k->getLabel());
    tree_nodes.push_back(temp);
    MST->addEdge(temp, tree_nodes[parent[index]], distance[index]);
    list<pair<Node*,double> > adj = G->adjNodes(k);
    
    for(unsigned m = 1; m < n; ++m) {
      if(G->isAdj(k, nodes[m])) {
	double wt = G->getWeight(k, nodes[m]);
	if(parent[m] != -1) {
	  if(wt < distance[m] || distance[m] == -1) {
	    //Update parent and distance flags to account for changes to MST
	    parent[m] = i; 
	    distance[m] = wt;
	  } 
	}
      }   
    }
    
    parent[index] = -1;
  }
  return MST;
}

//finds the index of the minimum element in the distance vector,
//or returns -1 if disconnected.
int minDistance(vector<double>& distance, vector<long>& parent) {
  unsigned j;
  for(j = 0; j < parent.size() && parent[j] < 0 ; ++j);
  long x = j; //find the first connected edge.
  if(distance[x] == -1)
    return -1;

  for(unsigned i = j; i < distance.size(); ++i) {
    for(;i < parent.size() - 1 && parent[i] < 0;++i);
    if(parent[i] < 0) {
      ++i;
      continue;
    }
    x = (distance[x] >= distance[i]) ? i : x;
  }
  
  return x;
}

