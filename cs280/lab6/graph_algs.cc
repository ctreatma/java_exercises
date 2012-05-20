//Charles Treatman ctreatma
//graph_algs.cc

#include "graph_algs.h"
#include "union_find.h"
#include "fib_heap.h"
#include <algorithm>

using namespace std;

//My code was starting to look nasty, so I'm setting this up to make it
//look a bit cleaner (in my opinion).  Hoping it works...
//It would probably be nicer to just make this an actual class in its own
//.cc and .h files, but as long as it works I'm happy, and everyone else
//should be happy too.
struct Edge {
  Node* u;
  Node* v;
  double weight;

  Edge() {
  }

  Edge(Node* node, Node* parent, double w) {
    weight = w;
    u = node;
    v = parent;
  }

  bool operator <(const Edge& comp) const {
    return weight < comp.weight;
  }
  
  bool operator >(const Edge& comp) const {
    return weight > comp.weight;
  }
};

Graph* Kruskal(Graph* G, GraphFactory* GF){
  unsigned n = G->numNodes();
  Graph* MST = GF->createGraph(n);
  UnionFind tracker(n);
  vector<Node*> nodes = G->nodeVector();
  vector<Edge> edges;

  for(unsigned i = 0; i < n; ++i) {
    list<pair<Node*, double> > adj = G->adjNodes(nodes[i]);
    int size = adj.size();
    unsigned index = nodes[i]->getIndex();
    list<pair<Node*, double> >::iterator it=adj.begin();
    for(int j = 0; j < size; ++j) {
      if(it->first->getIndex() > (int) index)
	edges.push_back(Edge(nodes[i], it->first, it->second));
      it++;
    } 
  }

  for(unsigned i = 0; i < n; ++i){
    Node* temp = MST->addNode(nodes[i]->getLabel());
    nodes[i] = temp;
  }

  sort(edges.begin(), edges.end());
  unsigned e = 0;
  unsigned max = edges.size();

  for(unsigned i = 0; i < n - 1; ++i){
    while(e < max && tracker.sameSet(edges[e].u->getIndex(), edges[e].v->getIndex()))
      ++e;

    if(e >= max){
      cerr << "Error: Kruskal: Input graph disconnected. No MST found!\n";
      exit(1);
    }

    int x = edges[e].u->getIndex();
    int y = edges[e].v->getIndex();
    MST->addEdge(nodes[x], nodes[y], edges[e].weight);
    tracker.unionSets(x,y);

    ++e;
  }
  return MST;
}

Graph* KruskalSlow(Graph* G, GraphFactory* GF) {
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
      cerr << "Error: KruskalSlow: Disconnected input graph. No MST found!\n";
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
  FibHeap<Edge> heap;
  unsigned n = G->numNodes();
  Graph* MST = GF->createGraph(n);
 
  vector<long> parents(n, -2);
  vector<Node*> tree_nodes;
  vector<Node*> nodes = G->nodeVector();
  vector<HeapNode<Edge>*> h_nodes(n, 0);

  Node* r = nodes[0];
  list<pair<Node*, double> > adj = G->adjNodes(r);
  int size = adj.size();
  list<pair<Node*, double> >::iterator it = adj.begin();
  for(int i = 0; i < size; ++i){
    int sub = it->first->getIndex();
    if(h_nodes[sub] == 0){
      h_nodes[sub] = heap.push(Edge(r, it->first, it->second));
      parents[sub] = 0;
    }
    else if(heap.getValue(h_nodes[sub]).weight < it->second) {
      heap.decreaseKey(h_nodes[sub], Edge(r, it->first, it->second));
      parents[sub] = 0;
    }
    it++;
  }

  tree_nodes.push_back(MST->addNode(r->getLabel()));
  parents[0] = -1;

  for(unsigned i = 1; i < n; ++i){
    Edge current = heap.top();
    heap.pop();
    int index = current.v->getIndex();

    if(parents[index] < 0){
      index = current.u->getIndex();
      if(parents[index] < 0){
	cerr << "Error: Prim: Input graph disconnected. No MST found!\n";
	exit(1);        
      }
    }

    Node* k = nodes[index];
    Node* temp = MST->addNode(k->getLabel());
    tree_nodes.push_back(temp);
    MST->addEdge(temp, tree_nodes[parents[index]], current.weight);
    list<pair<Node*,double> > adj = G->adjNodes(k);
    parents[index] = -1;
    size = adj.size();

    list<pair<Node*,double> >::iterator it = adj.begin();
    for(int j = 0; j < size; ++j){
      double weight = it->second;
      int ind = it->first->getIndex();
      if(parents[ind] != -1){
        if(h_nodes[ind] == 0){
          h_nodes[ind] = heap.push(Edge(k, it->first, weight));
          parents[ind] = i;
        }
        else{
          Edge hold = heap.getValue(h_nodes[ind]);
          if(weight < hold.weight){
            parents[ind] = i;
            heap.decreaseKey(h_nodes[ind], Edge(hold.u, hold.v, weight));
          }
        }
      }
      it++;
    }
    parents[index] = -1;
    delete h_nodes[index];
  }
  return MST;
}

Graph* PrimSlow(Graph* G, GraphFactory* GF) {
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
      cerr << "Error: PrimSlow: Input graph disconnected. No MST found!\n";
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

unsigned numComponents(Graph* G) {
  vector<Node*> nodes = G->nodeVector();
  int k = G->numNodes();
  UnionFind ponents(k);

  //Go through the nodes...for each node, see which nodes are adjacent to it
  //and if the corresponding sets in the unionfind structure are not the same,
  //then union the two sets.
  for(int i = 0; i < k; ++i) {
    list<pair<Node*, double> > adj = G->adjNodes(nodes[i]);
    int s = adj.size();
    list<pair<Node*, double> >::iterator it = adj.begin();
    for(int j = 0; j < s; ++j){
      if(it->first->getIndex() > i) {
	if(!ponents.sameSet(it->first->getIndex(), nodes[i]->getIndex()))
	  ponents.unionSets(it->first->getIndex(), nodes[i]->getIndex());
      }
    it++;
    }
  }

  return ponents.numSets();
}

