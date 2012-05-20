/*  Charles Treatman        Final Project        hash.h */

#include <iostream>

using namespace std;

class content {
 public:
  content(string n = "", int l = 0)
    {name = n; loc = l;}
  string name;
  int loc;
};

class Hashtable {
 private:
  string hashkey[500];
  content hashdata[500];
  int hash(const string& key, int i);
  string normalize(string key);
  
 public:
  Hashtable() {;} 
  void put(const string& key, const content& data);
  content* get(string key);
  char remove(string key);
};
