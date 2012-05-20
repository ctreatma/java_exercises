/* Charles Treatman    #3.2     uniq.c   */

#include <stdio.h>

#define MAXLINE 1000

int getline(char s[], int lim);
int streq(char *s, char *t);
int strcopy(char *s, char *t);

int main () {
  char line1[MAXLINE];
  char line2[MAXLINE];
  int len;
  
  while ((len = getline(line1, MAXLINE)) > 0) {
    if (streq(line1, line2) != 0) {
      printf(line1);
      strcopy(line2, line1);
    }
  }

  return 0;
}

int getline( char s[], int lim ) {
  int c, i;
  
  for ( i = 0; i < lim - 1 && ( c = getchar() ) != EOF && c != '\n'; ++i)
    s[i] = c;
  if ( c == '\n' ) {
    s[i] = c;
    ++i;
  }
  s[i] = '\0';
  return i;
}

int streq(char *s, char *t) {
  for (; *s == *t; ++s, ++t)
    if (*s == '\0') return 0;
  return *s - *t;
}

int strcopy(char *s, char *t) {
  while (*s++ = *t++);
}
