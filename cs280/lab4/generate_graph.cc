// generate_graph.cc

using namespace std;

#include "generate_graph.h"
#include "random_fun.h"
#include <set>

Graph* completeGraph(unsigned size, double weight, GraphFactory* GF)
{
  Graph* G = GF->createGraph();
  vector<Node*> nodes(size);
  for (unsigned i=0; i < size; i++)
    nodes[i] = G->addNode("Label");

  for (unsigned i=0; i < size-1; i++)
    for (unsigned j=i+1; j < size; j++)
      G->addEdge(nodes[i], nodes[j], weight);

  return G;
}

Graph* randomCompleteGraph(unsigned size, double minWeight, double maxWeight, GraphFactory* GF)
{
  if (minWeight <= 0 || maxWeight <= minWeight) {
    cerr << "Bad weights passed to randomCompleteGraph.\n";
    exit(1);
  }

  Graph* G = GF->createGraph();
  vector<Node*> nodes(size);
  for (unsigned i=0; i < size; i++)
    nodes[i] = G->addNode("Label");

  for (unsigned i=0; i < size-1; i++)
    for (unsigned j=i+1; j < size; j++)
      G->addEdge(nodes[i], nodes[j], uniform(minWeight,maxWeight));

  return G;
}


Graph* randomSparseGraph(unsigned size, double minWeight, double maxWeight, GraphFactory* GF)
{
  char* arr = new char[size*size];
  for (unsigned i=0; i < size*size; i++)
    arr[i] = 0;

  if (minWeight <= 0 || maxWeight <= minWeight) {
    cerr << "Bad weights passed to randomSparseGraph.\n";
    exit(1);
  }

  Graph* G = GF->createGraph();
  vector<Node*> nodes(size);
  for (unsigned i=0; i < size; i++)
    nodes[i] = G->addNode("Label");  

  vector<int> nums(size);
  for (unsigned i=0; i < size; i++)
    nums[i] = i;

  for (unsigned i=0; i < size; i++) {
    int j = equilikely(0,size-1);
    int temp = nums[i];
    nums[i] = nums[j];
    nums[j] = temp;
  }		  

  for (unsigned i=0; i < size-1; i++) {
    G->addEdge(nodes[nums[i]], nodes[nums[i+1]], uniform(minWeight, maxWeight));
    arr[nums[i] + nums[i+1]*size] = 1;
    arr[nums[i+1] + nums[i]*size] = 1;
  }
  
  while (G->numEdges() < (2*(G->numNodes()-1))) {
    int i = (int)equilikely(0,size-1);
    int j = (int)equilikely(0,size-1);
    if (i!=j && (arr[i + j*size]==0)) {
      G->addEdge(nodes[i], nodes[j], uniform(minWeight, maxWeight));
      arr[i + j*size] = 1;
      arr[j + i*size] = 1;
    }
  }

  delete [] arr;

  return G;
}

