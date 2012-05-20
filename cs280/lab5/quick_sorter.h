// quick_sorter.h

#include "sorter.h"
#include <vector>

#ifndef CS280_QUICK_SORTER_H
#define CS280_QUICK_SORTER_H

using std::vector;

class QuickSorter : public Sorter {
public:
  virtual void sort(vector<int>& vec);

 private:
  void q_sort(vector<int> &  vec, int p, int r);
  int QuickSorter::partition(vector<int>& vec, int p, int r);
};

#endif
