#include "io.h"
#include "exceptions.h"

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

    ClientNetIO clientNetIO("localhost", 9999);
    Player1TextIO player1TextIO("Ben");
    Player2TextIO player2TextIO("Pit");

    
//    MinimaxPlayer2 minimaxPlayer2;
//    player2TextIO.setPlayer(&minimaxPlayer2);

    clientNetIO.addIO(&player1TextIO);
    clientNetIO.addIO(&player2TextIO);

    clientNetIO.run();
}
