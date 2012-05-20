// random_fun.h

#ifndef CS280_RANDOM_FUN
#define CS280_RANDOM_FUN

#define bool int
#define true 1
#define false 0

bool coin_flip();
long long uniform(long long l, long long u);
long long* generate_permutation(long long first, long long second);

#endif
