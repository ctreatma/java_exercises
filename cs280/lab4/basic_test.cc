#include "graph_factory.h"
#include "graph.h"

using namespace std;

int main(int argc, char** argv) 
{
  GraphFactory* GF = new GraphFactory(atoi(argv[1]));
  Graph* G = GF->createGraph(argv[2]);

  cout << G;
  G->write(argv[3]);
  
  delete GF;
  delete G;
  return 0;
}
