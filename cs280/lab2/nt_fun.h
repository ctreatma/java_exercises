// nt_fun.h

#include <gmp.h>

#ifndef CS280_NT_FUN_H
#define CS280_NT_FUN_H

#define bool int
#define true 1
#define false 0

void findLargeNumber(mpz_t n, int size);
void findLargePrime(mpz_t n, int size);

#endif
