/*  Charles Treatman     #4     crossref.c  */

#include <stdio.h>
#include <ctype.h>
#include <string.h>
#define MAXWORD 200    /*maximum number of different words*/
#define MAXLINE 150    /*maximum number of lines*/
#define LINELEN 100    /*maximum line length*/

typedef struct word
{
  char *text;                /*points to the word text*/
  int freq;                  /*word frequency*/
  int online[MAXLINE];       /*number of occurrances of this word on each line (mostly 0) */
}WORD;

WORD* wlist[MAXWORD];         
int numwords=0;
char line_str[LINELEN];       
int line=0;                       

void crossref(FILE* ifp, FILE* ofp);
int strlwr(char *s);
int getword(FILE *ifp, char *s, int *pln);  
void winsert(char *s, int lineno);          
void swap(WORD **p, WORD **q);              
void wsort();                               
void wprint(FILE *ofp);                     
void prline(FILE *ofp, int j);              


main(int argc, char *argv[])
{
  FILE *ifp, *ofp;                
  argv++; argc--;                 

  if(argc==0)     
    crossref(stdin, stdout);

  else if(argc==1)
    {
      if (**argv=='-')
	{
	  fprintf(stderr, "crossref:  Usage:\n\tcrossref\n\tcrossref <input file>");
          fprintf(stderr, "\n\tcrossref <input file> <output file>\n\tcrossref -o <output file>\n");
	}
      else
	{
	  printf("%s\n", *argv);
	  ifp  = fopen(*argv, "r");
	  if (ifp == NULL)        
	    fprintf(stderr, "Can not open %s\n", *argv);
	  else
	    crossref(ifp, stdout);
	}
    }

  else if(argc==2)
    {
      ofp = fopen(*++argv, "w");  
      if (ofp == NULL)
        fprintf(stderr, "Can not open %s\n", *argv);
      else if (*--argv=="-o")
	crossref(stdin, ofp);
      else if (**argv != '-')     
	{
	  ifp = fopen(*argv, "r");
	  if (ifp == NULL)
	    fprintf(stderr, "Can not open %s\n", *argv);
	  else
	    crossref(ifp, ofp);
	}
      else
	{  
	  fprintf(stderr, "crossref:  Usage:\n\tcrossref\n\tcrossref <input file>");
	  fprintf(stderr, "\n\tcrossref <input file> <output file>\n\tcrossref -o <output file>\n");
	}
    }
  
  else
    {
      fprintf(stderr, "crossref:  Usage:\n\tcrossref\n\tcrossref <input file>");
      fprintf(stderr, "\n\tcrossref <input file> <output file>\n\tcrossref -o <output file>\n");
    }
}

void crossref(FILE *ifp, FILE *ofp)             
{
  char wd[LINELEN];
  while(getword(ifp, wd, &line))
    {
      int i;
      char *c = (char*) malloc(LINELEN, sizeof(char));
      strcpy(c, wd);
      winsert(c, line);
    }
  wsort();
  wprint(ofp);
}

int strlwr(char *s)                               /*convert a string to lower case*/
{                        
  while(*s)
    *s++ = tolower(*s);
  return 1;
}

int getword(FILE *Ifp, char *s,  int *pln)      
{
  char *p, *pl;                                 
  if (!*line_str || !(pl=strtok(NULL, " \t\n"))) 
    {
      do                                             /*call fgets until we've gotten a "strtokable" line or reached EOF*/
        {                                            
          p = fgets(line_str, LINELEN, Ifp);
          (*pln)++;
        }while(p && strlwr(line_str) && !(pl=strtok(line_str, " \t\n")));

      if(!p)                                     
	return 0;

      else                                       
        {                                            /*done grabbing a word*/
          strcpy(s,pl);
          return 1;
        }
    }
  else
    {                                            
      strcpy(s,pl);
      return 1;
    }
}

void winsert(char *s, int lineno)                
{
  int i=0;                                              /*recur until the same word is found in the array or end of array*/
  while(i<numwords && strcmp(wlist[i]->text,s) && ++i);
  if(i<numwords)                                        /*if word is found, increment freq*/
    {
      (wlist[i]->freq)++;
      wlist[i]->online[lineno] = 1;
    }
  else                                                  /*else make a new word and insert into the array*/
    {
      WORD *foo = (WORD*) malloc(sizeof(WORD));
      int i;
      foo->text =  s;
      foo->freq = 1;
      for (i = 0; i < MAXLINE; i++) foo->online[i] = 0;
      foo->online[lineno]=1;
      wlist[numwords++]= foo;
    }
}
void swap(WORD **p, WORD **q)                    
{
  WORD *temp;
  temp = *p;
  *p = *q;
  *q = temp;
}

void wsort()                                            /*bubble sort the list*/
{
  int i, j;
  for (i=0; i<numwords; i++)
    {
      for (j=0; j<numwords-1; j++)
        {
          if((wlist[j])->freq < (wlist[j+1])->freq)
	    swap(&(wlist[j]), &(wlist[j+1]));
        }
    }
}

void wprint(FILE *ofp)                                   /*print out the list*/
{
  int i, j;
  for (i=0; i<numwords; i++)
    {
      fprintf(ofp, "%-20s %-4d: ", (wlist[i])->text, (wlist[i])->freq);
      for (j=0; j<MAXLINE; j++)
	{
	  if ((wlist[i]->online[j])==1)
	    fprintf(ofp, "%-3d", j);
	}
      fprintf(ofp, "\n");
    }
}


