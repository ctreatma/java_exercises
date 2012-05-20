// cracker.c

#include <string.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include "nt_fun.h"

int main(int argc, char** argv) {
  FILE* ifp;
  FILE* ofp;

  char* nstr;

  mpz_t e, d, n;
  mpz_t p, q, t, phi;
  mpz_t one, two;
  mpz_t p2, q2;

  int l1, l2;

  l1 = 1;
  clock();

  mpz_init(e);
  mpz_init(d);
  mpz_init(n);
  mpz_init(p);
  mpz_init(q);
  mpz_init(phi);
  mpz_init(t);
  mpz_init_set_si(one, 1);
  mpz_init_set_si(two, 2);
  mpz_init(p2);
  mpz_init(q2);

  if (argc != 3) {
    printf("Bad command line.\n");
    exit(1);
  }

  ifp = fopen(argv[1], "r");
  ofp = fopen(argv[2], "w");

  mpz_inp_str(e, ifp, 10);
  mpz_inp_str(n, ifp, 10);

  mpz_set(p, two);
  while (true) {
    nstr = mpz_get_str(NULL, 10, p);
    l2 = strlen(nstr);
    if (l2 > l1) {
      printf("%d digits: %f seconds.\n", l1, clock() / ((double)CLOCKS_PER_SEC));
      l1 = l2;
    }
    free((void*)nstr);

    mpz_div(q, n, p);
    mpz_mul(t, q, p);
    if (mpz_cmp(t, n)==0)
      break;
    mpz_add(p, p, one);
  }

  mpz_sub(p2, p, one);
  mpz_sub(q2, q, one);
  mpz_mul(phi, p2, q2);
  mpz_invert(d, e, phi);

  mpz_out_str(ofp, 10, d);
  fprintf(ofp, "\n\n");
  mpz_out_str(ofp, 10, n);

  return 0;
}
