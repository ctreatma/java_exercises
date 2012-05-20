//Charles Treatman ctreatma
//sec_net.cc

#include "graph_factory.h"
#include "graph.h"
#include "graph_algs.h"

using namespace std;

int main(int argc, char** argv) {
  if (argc != 4) {
    cerr << "Error: main(): wrong number of command line arguments.\n";
    exit(1);
  }
  GraphFactory* GF = new GraphFactory(atoi(argv[1]));
  Graph* G = GF->createGraph(argv[2]);
  Graph* MST;
  if (atoi(argv[3]) == 0)
    MST = Kruskal(G, GF);
  else if (atoi(argv[3]) == 1)
    MST = Prim(G, GF);

  cout << "Total wire distance for all connections: ";
  cout << G->totalWeight() << " km\n";
  cout << "Total wire distance for optimal solution: ";
  cout << MST->totalWeight() << " km\n";

  delete G;
  delete MST;
  delete GF;
  return 0;
}
