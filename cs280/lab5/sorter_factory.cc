// sort_factory.cc

#include "sorter_factory.h"
#include "bubble_sorter.h"
#include "merge_sorter.h"
#include "insertion_sorter.h"
#include "selection_sorter.h"
#include "quick_sorter.h"
#include <iostream>

using namespace std;

Sorter* sorter_factory(int type) {
  switch (type) {
  case 0  : return new BubbleSorter;
  case 1  : return new InsertionSorter;
  case 2  : return new SelectionSorter;
  case 3  : return new MergeSorter;
  case 4  : return new QuickSorter;
  default : cerr << "Bad input to sorter factory." << endl;
            exit(1);
  }

  return NULL;
}
