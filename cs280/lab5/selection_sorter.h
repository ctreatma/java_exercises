// bubble_sorter.h

#include "sorter.h"
#include <vector>

#ifndef CS280_SELECTION_SORTER_H
#define CS280_SELECTION_SORTER_H

using std::vector;

class SelectionSorter : public Sorter {
public:
  virtual void sort(vector<int>& vec);
};

#endif
