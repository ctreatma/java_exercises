/*  Charles Treatman  #1.1   celtofahr.c  */

#include <stdio.h>

#define LOWER -20
#define UPPER 160
#define STEP 10

main ()
{
  int celcius;

  printf("Celcius to Fahrenheit Conversions\n");
  
  for (celcius = LOWER; celcius <= UPPER; celcius = celcius + STEP)
    printf("%3d Celcius is equivalent to %3.1f Fahrenheit\n", celcius, ((9.0/5.0) * celcius) + 32);
}
