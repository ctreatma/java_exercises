/* Charles Treatman     #2.1     htoi.c   */


#include <stdio.h>
#define MAXLINE 1000

int getline(char s[], int lim);
int htoi(char line[], int len);


main()
{
  char line[MAXLINE];
  int len = 0;

  while ((len = getline(line, MAXLINE)) > 0) {
    printf("%d\n", htoi(line, len));
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


int htoi(char line[], int len) {
  int decimal = 0;
  int power = 1;
  int c, i;

  int low = 0;
  if (line[0] == '0' && (line[1] == 'x' || line[1] == 'X'))
    low = 2;

  for (i = len - 2; i >= low; i-- ) {
    c = line[i];
    if (c >= '0' && c <= '9')
      decimal += power * (c - '0');
    else {
      switch (c) {
      case 'a': case 'A':
	decimal += power * 10;
	break;
      case 'b': case 'B':
	decimal += power * 11;
	break;
      case 'c': case 'C':
	decimal += power * 12;
	break;
      case 'd': case 'D':
	decimal += power * 13;
	break;
      case 'e': case 'E':
	decimal += power * 14;
	break;
      case 'f': case 'F':
	decimal += power * 15;
	break;
      default:
	return 0;
      }
    }
    power *= 16;
  }
  
  return decimal;
}
