// analyze.cc

#include "sorter_factory.h"
#include "random_fun.h"
#include <vector>
#include <iostream>
#include <fstream>

using namespace std;

int singleRun(int type, int size);
pair<double,int> multiRun(int numTrials, int type, int size);

int main(int argc, char** argv) {
  if (argc==8)
    seedGenerator(atoi(argv[7]));

  int start_size = atoi(argv[1]);
  int final_size = atoi(argv[2]);
  int step_size = atoi(argv[3]);
  int num_trials = atoi(argv[4]);
  int type = atoi(argv[5]);
  char* file = argv[6];

  ofstream fout(file);
  for (int i=start_size; i <= final_size; i+=step_size) {
    pair<double,int> p = multiRun(num_trials, type, i);
    fout << i << " " << p.first << " " << p.second << endl;
  }

  return 0;
}


int singleRun(int type, int size) {
  Sorter* S = sorter_factory(type);
  vector<int> vec(size);
  if (uniform(0,1) < ((double)(size-1))/size) {
    for (int i=0; i < size; i++)
      vec[i] = equilikely(0,vec.size()-1);
  }
  else {
    for (int i=0; i < size; i++)
      vec[i] = i;
  }

  int n = S->numComparisons();
  S->sort(vec);
  n = S->numComparisons();
  delete S;

  return n;
}

pair<double,int> multiRun(int numTrials, int type, int size) {
  int max_value = 0;
  int total = 0;

  for (int i=0; i < numTrials; i++) {
    int n = singleRun(type, size);
    total += n;
    max_value = (max_value < n) ? n : max_value;
  }

  return pair<double, int>(((double)total)/numTrials, max_value);
}
