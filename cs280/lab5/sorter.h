// sorter.h

#include <vector>
#include <algorithm>

#ifndef CS280_SORTER_H
#define CS280_SORTER_H

using std::vector;

class Sorter {
public:
  Sorter();

  virtual void sort(vector<int>& vec) = 0;
  int numComparisons();
  void swap(vector<int>& vec, int x, int y);
  void reset();

protected:
  bool lessThan(int x, int y);
  bool greaterThan(int x, int y);

private:
  int num_comparisons;
};

#endif
