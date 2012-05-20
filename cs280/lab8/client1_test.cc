#include "io.h"

using namespace std;

int main(int argc, char* argv[])
{
    if (argc < 2)
    {
	cerr << "usage: " << argv[0] <<" <username>"<<endl;
	exit(1);
    }

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
    Player1TextIO player1TextIO(argv[1]);
    clientNetIO.addIO(&player1TextIO);

    clientNetIO.run();
}
