/*  Charles Treatman        Final Project        hash.cpp */
#include "hash.h"

int Hashtable::hash(const string& key, int i) {
  int h = 0;
  for (int j = 0; j < key.length(); j++) {
    h = key[j] + (key[j] + 26) * i;
  }
  int j;
  j = h % 500;
  if (j < 0) j *= -1;
  return j;
}

content* Hashtable::get(const string key) {
  string hashstr = normalize(key);
  int i = 0;
  while (i++ != 500) {
    int j = hash(hashstr, i);
    if ( hashkey[j] == hashstr ) return &hashdata[j];
    if ( hashkey[j] == "" || i == 500 ) return NULL;
  }
}

char Hashtable::remove(string key) {
  int i = 0;
  string hashstr = normalize(key);
  while (i++ != 500) {
    int j = hash(hashstr, i);
    if ( hashkey[j] == hashstr ) { hashkey[j] = ""; return 1; }
    if ( hashkey[j] == "" || i == 500 ) return 0;
  }
}

void Hashtable::put(const string& key, const content& data) {
  int i = 0;
  string hashstr = normalize(key);
  while ( i++ != 500 ) {
    int j = hash(hashstr, i);
    if (hashkey[j] == "") {
      hashkey[j] = hashstr;
      hashdata[j] = data;
      return;
    }
  }
  cerr << "Error:  in Hashtable::put(key, data):  table overflow." << endl;
}

string Hashtable::normalize(string key) {
  string hashstr;
  for (int i = 0; i < key.length(); ++i) {
    if (key[i] >= 'A' && key[i] <= 'Z') hashstr += key[i] - 'A' + 'a';
    else hashstr += key[i];
  }
  return hashstr;
}
