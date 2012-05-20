/* Charles Treatman  #1.2   lowerfy.c  */

#include <stdio.h>

int lower(int c);

main()
{
  int c;


  while((c=getchar()) != EOF) {
    putchar(lower(c));
  }
}


int lower(int c) {
  if (c >= 'A' && c <= 'Z')
    return c + 32;
  else
    return c;
}
