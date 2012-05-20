/* Charles Treatman     Final Project     treeinfo.cpp */

#include "hash.h"
#include <fstream>

void loop();
int intrees();
void addrec();
void addrec(string name);
void printdesc(int offset);
int deleterec(string name);

Hashtable *trees = new Hashtable();
int numTrees;

int main() {
  cout << "Reading data file: names.txt" << endl;
  numTrees = intrees();
  cout << "Read and stored " << numTrees << " records." << endl << endl;
  cout << "Welcome to the Oberlin Real World Tree Information System" << endl << endl;
  cout << "Please enter the common name for the tree that you seek." << endl;
  cout << "Type \"add\" to add a new record or \"quit\" to exit the database.";
  cout << endl << endl;
  cout << "Common name: ";
  loop();
}

void loop() {
  string common;
  content *data;
  string choice;
  
  while (true) {
    fflush(stdin);
    cin >> common;
    if (common == "quit" || common == "Quit"
	|| common == "QUIT") return;
    data = trees->get(common);
    if (data != NULL) {
      cout << "Latin name: " << data->name << endl;
      cout << "Description: " << endl;
      choice = "";
      if ( data->loc != 0 ) printdesc(data->loc);
      else cout << "No description is available." << endl;
      cout << "Delete this record? (Y/N) ";
      cin >> choice;
      if ( choice == "Y" || choice == "y" ) {
	if (deleterec(common) == 1) { 
	  numTrees--;
	  cout << "Record deleted, " << numTrees << " records total." << endl; 
	}
	else cout << "Could not delete record, " << numTrees << " records total." << endl;
      }
    }
    else {
      if (common == "add" || common == "Add"
	   || common == "ADD") addrec();
      else {
	cout << "No information on \"" << common << "\" was found." << endl;
	cout << "Would you like to add this record? (Y/N) ";
	cin >> choice;
	if ( choice == "Y" || choice == "y" ) addrec(common);
      }
    }
    cout << endl;
    cout << "Common name: ";
  }
}

void addrec() {
  fflush(stdin);
  string common;
  string latin;
  cout << "Add tree:" << endl;
  cout << "  Enter Common name: ";
  cin >> common;
  cout << "  Enter Latin name: ";
  fflush(stdin);
  cin >> latin;
  content temp (latin, 0);
  trees->put(common, temp);
  numTrees++;
  cout << "New tree \"" << common << "\" has been added. ";
  cout << numTrees << " records total." << endl;
}

void addrec(string name) {
  fflush(stdin);
  string latin;
  cout << "Add tree:" << endl;
  cout << "  Enter Latin name: ";
  cin >> latin;
  content temp (latin, 0);
  trees->put(name, temp);
  numTrees++;
  cout << "\"" << name << "\" has been added. " << numTrees << " records total." << endl;
}

int deleterec(string name) {
  return trees->remove(name);
}

int intrees() {
  string line;
  ifstream fin ("names.txt");
  int i = 0;
  int j = 0;
  string data[3];
  while (getline(fin,line,'\n') ) {
    data[i++] = line;
    if ( i == 3 ) {
      content temp (data[1], atoi(line.c_str()));
      trees->put(data[0], temp);
      i = 0;
      j++;
    }
  }
  fin.close();
  return j;
}

void printdesc(int offset) {
  string line;
  ifstream fin ("desc.txt");
  char c;
  for (int i = 0; i < offset; i++) fin.get();
  int n = 0;
  string choice;
  bool done = false;
  while (((c = fin.get()) != '@') && !done) {
    cout << c;
    if (c == '\n') n++;
    if (n == 20) {
      cout << "Continue displaying description? (Y/N) ";
      cin >> choice;
      if ( choice == "N" || choice == "n" )
	done = true;
      n = 0;
      choice = "";
      fflush(stdin);
    }
  }
  fin.close();
}
