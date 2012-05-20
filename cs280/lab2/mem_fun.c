//Charles Treatman ctreatma
//mem_fun.c

#include "mem_fun.h"

void resize(long long** array, int oldsize, int newsize) {
  int i;
  long long* newArray = (long long*) malloc(newsize * sizeof(long long));

  for (i = 0; i < newsize; ++i) {
    if (i >= oldsize)
      newArray[i] = 0;
    else
      newArray[i] = (*array)[i];
  }

  free(*array);
  *array = newArray;
}
