// sorter.h

#include "sorter.h"
#include <vector>

#ifndef CS280_MERGE_SORTER_H
#define CS280_MERGE_SORTER_H

using std::vector;

class MergeSorter : public Sorter {
 public:
  virtual void sort(vector<int>& vec);
 
 private:
  void m_sort(vector<int> & vec, int left, int right);
  void merge(vector<int> & vec, int left, int mid, int right);
};

#endif
