// mst-timing
using namespace std;

#include "graph_factory.h"
#include "graph_algs.h"
#include "generate_graph.h"
#include <iostream>
#include <time.h>

int main(int argc, char** argv)
{
  if (argc != 5) {
    cerr << "Wrong number of command line parameters." << endl;
    exit(1);
  }

  GraphFactory* GF;
  if ((strcmp(argv[1], "list")==0) || strcmp(argv[1], "List")==0)
    GF = new GraphFactory(1);
  else if ((strcmp(argv[1], "matrix")==0) || strcmp(argv[1], "Matrix")==0)
    GF = new GraphFactory(0);
  else {
    cerr << "Bad command line parameter.  List or Matrix graph?" << endl;
    exit (1);
  }

  Graph* G;
  if (strcmp(argv[2], "complete")==0 || strcmp(argv[2], "Complete")==0)
    G = randomCompleteGraph(atoi(argv[4]), 1, 1000, GF);
  else if (strcmp(argv[2], "sparse")==0 || strcmp(argv[2], "Sparse")==0)
    G = randomSparseGraph(atoi(argv[4]), 1, 1000, GF);
  else {
    cerr << "Bad command line parameter.  Complete/sparse not specified." << endl;
    exit(1);
  }

  Graph* T;
  clock_t t = clock();
  if (strcmp(argv[3], "KruskalSlow")==0 || strcmp(argv[3], "kruskalSlow")==0) {
    T = KruskalSlow(G, GF); 
    cout << "The KruskalSlow alg. took ";
  }
  else if (strcmp(argv[3], "PrimSlow")==0 || strcmp(argv[3], "primSlow")==0) {
    T = PrimSlow(G, GF);
    cout << "The PrimSlow alg. took ";
  }
  else if (strcmp(argv[3], "Kruskal")==0 || strcmp(argv[3], "kruskal")==0) {
    T = Kruskal(G, GF);
    cout << "The Kruskal alg. took ";
  }
  else if (strcmp(argv[3], "Prim")==0 || strcmp(argv[3], "prim")==0) {
    T = Prim(G, GF);
    cout << "The Prim alg. took ";
  }
  else {
    cerr << "Bad command line parameter.  Which mst algorithm?" << endl;
    exit(1);
  }

  cout << (clock()-t)/((double)CLOCKS_PER_SEC) << " seconds for a randomly generated ";
  if (*(argv[2])=='c')
    cout << "complete";
  else
    cout << "sparse";
  if (atoi(argv[1])==0)
    cout << " matrix";
  else
    cout << " list";
  cout << " graph of " << G->numNodes() << " nodes.\n";

  delete T;
  delete GF;

  return 0;
}

