#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>
#include <ctype.h>

int numtoken, numspech, numpipe;
char command[81];
char *tokens[80];
int comsegs[12];
int pids[12];
int pipevec[10][2];

void Prompt(int Num);
void setFrom(int from);
void setTo(int to);
void Pclose();
void execute();
int tokenize(char*s);
int segmentize(char**s);
int bg();

/*Read a line, return NULL on EOF*/
#define GetLine( S ) ( fgets( S, 81, stdin) ) 
 
/* Print a Prompt*/
void Prompt (int Num)
{
  printf( "a5sh[%d]", Num);
  fflush(stdout);
}

main(void)
{
  int i;
  int CommandNum=1;

  signal(SIGINT, SIG_IGN);      /*ignore signals*/
  signal(SIGQUIT, SIG_IGN);

  for (Prompt(CommandNum); GetLine(command);)
    {
      if (command[0] != '\n')
	{
	  numtoken = tokenize(command);
	  numspech = segmentize(tokens) - 1;
	  execute();
	}
      Prompt( CommandNum );
    }

  return 0;

}

/*set input redirection*/
void setFrom(int from)
{
  int fdf;

  if(from)
    {
      if((fdf = open(tokens[comsegs[1] + 1], O_RDONLY, 0)) == -1)
	{
	  perror(tokens[comsegs[1] + 1]);
	  exit(127);
	}
      dup2(fdf, 0);
      close(fdf);
    }
}

/*set output redirection*/
void setTo(int to)
{
  int fdt;  
  if(to)
    {
      fdt = open(tokens[comsegs[numspech] + 1], O_CREAT|O_WRONLY, 0644);
      dup2(fdt, 1);
      close(fdt);
    }
}

void execute()
{
  int pid, status;

  /*
   * Get a child process.
   */
  if ((pid = fork()) < 0) {
    perror("fork");
    exit(1);
  }

  /*
   * The child executes the code inside the if.
   */
  if (pid == 0) {
    execvp(tokens[0], tokens);
    perror(command);
    exit(1);
  }

  /*
   * The parent executes the wait.
   */
  if (command[comsegs[numspech + 1]] != '&') 
    while (wait(&status) != pid)
      /* empty */ ;
}

/*close all pipes*/
void Pclose()
{
  int i;
  for (i = 0; i < numpipe; i++)
    {
      close(pipevec[i][0]);
      close(pipevec[i][1]);
    }
}


tokenize(char* s)
{
  char *p, *q;
  int i;
    
  p = s;
  i = 0;
  while (*p != '\0') {
    while (*p == ' ' || *p == '\t') p++;
    if (*p == '\0') break;
    for (q = p; *q != ' ' && *q != '\t' && *q != '\0'; q++);
    tokens[i++] = p;
    if (*q == '\0')
      p = q;
    else p = q + 1;
    *q = '\0';
  }
  tokens[i] = NULL;
  return(i);
}

segmentize(char *s[])
{
  int i, j;
    
  comsegs[0] = -1;
  j = 1;
  for (i = 0; s[i] != 0; i++)
    if (!strcmp(s[i], "|"))comsegs[j++] = i;
  if (!strcmp(s[i-1], "&"))
    comsegs[j] = i-1;
  else comsegs[j] = i;
  return(j);
}

/*check if background processing needed*/
int bg()
{
  return !strcmp(&command[comsegs[numspech]], "&");
}


