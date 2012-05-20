// encrypt.c

#include "nt_fun.h"
#include <stdio.h>
#include <stdlib.h>

#define BLOCK_SIZE (sizeof(unsigned long))

long transform_char(char* arr, int size);

int main(int argc, char** argv) {
  FILE* kfp;
  FILE* ifp;
  FILE* ofp;
  int j;

  char arr[3];
  char c;


  mpz_t key, n, L, R;
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
  j = 0;
  while(fscanf(ifp, "%c", &c) > -1) {
    arr[j++] = c;
    if (j==BLOCK_SIZE) {
      mpz_set_ui(L, (unsigned long) transform_char(arr, BLOCK_SIZE));
      mpz_powm(R, L, key, n);
      mpz_out_str(ofp, 10, R);
      fprintf(ofp, " ");
      j=0;
    }     
  }

  if (j > 0) {
    while (j < BLOCK_SIZE)
      arr[j++] = '\0';
    mpz_set_ui(L, (unsigned long) transform_char(arr, BLOCK_SIZE));
    mpz_powm(R, L, key, n);
    mpz_out_str(ofp, 10, R);
  }

  return 0;
}

long transform_char(char* arr, int size) {
  int i;
  long l;

  l = 0;
  for (i=0; i < size; i++)
    l = (l << 8*sizeof(char)) | ((long) arr[i]);

  return l;
}

