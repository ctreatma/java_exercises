/*  Charles Treatman   #1.3   reverse.c   */

#include <stdio.h>

#define MAXLINE 1000

int getline(char line[], int maxline);
void reverse(char s[], char rev[], int len);

main()
{
  int len;
  char s[MAXLINE];
  char rev[MAXLINE];

  while ((len = getline(s, MAXLINE)) > 0) {
    reverse(s, rev, len);
    printf("%s", rev);
  }
  return 0;
}



int getline(char s[], int lim)
{
  int c, i;

  for (i=0; i<lim-1 && (c=getchar())!=EOF &&  c != '\n'; ++i)
    s[i] = c;
  if (c == '\n') {
    s[i] = c;
    ++i;
  }
  s[i] = '\0';
  return i;
}

void reverse(char s[], char rev[], int len)
{
  int i;

  for(i=0; i < len - 1; ++i)
    rev[len-i-2] = s[i];
  rev[i] = '\n';
  rev[i+1] = '\0';
}
  
