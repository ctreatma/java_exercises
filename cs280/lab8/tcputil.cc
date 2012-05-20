#include "tcputil.h"
#include <iostream>

// receive a buffer from a TCP socket with error checking
// this function handles the memory, so it can't use any [] arrays
// returns 0 on any errors, or a valid char* on success
char *getNetMsg(TCPsocket sock, char **buf)
{
	Uint32 len,result;
	static char *_buf;

	// allow for a NULL buf, use a static internal one...
	if(!buf)
	    buf=&_buf;
	
	// free the old buffer
	if(*buf)
	    free(*buf);
	*buf=NULL;

	// receive the length of the string message
	result=SDLNet_TCP_Recv(sock,&len,sizeof(len));
	if(result<sizeof(len))
	{
#ifdef MANCALA_NET_DEBUG	   
            // sometimes blank!
	    if(SDLNet_GetError() && strlen(SDLNet_GetError())) 
		std::cout<<"Net Debug: SDLNet_TCP_Recv: "
			 << SDLNet_GetError()<<std::endl;
#endif
	    return(NULL);
	}
	
	// swap byte order to our local order
	len=SDL_SwapBE32(len);
	
	// check if anything is strange, like a zero length buffer
	if(!len)
	    return(NULL);

	// allocate the buffer memory
	*buf=(char*)malloc(len);
	if(!(*buf))
	    return(NULL);

	// get the string buffer over the socket
	result=SDLNet_TCP_Recv(sock,*buf,len);
	if(result<len)
	{
#ifdef MANCALA_NET_DEBUG
	    // sometimes blank!
	    if(SDLNet_GetError() && strlen(SDLNet_GetError())) 
		std::cerr<<"Net Debug: SDLNet_TCP_Recv: "
			 <<SDLNet_GetError()<<std::endl;
#endif
	    free(*buf);
	    buf=NULL;
	    return NULL;
	}

	// return the new buffer
	return(*buf);
}

// send a string buffer over a TCP socket with error checking
// returns 0 on any errors, length sent on success
int putNetMsg(TCPsocket sock, char *buf)
{
	Uint32 len,result;

	if(!buf || !strlen(buf))
		return(1);

	// determine the length of the string
	len=strlen(buf)+1; // add one for the terminating NULL
	
	// change endianness to network order
	len=SDL_SwapBE32(len);

	// send the length of the string
	result=SDLNet_TCP_Send(sock,&len,sizeof(len));
	if(result<sizeof(len)) {
#ifdef MANCALA_NET_DEBUG
	    // sometimes blank!
	    if(SDLNet_GetError() && strlen(SDLNet_GetError())) 
		std::cerr<<"Net Debug: SDLNet_TCP_Send: "
			 << SDLNet_GetError()<<std::endl;
#endif
	    return(0);
	}
	
	// revert to our local byte order
	len=SDL_SwapBE32(len);
	
	// send the buffer, with the NULL as well
	result=SDLNet_TCP_Send(sock,buf,len);
	if(result<len) {
#ifdef MANCALA_NET_DEBUG
	    // sometimes blank!
	    if(SDLNet_GetError() && strlen(SDLNet_GetError()))
		std::cerr<<"Net Debug: SDLNet_TCP_Send: "
			 << SDLNet_GetError()<<std::endl;
#endif
	    return(0);
	}
	
	// return the length sent
	return(result);
}

