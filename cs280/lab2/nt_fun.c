// nt_fun.c

#include "nt_fun.h"
#include <math.h>
#include "random_fun.h"
#include <stdlib.h>

void findLargeNumber(mpz_t n, int size) {
  int i;
  char* arr = (char*)malloc((size+1)*sizeof(char));
  for (i=0; i < size; i++)
    arr[i] = '0' + (int) uniform(0,9);

  mpz_set_str(n, arr, 10);
  free((void*)arr);
}

  
   

void findLargePrime(mpz_t p, int size) {
  int i;
  mpz_t n;
  char* arr = (char*)malloc((size+1)*sizeof(char));

  arr[0] = '0' + (int)uniform(1,9);
  for (i=0; i < size; i++)
    arr[i] = '0' + (int)uniform(0,9);
  arr[size] = '\0';

  mpz_init_set_str(n, arr, 10);
  mpz_nextprime(p, n);
  free((void*)arr);
}

