/*  Charles Treatman  #2.3   revword.c   */

#include <stdio.h>
#define MAXLINE 1000

int getline(char line[], int maxline);
void revword( char s[], int len);

main() {
  int len;
  char line[MAXLINE];

  while ((len = getline(line, MAXLINE)) > 0)
    revword(line, len);
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

void revword( char s[], int len ) {
  int word = len - 1;
  int i, j;

  for ( i = len - 1; i >= -1; i-- ) {
    if ( s[i] == ' ' || i == -1) {
      for ( j = i + 1; j <= word - 1; j++ )
        putchar(s[j]);
      printf(" ");
      word = i;
    }
  }

  printf("\n");
}
