// gen_graph.cc
#include "generate_graph.h"

using namespace std;

int main(int argc, char** argv)
{
  if (argc != 5) {
    cerr << "Wrong number of command line parameters." << endl;
    exit(1);
  }

  GraphFactory* GF = new GraphFactory(atoi(argv[1]));
  
  Graph* G;
  switch (atoi(argv[2])) {
  case 0 : G = randomCompleteGraph(atoi(argv[3]), 1, 1000, GF);
           break;
  case 1 : G = randomSparseGraph(atoi(argv[3]), 1, 1000, GF);
           break;
  default: cerr << "Second command line bad." << endl;
           exit(1);
  }

  G->write(argv[4]);
}
