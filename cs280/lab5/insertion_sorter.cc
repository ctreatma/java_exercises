// insertion_sort.cc

#include "insertion_sorter.h"

using namespace std;

void InsertionSorter::sort(vector<int>& vec) {
  reset();

  int n = vec.size();

  int key, index;
  int last = 0;

  for(int i = 1; i < n; ++i){
    key = vec[i];

    index = -1;
    for(int j = 0; j <= i; ++j) {
      if (lessThan(vec[j], key))
	index = j;
    }

    for(int j = i; j > (index + 1); --j)
      swap(vec, j, j - 1);

    last = index;
  }
}
