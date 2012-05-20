//Charles Treatman ctreatma
//sort.cc

#include "sort.h"
#include <vector>
#include <stack>
#include <math.h>
#include <utility>
#include<iostream>

using namespace std;

void my_sort(vector<int>::iterator first, vector<int>::iterator last) {
  int size = last - first;
  int end = size;
  int start = 0;
  int m;
  stack<pair<int, int> > s;
  stack<pair<int, int> > r;
                                            
  pair<int, int> current;
  s.push(pair<int, int>(start, end));
  while(!s.empty()) {
    current = s.top();
    s.pop();
    r.push(current);
    if(current.second - current.first >2) {
      m = (int)floor((double) (current.first + current.second)/2);
      s.push(pair<int, int>(current.first, m));
      s.push(pair<int, int>(m, current.second));
    }
  }

  while(!r.empty()) {
    current = r.top();
    r.pop();

    vector<int>::iterator b_point = first + current.first;
    vector<int>::iterator m_point = first + (int)floor((double) (current.first + current.second)/2);
    vector<int>::iterator e_point = first + current.second;

    merge(b_point, m_point, e_point);
  }
}

void merge(vector<int>::iterator b_point, vector<int>::iterator m_point, vector<int>::iterator e_point) {
  if(e_point - b_point < 2)
    return;

  vector<int> v;
  vector<int>::iterator mid = m_point;
  vector<int>::iterator first = b_point;

  while(b_point < mid && m_point < e_point) {
    if(*b_point < *m_point) {
      v.push_back(*b_point);
      b_point++;
    }
    else {
      v.push_back(*m_point);
      m_point++;
    }
  }

  while(b_point < mid){
    v.push_back(*b_point);
    ++b_point;
  }
  while(m_point < e_point){
    v.push_back(*m_point);
    ++m_point;
  }

  int bound = v.size();
  for (int k = 0; k < bound; ++k){
    *first = v[k];
    ++first;
  }  
}
