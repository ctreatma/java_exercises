//Charles Treatman ctreatma
//char_fun.c

#include "char_fun.h"

bool isLowerCase(char c) {
  if (c < 'a' || c > 'z')
    return false;
  else
    return true;
}

bool isUpperCase(char c) {
  if (c < 'A' || c > 'Z')
    return false;
  else
    return true;
}

bool isLetter(char c) {
  if (isLowerCase(c) == true || isUpperCase(c) == true)
    return true;
  else
    return false;
}
