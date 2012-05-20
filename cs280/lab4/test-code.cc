#include "graph_factory.h"
#include <stdlib.h>
#include <vector>
#include <list>
#include <iostream>

using namespace std;

void check(bool v, char* c) {
  if (!v) 
    cout << c << endl;
}

int main(int argc, char** argv) {
  for (int type = 1; type >= 0; type--) {
    cout << "Type: " << type << endl;
    GraphFactory GF(type);
    Graph* G = GF.createGraph();

    vector<Node*> vecArray(10);
    for (int i=0; i < 10; i++)
      vecArray[i] = G->addNode("");
    
    check(G->numNodes() == 10, "Node Number not working (1)");
    check(G->numEdges() == 0, "Edge number not working (2)");
    
    for (int i=0; i < 10; i+=2)
      G->addEdge(vecArray[i],vecArray[(i+2)%10], 1);
    for (int i=1; i < 9; i+=2)
    G->addEdge(vecArray[i],vecArray[(i+2)%10], 2);
    
    check(G->numEdges() == 9, "Edge number not working (3)");
    check(G->totalWeight() == 13, "Total weight not working (4)");
    
    check(G->degree(vecArray[4])==2, "degree not working (5)");
    check(G->degree(vecArray[1])==1, "degree not working (6)");
    
    
    check(G->isAdj(vecArray[4],vecArray[6]), "isAdj not working (7)");
    check(G->isAdj(vecArray[1],vecArray[3]), "isAdj not working (8)");
    check(!G->isAdj(vecArray[1],vecArray[2]), "isAdj not working (9)");
    
    G->addEdge(vecArray[1],vecArray[2],2);
    check(G->isAdj(vecArray[1],vecArray[2]), "isAdj not working (10)");
    
    check(G->getWeight(vecArray[2],vecArray[4])==1, "getWeight not working (11)");

    list<pair<Node*,double> > adjArr = G->adjNodes(vecArray[1]);

    check(adjArr.size()==2, "adjArr wrong size (12)");
    if (adjArr.size()==2) {
      list<pair<Node*,double> >::iterator i1 = adjArr.begin();
      list<pair<Node*,double> >::iterator i2 = adjArr.begin();
      i2++;

      check(i1->first==vecArray[2] || i2->first==vecArray[2], "adjArr wrong (13)");
      check(i1->first==vecArray[3] || i2->first==vecArray[3], "adjArr wrong (14)");
    }
    
    vector<Node*> nodeVec = G->nodeVector();
    for (int i=0; i < 10; i++)
      check(nodeVec[i] == vecArray[i], "nodeVector wrong (15)");
    G->changeWeight(vecArray[1],vecArray[2],10);
    check(G->getWeight(vecArray[1],vecArray[2])==10, "changeWeight didn't take (16)");
    check(G->totalWeight() == 23, "Total weight not working (17)");
    
    G->removeEdge(vecArray[1],vecArray[2]);
    check(G->numEdges()==9, "remove edge problem -- numEdges (18)");
    check(G->totalWeight() == 13, "remove edge problem -- remove edge (19)");

    G->clearEdges();
    check(G->numEdges()==0, "clear edge problem -- numEdges (20)");
    check(G->totalWeight() == 0, "clear edge problem -- totalWeight (21)");
    
    G->addEdge(vecArray[0], vecArray[1], 1);
    G->clear();
    check(G->numEdges()==0, "clear problem -- numEdges (22)");
    check(G->totalWeight() == 0, "clear problem -- totalWedight (23)");
    check(G->numNodes() == 0, "clear problem -- numNodes (23)");

    delete G;

    cout << "Done." << endl;
  }
}
