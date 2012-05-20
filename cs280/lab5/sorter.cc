// sorter.h

#include <iostream>
#include "sorter.h"

using std::cerr;
using std::endl;

Sorter::Sorter() {
  num_comparisons = 0;
}

int Sorter::numComparisons() {
  return num_comparisons;
}

bool Sorter::lessThan(int x, int y) {
  num_comparisons++;
  return (x < y);
}

bool Sorter::greaterThan(int x, int y) {
  num_comparisons++;
  return (x > y);
}

void Sorter::reset() {
  num_comparisons = 0;
}

void Sorter::swap(vector<int>& vec, int x, int y){
  if (x >= vec.size() || y >= vec.size()) {
    cerr << "Called sorter:swap with bad array bounds." << endl;
    exit(1);
  }

  int temp = vec[x];
  vec[x] = vec[y];
  vec[y] = temp;
}
