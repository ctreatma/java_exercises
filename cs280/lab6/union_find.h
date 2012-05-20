 // union_find.h

#include <deque>

#ifndef CS280_UNION_FIND_H
#define CS280_UNION_FIND_H

using std::deque;

class UnionFind {
public:
  UnionFind();
  UnionFind(unsigned size);

  // Inspectors
  unsigned numSets() const;
  unsigned numElements() const;
  int findSet(int e);
  int setSize(int e);
  bool sameSet(int e1, int e2);

  // Mutators
  void makeSet();
  void unionSets(int e1, int e2);

private:
  void checkElement(int e) const;

  deque<int> vec;
  unsigned sets;
  unsigned elements;
};


#endif

