#include "hash.h"

void Hash::put(const string& key, const content& data) {
  int i = 0;
  string newstr;
  for ( int j = 0; j < key.length(); j++ ) { // convert to lowercase
    if ( key[j] >= 'A' && key[j] <= 'Z' ) newstr += key[j] - 'A' + 'a';
    else newstr += key[j];
  }
  while ( i++ != 500 ) {
    int h = 0;
    for (int j = 0; j < newstr.length(); j++) {
      h = 31*h + newstr[j] + i;
    }
    int j;
    j = h % 500;
    if (j < 0) j *= -1;
    if ( hashKeys[j] == "" ) {
      hashKeys[j] = newstr;
      hashData[j] = data;
      return;
    }
  }

  cout << "Error!  Hash table overflow!" << endl;
}

content* Hash::get(const string key) {
  int i = 0;
  string newstr;
  for ( int j = 0; j < key.length(); j++ ) {
    if ( key[j] >= 'A' && key[j] <= 'Z' ) newstr += key[j] - 'A' + 'a';
    else newstr += key[j];
  }
  while ( i++ != 500 ) {
    int h = 0;
    for ( int j = 0; j < newstr.length(); j++ ) {
      h = 31*h + newstr[j] + i;
    }
    int j;
    j = h % 500;
    if ( j < 0 ) j *= -1;
    if ( hashKeys[j] == newstr ) return &hashData[j];
    if ( hashKeys[j] == "" || i == 500 ) return NULL;
  }
}

char Hash::remove(string key) {
  int i = 0;
  string newstr;
  for ( int j = 0; j < key.length(); j++ ) {
    if ( key[j] >= 'A' && key[j] <= 'Z' ) newstr += key[j] - 'A' + 'a';
    else newstr += key[j];
  }
  while ( i++ != 500 ) {
    int h = 0;
    for ( int j = 0; j < newstr.length(); j++ ) {
      h = 31*h + newstr[j] + i;
    }
    int j;
    j = h % 500;
    if ( j < 0 ) j *= -1;
    if ( hashKeys[j] == newstr ) { hashKeys[j] = ""; return 1; }
    if ( hashKeys[j] == "" || i == 500 ) return 0;
  }
}
