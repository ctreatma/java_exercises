// random_fun.h

#include <vector>

#ifndef CS280_RANDOM_FUN
#define CS280_RANDOM_FUN

// Seed the generator (using a non-negative number)
void seedGenerator(long s);

// Returns a random double in the interval [u,l) with a uniform probability distribution.
double uniform(double l, double u);

// Returns a random number between l and u with a uniform probability distribution.
long long equilikely(long long l, long long u);

// Returns a 0 50% of the time, and a 1 50% of the time.
int coin_flip();

// Returns an array containing all numbers from first to second in a 
// random order.
long long* generate_permutation(long long first, long long second);

#endif
