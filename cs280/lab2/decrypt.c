// encrypt.c

#include "nt_fun.h"
#include <stdio.h>
#include <stdlib.h>

#define BLOCK_SIZE (sizeof(unsigned long))

char* reverse_transformation(long long l, int size);

int main(int argc, char** argv) {
  FILE* kfp;
  FILE* ifp;
  FILE* ofp;

  char* arr;	

  mpz_t key, n, R, L;
  mpz_init(key);
  mpz_init(n);
  mpz_init(R);
  mpz_init(L);

  if (argc != 4) {
    printf("Bad command line arguments.\n");
    exit(1);
  }

  kfp = fopen(argv[1], "r");
  mpz_inp_str(key, kfp, 10);
  mpz_inp_str(n, kfp, 10);
  fclose(kfp);

  ifp = fopen(argv[2], "r");
  ofp = fopen(argv[3], "w");

  while (mpz_inp_str(R, ifp, 10) > 0) {
    mpz_powm(L, R, key, n);
    arr = reverse_transformation((long long) mpz_get_ui(L), BLOCK_SIZE);
    fprintf(ofp, "%s", arr);
    free((void*)arr);
  }
    
  fclose(ifp);
  fclose(ofp);
  return 0;
}

char* reverse_transformation(long long l, int size) {
  int i;
  char* arr = (char*)malloc((size+1)*sizeof(char));
  arr[size] = '\0';

  for (i=size-1; i >=0; i--) {
    arr[i] = (char) l;
    l = l >> 8*sizeof(char);
  }

  return arr;
}
