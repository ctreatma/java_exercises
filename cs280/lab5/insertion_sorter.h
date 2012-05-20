// bubble_sorter.h

#include "sorter.h"
#include <vector>

#ifndef CS280_INSERTION_SORTER_H
#define CS280_INSERTION_SORTER_H

using std::vector;

class InsertionSorter : public Sorter {
public:
  virtual void sort(vector<int>& vec);
};

#endif
