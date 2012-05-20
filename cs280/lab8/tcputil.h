/*  
    Thanks to the creator(s) of SDL_net_demos for this class
    Please see http://jcatki.no-ip.org/SDL_net/
*/

#ifndef tcputil_h
#define tcputil_h 1

#include <SDL/SDL.h>
#include <SDL/SDL_net.h>
#include <malloc.h>

// receive a buffer from a TCP socket with error checking
// this function handles the memory, so it can't use any [] arrays
// returns 0 on any errors, or a valid char* on success
char *getNetMsg(TCPsocket sock, char **buf);

// send a string buffer over a TCP socket with error checking
// returns 0 on any errors, length sent on success
int putNetMsg(TCPsocket sock, char *buf);

#endif
