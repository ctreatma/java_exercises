//Charles Treatman ctreatma
//cypher.c

#include <stdio.h>
#include <stdlib.h>
#include "char_fun.h"

void arrayEncrypt(int key);
int getSize(FILE* input);

int main() {
  int key;

  printf("%s\n", "Please input a positive integer encryption key:");
  
  if (scanf("%d", &key) >= 0)
    arrayEncrypt(key);
  else {
    printf("%s\n", "Illegal negative key.");
    exit(1);
  }

  return 0;
}

void arrayEncrypt(int key) {
  FILE* ifp;
  FILE* ofp;
  int i;
  int size;
  char* input;

  ifp = fopen("input.txt", "r");
  ofp = fopen("output.txt", "w");
  size = getSize(ifp);
  fclose(ifp);
  ifp = fopen("input.txt", "r");
  input = (char*) malloc(size * sizeof(char));
  
  for (i = 0; i < size; ++i) {
      fscanf(ifp, "%c", &input[i]);
  }

  for (i = 0; i < size; ++i) {
    if (isLowerCase(input[i]))
      input[i] = ((input[i] - 'a' + key) % 26) + 'a';
    else if (isUpperCase(input[i]))
      input[i] = ((input[i] - 'A' + key) % 26) + 'A';
  }

  for (i = 0; i < size; i++) {
      fprintf(ofp, "%c", input[i]);
  }
 
  fclose(ifp);
  fclose(ofp);
  free(input);
}

int getSize(FILE* input) {
  int i = 0;
  char c;

  while (fscanf(input, "%c", &c) != EOF) {
    ++i;
  }

  return i;
}
