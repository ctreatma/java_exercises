//Charles Treatman ctreatma
//sort_check.cc

#include <sort.h>
#include <vector>
#include <iostream>

using namespace std;

int main(int argc, char** argv) {
  vector<int> vec;

  for (int i = 0; i < 10; ++i) {
    vec.push_back(10 - i);
  }

  my_sort(vec.begin(), vec.end());

  for (int i = 0; i < 10; ++i) {
    cout << vec[i] << " ";
  }
  cout << endl;

  return 0;
}
