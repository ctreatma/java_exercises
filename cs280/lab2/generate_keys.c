// generate_keys.c

#include "nt_fun.h"
#include <stdio.h>
#include <stdlib.h>

#define PRIME_SIZE 200
#define MAX_PUBLIC_KEY_SIZE 10

int main(int argc, char** argv) {
  FILE* public_file;
  FILE* private_file;
 
  mpz_t p, q, p2, q2, n, phi, e, d, one, g;
  mpz_init(p);
  mpz_init(q);
  mpz_init(p2);
  mpz_init(q2);
  mpz_init(n);
  mpz_init(phi);
  mpz_init(e);
  mpz_init(d);
  mpz_init(g);
  mpz_init_set_si(one, 1);

  if (argc != 3) {
    printf("Bad command line parameters.");
    exit(1);
  }

  public_file = fopen(argv[1], "w");
  private_file = fopen(argv[2], "w");

  do {
    findLargePrime(p, PRIME_SIZE);
    findLargePrime(q, PRIME_SIZE);
  } while (mpz_cmp(p,q)==0);

  mpz_mul(n, p, q);
  mpz_sub(p2, p, one);
  mpz_sub(q2, q, one);
  mpz_mul(phi, p2, q2);

  while (true) {
    findLargeNumber(e, MAX_PUBLIC_KEY_SIZE);
    mpz_gcd(g, e, phi);
    if (mpz_cmp(g, one)==0)
      break;
  }

  mpz_invert(d, e, phi);
  if (mpz_cmp(d, e) < 0) {
    mpz_set(g, d);
    mpz_set(d, e);
    mpz_set(e, g);
  }

  mpz_out_str(public_file, 10, e);
  fprintf(public_file, "\n\n");
  mpz_out_str(public_file, 10, n);
  mpz_out_str(private_file, 10, d);
  fprintf(private_file, "\n\n");
  mpz_out_str(private_file, 10, n);
  return 0;
}


