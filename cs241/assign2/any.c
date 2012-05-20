/*    Charles Treatman    #2.2    any.c    */

#include <stdio.h>
#define MAXLINE 1000

int any(char s1[MAXLINE], char s2[MAXLINE], int len1, int len2);

main() {
  char s1[MAXLINE];
  char s2[MAXLINE];
  int len1 = 0;
  int len2 = 0;

  while ((len1 = getline(s1, MAXLINE)) > 0 &&
	 (len2 = getline(s2, MAXLINE)) > 0)
    printf("%d\n", any(s1, s2, len1, len2));
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


int any(char s[MAXLINE], char s2[MAXLINE], int len1, int len2) {
  int i, j;

  for ( i = 0; i < len1 - 1; i++ ) {
    for ( j = 0; j < len2 - 1; j++ ) {
      if ( s[i] == s2[j] ) {
        return i + 1;
      }
    }
  }

  return -1;
}
