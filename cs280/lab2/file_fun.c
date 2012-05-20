// read_file.c

#include "file_fun.h"
#include <stdio.h>
#include <stdlib.h>

char* read_char_file(char* name, int* size) {
  FILE* ifp;
  char* file_arr;
  char c;
  int i;

  *size = 0;
  ifp = fopen(name, "r");
  while (fscanf(ifp, "%c", &c)==1) 
    (*size) = (*size) + 1;
  *size = *size + 1;
  while (*size % 3 != 0)
  fclose(ifp);

  file_arr = (char*)malloc((*size)*sizeof(char));
  ifp = fopen(name, "r");
  for (i=0; i < *size-1; i++)
    fscanf(ifp, "%c", &file_arr[i]);
  file_arr[*size-1] = '\0';
  fclose(ifp);
  
  return file_arr;
}

long long* read_long_long_file(char* name, int* size) {
  FILE* ifp;
  long long* arr;
  long long l, i;

  *size = 0;
  ifp = fopen(name, "r");
  while (fscanf(ifp, "%lld", &l) != -1)
    (*size) = (*size) + 1;
  
  arr = (long long*) malloc((*size)*sizeof(long long));
  ifp = fopen(name, "r");
  for (i=0; i < *size; i++)
    fscanf(ifp, "%lld", &arr[i]);
  fclose(ifp);

  return arr;
}
  


void write_file(char* name, char* arr, int size) {
  FILE* ofp = fopen(name, "w");
  fprintf(ofp, "%s", arr);
  fclose(ofp);
}

char** split_string(char* str, int size, int bsize, int* new_size) {
  char** str_array;
  int i, j, ln;

  *new_size = (size-1) / bsize;
  if ((*new_size)*bsize < size-1)
    (*new_size)++;

  str_array = (char**)malloc((*new_size)*sizeof(char*));
  for (i=0; i < *new_size; i++)
    str_array[i] = (char*)malloc((*new_size)*sizeof(bsize));

  for (i=0, j=0, ln = 0; i < size-1; i++, j=(j+1) % bsize) {
    str_array[ln][j] = str[i];
    if (j == bsize-1)
      ln++;
  }

  return str_array;
}








