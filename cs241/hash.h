#include <iostream>
#include <string>

using namespace std;

class content {
 public:
  content(const string& n = "", const int& i = 0) {
    name = n;
    offset = i;
  }
  string name;
  int offset;
};

class Hash {
  string hashKeys[500];
  content hashData[500];
 public:
  Hash() {;}
  void put(const string&, const content&);
  content* get(string);
  char remove(string key);
};
