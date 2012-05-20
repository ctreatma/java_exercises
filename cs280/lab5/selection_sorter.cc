// selection_sort.cc

#include "selection_sorter.h"

using namespace std;

void SelectionSorter::sort(vector<int>& vec) {
  reset();

  unsigned i, j;
  int min, temp;

  for (i = 0; i < vec.size() - 1; i++) {
    min = i;
    for (j = i + 1; j < vec.size(); j++) {
      if (lessThan(vec[j], vec[min]))
	min = j;
    }
    temp = vec[i];
    vec[i] = vec[min];
    vec[min] = temp;
  }
}
