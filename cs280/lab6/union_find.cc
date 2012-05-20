// union_find.cc

#include "union_find.h"
#include <iostream>

using std::cerr;
using std::endl;

UnionFind::UnionFind()
{
  sets = 0;
  elements = 0;
}

UnionFind::UnionFind(unsigned size)
{
  vec.resize(size,-1);
  sets = size;
  elements = size;
}

unsigned UnionFind::numSets() const
{
  return sets;
}

unsigned UnionFind::numElements() const
{
  return elements;
}

int UnionFind::findSet(int e)
{
  checkElement(e);

  if(vec[e] < 0)
    return e;

  int i = e;
  while(vec[i] >= 0){
    i = vec[i];
  }

  int j;
  while(vec[e] >= 0){
    j = vec[e];
    vec[e] = i;
    e = j;
  }

  return i;
}

int UnionFind::setSize(int e)
{
  checkElement(e);

  int r = findSet(e);
  return -vec[r];
}

bool UnionFind::sameSet(int e1, int e2)
{
  checkElement(e1);
  checkElement(e2);
  
  return (findSet(e1)==findSet(e2));
}

void UnionFind::makeSet()
{
  elements++;
  sets++;
  vec.push_back(-1);
}

void UnionFind::unionSets(int e1, int e2)
{
  checkElement(e1);
  checkElement(e2);
  
  if(findSet(e1) == findSet(e2))
    return;
  
  sets--;

  if(setSize(e1) > setSize(e2)) {
    vec[findSet(e2)] += vec[findSet(e1)];
    vec[findSet(e1)] = findSet(e2);

  }
  else {
    vec[findSet(e1)] += vec[findSet(e2)];
    vec[findSet(e2)] = findSet(e1);
  }
}

void UnionFind::checkElement(int e) const
{
  if ((e < 0) || (e >= (int)elements)) {
    cerr << "Bad element number: " << e << endl;
    exit(1);
  }
}



