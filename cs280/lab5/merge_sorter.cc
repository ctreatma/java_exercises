// merge_sorter.h

#include "merge_sorter.h"

void MergeSorter::sort(vector<int>& vec) {
  reset();
  
  m_sort(vec, 0, vec.size());
}


void MergeSorter::m_sort(vector<int>& vec, int left, int right) {
  if(lessThan((right - left), 2))
    return;

  int mid = (left + right) / 2;

  m_sort(vec, left, mid);
  m_sort(vec, mid, right);
  merge(vec, left, mid, right);
}

void MergeSorter::merge(vector<int>& vec, int left, int mid, int right) {
  vector<int> temp;
  int i = left;
  int j = mid;
  
  while(i < mid && j < right) {
    if(lessThan(vec[i], vec[j])) {
      temp.push_back(vec[i]);
      i++;
    }
    else {
      temp.push_back(vec[j]);
      ++j;
    }
  }
  
  while(j < right) {
    temp.push_back(vec[j]);
    ++j;
  }
  
  while(i < mid) {
    temp.push_back(vec[i]);
    ++i;
  }
  
  for(int i = left; i < right; ++i){
    vec[i] = temp[i - left];
  }
}

