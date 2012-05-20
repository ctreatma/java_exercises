#include "io.h"

using namespace std;

int main(int argc, char* argv[])
{
    if (SDL_Init(SDL_INIT_TIMER) == -1)
    {
	cerr << "SDL_Init: " << SDL_GetError() << endl;
	exit(1);
    }
    if (SDLNet_Init() == -1)
    {
	cerr << "SDLNet_Init: " << SDLNet_GetError() << endl;
	SDL_Quit();
	exit(1);
    }
    set_terminate(mancala_default_exception_handler);

    ClientNetIO clientNetIO("132.162.215.204", 9999);
    SpectatorTextIO spectatorTextIO("Mr. Spectator");
    clientNetIO.addIO(&spectatorTextIO);

    clientNetIO.run();
}
