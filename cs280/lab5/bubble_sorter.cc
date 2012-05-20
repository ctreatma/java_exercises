// bubble_sorter.cc

#include "bubble_sorter.h"

using namespace std;

void BubbleSorter::sort(vector<int>& vec) {
  reset();

  int n = vec.size();

  for (int i = 0; i < n; ++i) {
    for (int j = i; j < n; ++j) {
      if (Sorter::greaterThan(vec[i], vec[j])) {
	swap(vec, i, j);
      }
    }
  }
}
