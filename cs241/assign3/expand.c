/*  Charles Treatman    #3.1      expand.c   */

#include <stdio.h>

#define MAXLINE 1000

void expand(char *s1, char *s2);
int getline(char *line, int lim);

main()
{
  char s1[MAXLINE];
  char s2[MAXLINE];
  int len;

  while ((len = getline(s1, MAXLINE)) > 0)
    {
      expand(s1, s2);
      printf("%s\n", s2);
    }

  return 0;
}

void expand (char *s1, char *s2)
{
  int i, k, j=0;

  for (i=0; s1[i]!='\n'; i++)
    {
      if(s1[i]!='-' || i == 0) {
        s2[j++] = s1[i];
      }

      else if((s1[i+1] > s1[i-1] + 1) &&
              ((s1[i-1]>='a' && s1[i+1]<='z') ||
               (s1[i-1]>='0' && s1[i+1]<='9') ||
               (s1[i-1]>='A' && s1[i+1]<='Z')))
        {
          for(k=1; k < s1[i+1]-s1[i-1]; k++)
            s2[j++] = s1[i-1] + k;
        }

      else {
        s2[j++]=s1[i];
      }
    }

  s2[j]='\0';
}

int getline(char *line, int lim)
{
  int c, i;

  for (i=0; i<lim-1 && (c=getchar())!=EOF && c!='\n'; i++)
    line[i] = c;

  if (c == '\n') {
    line[i] = c;
    ++i;
  }
  line[i] = '\0';
  return i;
}
