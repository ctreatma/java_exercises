//Charles Treatman ctreatma
//sort.h

#include <vector>

#ifndef CTREATMA_SORT_H
#define CTREATMA_SORT_H

using std::vector;

void my_sort(vector<int>::iterator first, vector<int>::iterator last);
void merge(vector<int>::iterator i, vector<int>::iterator j, vector<int>::iterator end);

#endif
