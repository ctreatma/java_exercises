/* Charles Treatman       #3.3     itob.c   */

#include <stdio.h>

#define MAXLINE 1000

void itob(int n, char *s, int b);
void reverse(char s[], char rev[]);

int main() {
  int n, b;
  char s[MAXLINE];
  char num[MAXLINE];
  
  printf("Input a base, then enter, then a number, then enter, or ctl-C to exit.\n");
  while(scanf("%d\n%d", &b, &n))
  {
      itob(n, s, b);
      reverse(s, num);
      printf("The base %d representation of %d is %s.\n", b, n, num);
  }

  return 0;
}

void reverse(char s[], char rev[])
{
  int i, len;
  for(i=0; *(s+i) != '\0'; i++);
  len = i-1;
  for(i=0; i < len; ++i)
    rev[len-i-1] = s[i];
  rev[i] = '\0';
}


void itob(int n, char *s, int b) {
  int i;
  int j = (n <=b);
  char digits[]  = "0123456789abcdefghijklmnopqrstuvwxyz";
  
  for(i = n/b + j; i >= 0; n/=b, s++, i--)
    *s = digits[n%b];
  *s = '\0';
}
