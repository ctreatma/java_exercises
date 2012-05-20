/* Charles Treatman     #5      squish.c  */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <signal.h>
#include <fcntl.h>

#define MY_MAX 1024
#define MAX_PIPES 128
#define U_PROMPT "squish% "
#define DELIM " "

char **parser(char *, int *);
void redirect(char **, int);
void pipeme(char **, int);

int main(int argc, char *argv[]) {
  char **args;
  char opts[MY_MAX];
  int i, j, bool, tokens;
  static int background;
  while(1) {

    signal(SIGCHLD, SIG_IGN);
    signal(SIGINT, SIG_IGN);
    bool = tokens = background = 0;

    printf("%s", U_PROMPT);
    if(fgets(opts, MY_MAX, stdin) == NULL) {
      fprintf(stdout, "logout\n");
      exit(0);
    }

    for(i = 0; i < (strlen(opts) - 1); i++) {
      if(opts[i] == 32) bool = 1;
      else {
        bool = 0;
        break;
      }
    }
    if(bool == 0) {
      if((opts[0] != 0) && (opts[0] != '\n')) {
        for(i = 0; i < strlen(opts); i++)
          if(opts[i] == '\n') opts[i] = 32;
        args = parser(opts, &tokens);
        for(i = 0; i < tokens; i++) {
          if(!strcmp(args[i], "<") || !strcmp(args[i], ">")) {
            for(j = i; j < tokens; j++) {
              if(!strcmp(args[j], "|")) {
                bool = -1;
                break;
              }
              else bool = 1;
            }
            break;
          }
          else if(!strcmp(args[i], "|")) {
            bool = 2;
            break;
          }
        }
        if(bool == -1) fprintf(stderr, "Malformed command\n");
        else if(bool == 1)
          redirect(args, i);
        else if(bool == 2)
          pipeme(args, tokens);
        else {
          if(!strcmp(args[0], "cd")) {
            if(args[1] == NULL)
              args[1] = getenv("HOME");
	    if(chdir(args[1]) < 0)
	      fprintf(stderr, "cd: %s\n", strerror(errno));
          }
          else if(!strcmp(args[0], "logout"))
            exit(0);
          else {
            if(!strcmp(args[tokens - 1], "&")) {
              background = 1;
              args[tokens - 1] = NULL;
            }
            else background = 0;
            if(fork() == 0) {
              if(background == 0) signal(SIGINT, SIG_DFL);
              else signal(SIGINT, SIG_IGN);
              if(execvp(args[0], args) < 0) {
                fprintf(stderr, "%s: %s\n", args[0], strerror(errno));
                exit(1);
              }
            }
            else {
              if(background == 0) {
                signal(SIGCHLD, SIG_DFL);
                if(wait(NULL) < 0)
                  fprintf(stderr, "%s: wait() in parent\n", strerror(errno));
              }
            }
          }
        }
      }
    }
  }
}


char **parser(char *opts, int *numoftokens) {
  static char *args[MY_MAX];
  int i = 1;
  args[0] = strtok(opts, DELIM);
  while(1) {
    args[i] = strtok(NULL, DELIM);
    *numoftokens += 1;
    if(args[i] == NULL) break;
    i++;
  }
  return args;
}

void pipeme(char **args, int numoftokens) {
  int p[2];
  int fd;
  int numofpipes = 0;
  int i, j, process_num = 0;
  int out_bool, redirect_pos;
  int pipepos[MAX_PIPES];
  out_bool = 0;
  for(i = 0, j = 0; i < numoftokens; i++) {
    if(strcmp(args[i], "|") == 0) {
      pipepos[j] = i;
      j++;
      numofpipes++;
    }
    else if(strcmp(args[i], ">") == 0) {
      out_bool = 1;
      redirect_pos = i;
      if(args[redirect_pos + 1] == NULL) {
        fprintf(stderr, "Malformed redirect\n");
        exit(1);
      }
      else
        args[redirect_pos] = NULL;
    }
  }
  if(fork() == 0) {
    pipe(p);
    dup2(p[0], 0);
    close(p[0]);
    for(i = 1; i < numofpipes + 1; i++) {
      if(fork() == 0) {
        dup2(p[0], 0);
        close(p[0]);
        close(p[1]);
        pipe(p);
        process_num = i;
      }
      else {
        dup2(p[1], 1);
        close(p[1]);
        close(p[0]);
        break;
      }
    }
    if(process_num == numofpipes) {
      for(j = pipepos[numofpipes - 1]; j < numoftokens; j++) {
        if(strcmp(args[j], "|") == 0) {
          args[j] = NULL;
          if(out_bool == 1) {
            if((fd = open(args[redirect_pos + 1], O_RDWR |
                          O_CREAT | O_TRUNC, 0666)) < 0)
              fprintf(stderr, "%s: open() in child\n", strerror(errno));
            if((fd = dup2(fd, 1)) < 0)
              fprintf(stderr, "%s: dup2() in child\n", strerror(errno));
          }
          if(execvp(args[pipepos[numofpipes - 1] + 1], args +
                    pipepos[numofpipes - 1] + 1) < 0) {
            fprintf(stderr, "%s: %s\n", args[pipepos[numofpipes - 1] + 1],
                    strerror(errno));
            exit(1);
          }
        }
      }
    }
    else if(process_num > 0) {
      for(j = pipepos[process_num]; j < numoftokens; j++) {
        if(strcmp(args[j], "|") == 0) {
          args[j] = NULL;
          if(execvp(args[pipepos[process_num - 1] + 1],
                    args + pipepos[process_num - 1] + 1) < 0) {
            fprintf(stderr, "%s: %s\n", args[pipepos[process_num - 1] + 1],
                    strerror(errno));
            exit(1);
          }
        }
      }
    }
    else {
      dup2(p[1], 1);
      close(p[0]);
      close(p[1]);
      for(j = 0; j < numoftokens; j++) {
        if(strcmp(args[j], "|") == 0) {
          args[j] = NULL;
          if(execvp(args[0], args) < 0) {
            fprintf(stderr, "%s: %s\n", args[0], strerror(errno));
            exit(1);
          }
        }
      }
    }
  }
  else
    for(i = 0; i < numofpipes + 1; i++)
      wait(NULL);
}

void redirect(char **args, int i) {
  int fd;
  if(strcmp(args[i], "<") == 0) {
    if(args[i + 1] == NULL)
      fprintf(stderr, "%s: Bad form\n", args[0]);
    else {
      args[i] = NULL;
      if(fork() == 0) {
        if((fd = open(args[i + 1], O_RDONLY)) < 0)
          fprintf(stderr, "%s: open() in child\n", strerror(errno));
        if((fd = dup2(fd, 0)) < 0)
          fprintf(stderr, "%s: dup2() in child\n", strerror(errno));
        if(execvp(args[0], args) < 0)
          fprintf(stderr, "%s: %s\n", args[0], strerror(errno));
      }
      else {
        if(wait(NULL) < 0)
	  fprintf(stderr, "%s: wait() in parent\n", strerror(errno));
      }
    }
  }
  else if(strcmp(args[i], ">") == 0) {
    if(args[i + 1] == NULL)
      fprintf(stderr, "%s: Bad form\n", args[0]);
    else {
      args[i] = NULL;
      if(fork() == 0) {
        if((fd = open(args[i + 1], O_RDWR | O_CREAT | O_TRUNC, 0666)) < 0)
          fprintf(stderr, "%s: open() in child\n", strerror(errno));
        if((fd = dup2(fd, 1)) < 0)
          fprintf(stderr, "%s: dup2() in child\n", strerror(errno));
        if(execvp(args[0], args) < 0)
          fprintf(stderr, "%s: %s\n", args[0], strerror(errno));
      }
      else {
        if(wait(NULL) < 0)
          fprintf(stderr, "%s: wait() in parent\n", strerror(errno));
      }
    }
  }
}

