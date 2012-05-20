// quick_sorter.cc

#include "quick_sorter.h"

using namespace std;

void QuickSorter::sort(vector<int> & vec) {
  reset();

  q_sort(vec, 0, vec.size() - 1);
}

void QuickSorter::q_sort(vector<int>& vec, int p, int r) {
  if(lessThan(p, r)) {
    int q = partition(vec, p, r);
    q_sort(vec, p, q - 1);
    q_sort(vec, q + 1, r);
  }
}

int QuickSorter::partition(vector<int>& vec, int p, int r) {
  int i = p - 1;
  int x = vec[r];

  for(int j = p; j < r; ++j) {
    if(lessThan(vec[j], x + 1)) {
      i++;
      swap(vec, i, j);
    }
  }

  if(i != r)
    swap(vec, i + 1, r);

  return i + 1;
}
