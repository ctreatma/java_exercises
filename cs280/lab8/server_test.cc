#include "io.h"
#include "server.h"

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

    MancalaServer server;

    //Player1TextIO textIO1("Pitiya");
    //Player2TextIO textIO2("Ben");

    ServerNetIO serverNetIO(9999);

    //server.addIO(&textIO1);
    //server.addIO(&textIO2);

    server.addIO(&serverNetIO);

    serverNetIO.start();

    //Player12TextIO textIO("John", "Ben");
    //Player2TextIO textIO2("Ben");
    //server.addIO(&textIO);
    //server.addIO(&textIO2);
    server.run();

}
