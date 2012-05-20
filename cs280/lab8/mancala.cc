#include "io.h"
#include "server.h"
#include <sstream>

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

    // set a new default exception handler for
    // uncaught exceptions
    set_terminate(mancala_default_exception_handler);


    // parse the command line
    ostringstream help_oss;
    help_oss 
	<< "usage: " << argv[0] << " [option1 option2 ...]"<<endl<<endl
	<< "options: "<<endl
	<< "-p1 name      connect as player1" <<endl
	<< "-p2 name      connect as player2" << endl
	<< "              note: you can use both -p1 and -p2 "
	<< "for a local game"<<endl
	<< "-spec name    connect as a spectator" << endl
	<< "-ai1 name     have player1 be your AI. overrides -p1 option" << endl
	<< "-ai2 name     have player2 be your AI. overrides -p2 option" << endl
	<< "-ai1t type    type of AI player 1: minimax, greedy, greedy_r, random"<<endl
	<< "              default: minimax" << endl
	<< "-ai2t type    type of AI player 2: minimax, greedy, greedy_r, random"<<endl
	<< "              default: minimax" << endl
	<< "-s server     set the server to connect to " << endl
	<< "              (overrides default setting of being a server)"<<endl
	<< "-S name       start a dedicated multiserver where everyone who "
	<< "connects will"<<endl
	<< "              play against player <name>. use with -St option"<<endl
	<< "-St type      set the player type for a dedicated multiserver"<<endl
	<< "              valid types: greedy, greedy_r, random."<<endl
	<< "              default: greedy"<<endl
	<< "-p port       set the port of the server, "<<endl
	<< "              or the port to connect to if using -s"<<endl
	<< "              default: 5200"<<endl
	<< "options only valid without -s option:"<<endl
	<< "-bps bins     bins per side (including kalahas)"<<endl
	<< "              default: 7"<<endl
	<< "-ppb pieces   initial pieces per bin"<<endl
	<< "              default: 4" << endl
	<< "-t1 ms        player 1's timelimit (in milliseconds)"<<endl
	<< "              default: 120000" <<endl
	<< "-t2 ms        player 2's timelimit (in milliseconds)"<<endl
	<< "              default: 120000" << endl
	<< "-md depth     maximum depth of AI search" << endl
	<< "              default: 4" << endl
	<< "-ms people    maximum number of spectators allowed" << endl
	<< "              default: 15" << endl
	<< "-gt gametype  either E for Egyptian or W for Awari"<<endl
	<< "              default: E"<<endl;


    bool isServer, isPlayer1, isPlayer2, isSpec, isAI1, isAI2, isMulti;
    isPlayer1 = isPlayer2 = isAI1 = isAI2 = isSpec = isMulti = false;
    isServer = true;
    string p1N, p2N, specN, ai1N, ai2N, SN;
    string St = "greedy";
    string server = "localhost";
    string ai1t = "minimax";
    string ai2t = "minimax";
    int port = 5200;
    int t1 = 120000;
    int t2 = 120000;
    int bps = 7;
    int ppb = 4;
    int md = 4;
    int ms = 15;
    string gt = "E";

    int i = 1;
    while (i < argc)
    {
	if (i+1 < argc)
	{
	    if (string(argv[i]) == "-p1")
	    {
		p1N = string(argv[i+1]);
		isPlayer1 = true;
	    }
	    else if (string(argv[i]) == "-p2")
	    {
		p2N = string(argv[i+1]);
		isPlayer2 = true;
	    }
	    else if (string(argv[i]) == "-spec")
	    {
		specN = string(argv[i+1]);
		isSpec = true;
	    }
	    else if (string(argv[i]) == "-ai1")
	    {
		ai1N = string(argv[i+1]);
		isAI1 = true;
		isPlayer1 = false;
	    }
	    else if (string(argv[i]) == "-ai2")
	    {
		ai2N = string(argv[i+1]);
		isAI2 = true;
		isPlayer2 = false;
	    }
	    else if (string(argv[i]) == "-ai1t")
	    {
		ai1t = string(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-ai2t")
	    {
		ai2t = string(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-s")
	    {
		server = string(argv[i+1]);
		isServer = false;
	    }
	    else if (string(argv[i]) == "-S")
	    {
		SN = string(argv[i+1]);
		isServer = false;
		isMulti = true;
	    }
	    else if (string(argv[i]) == "-St")
	    {
		St = string(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-p")
	    {
		port = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-bps")
	    {
		bps = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-ppb")
	    {
		ppb = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-t1")
	    {
		t1 = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-t2")
	    {
		t2 = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-md")
	    {
		md = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-ms")
	    {
		ms = atoi(argv[i+1]);
	    }
	    else if (string(argv[i]) == "-gt")
	    {
		gt = string(argv[i+1]);
		if (gt != "E" && gt != "W")
		{
		    cerr << "game type must be either E or W"<<endl;
		    exit(1);
		}
	    }
	    else
	    {
		cerr << help_oss.str();
		exit(1);
	    }
	    i += 2;
	}
	else
	{
	    cerr << help_oss.str();
	    exit(1);
	}
    }


    APlayer* aiPlayer1 = NULL;
    APlayer* aiPlayer2 = NULL;

    /*
     * Here's where you can add more possible AI players
     */
    if (ai1t == "minimax")
    {
	aiPlayer1 = new MinimaxPlayer1();
    }
    else if (ai1t == "greedy")
    {
	aiPlayer1 = new GreedyPlayer();
    }
    else if (ai1t == "greedy_r")
    {
	aiPlayer1 = new GreedyRPlayer();
    }
    else if (ai1t == "random")
    {
	aiPlayer1 = new RandomPlayer();
    }
    else
    {
	cerr << "invalid value for ai1t: "<<ai1t<<endl;
	exit(1);
    }

    if (ai2t == "minimax")
    {
	aiPlayer2 = new MinimaxPlayer2();
    }
    else if (ai2t == "greedy")
    {
	aiPlayer2 = new GreedyPlayer();
    }
    else if (ai2t == "greedy_r")
    {
	aiPlayer2 = new GreedyRPlayer();
    }
    else if (ai2t == "random")
    {
	aiPlayer2 = new RandomPlayer();
    }
    else
    {
	cerr << "invalid value for ai2t: "<<ai2t<<endl;
	exit(1);
    }
    

    if (isPlayer1 && isPlayer2) // local game
    {
	MancalaServer server(bps, ppb, t1, t2, md, ms, gt);
	Player12TextIO io(p1N, p2N);
	server.addIO(&io);
	server.run();
    }

    else if (isPlayer1 && isAI2) // local game
    {
	MancalaServer server(bps, ppb, t1, t2, md, ms, gt);
	Player12TextIO io(p1N, ai2N);
	if (io.getPlayer2())
	    delete io.getPlayer2();
	io.setPlayer2(aiPlayer2);
	server.addIO(&io);
	server.run();
    }

    else if (isAI1 && isPlayer2) // local game
    {
	MancalaServer server(bps, ppb, t1, t2, md, ms, gt);
	Player12TextIO io(ai1N, p2N);
	if (io.getPlayer1())
	    delete io.getPlayer1();
	io.setPlayer1(aiPlayer1);
	server.addIO(&io);
	server.run();
    }
    
    else if (isAI1 && isAI2) // local game
    {
	MancalaServer server(bps, ppb, t1, t2, md, ms, gt);
	Player12TextIO io(ai1N, ai2N);
	if (io.getPlayer1())
	    delete io.getPlayer1();
	if (io.getPlayer2())
	    delete io.getPlayer2();
	io.setPlayer1(aiPlayer1);
	io.setPlayer2(aiPlayer2);
	server.addIO(&io);
	server.run();
    }
    
    else if (isServer)
    {
	cout << "starting server at "<<server<<":"<<port<<endl;
	MancalaServer server(bps, ppb, t1, t2, md, ms, gt);
	AIO *io1, *io2, *io3, *io4, *io5;
	io1 = io2 = io3 = io4 = io5 = NULL;
	if (isPlayer1)
	{
	    io1 = new Player1TextIO(p1N);
	}
	if (isPlayer2)
	{
	    io2 = new Player2TextIO(p2N);
	}
	if (isSpec)
	{
	    io3 = new SpectatorTextIO(specN);
	}
	if (isAI1)
	{
	    io4 = new Player1TextIO(ai1N);
	    if (io4->getPlayer())
		delete io4->getPlayer();
	    io4->setPlayer(aiPlayer1);
	}
	if (isAI2)
	{
	    io5 = new Player2TextIO(ai2N);
	    if (io4->getPlayer())
		delete io4->getPlayer();
	    io5->setPlayer(aiPlayer2);
	}
	ServerNetIO ioN(port);
	server.addIO(&ioN);
	if (io1)
	    server.addIO(io1);
	if (io2)
	    server.addIO(io2);
	if (io3)
	    server.addIO(io3);
	if (io4)
	    server.addIO(io4);
	if (io5)
	    server.addIO(io5);
	
	ioN.start();
	server.run();
	
	if (io1)
	    delete io1;
	if (io2)
	    delete io2;
	if (io3)
	    delete io3;
	if (io4)
	    delete io4;
	if (io5)
	    delete io5;
    }

    else if (isMulti)
    {
	cout << "starting multi-server at port: "<<port<<endl;
	APlayer* player;

	if (St == "random" || St == "Random" || St == "RANDOM")
	{
	    player = new RandomPlayer();
	}
	else if (St == "greedy" || St == "Greedy" || St == "GREEDY")
	{
	    player = new GreedyPlayer();
	}
	else if (St == "greedy_r" || St == "greedyr" 
		 || St == "GreedyR" || St == "GREEDY_R"
		 || St == "Greedy_R")
	{
	    player = new GreedyRPlayer();
	}
	else
	{
	    cerr << "invalid value for St: "<<St<<endl;
	    exit(1);
	}

	ostringstream oss;
	oss << gt << " " << bps << " " << ppb << " "
	    << md << " " << t1 << " " << t2;
	RuleSet* ruleSet = RuleSet::createRuleSet(oss.str());

	SinglePlayerMultiServerNetIO 
	    server(ruleSet, player, SN, port, ms);
	server.run();
	delete player;
    }

    else // must be a client
    {
	ClientNetIO client(server, port);

	AIO *io1, *io2, *io3, *io4, *io5;
	io1 = io2 = io3 = io4 = io5 = NULL;
	if (isPlayer1)
	{
	    io1 = new Player1TextIO(p1N);
	}
	if (isPlayer2)
	{
	    io2 = new Player2TextIO(p2N);
	}
	if (isSpec)
	{
	    io3 = new SpectatorTextIO(specN);
	}
	if (isAI1)
	{
	    io4 = new Player1TextIO(ai1N);
	    if (io4->getPlayer())
		delete io4->getPlayer();
	    io4->setPlayer(aiPlayer1);
	}
	if (isAI2)
	{
	    io5 = new Player2TextIO(ai2N);
	    if (io5->getPlayer())
		delete io5->getPlayer();
	    io5->setPlayer(aiPlayer2);
	}

	if (io1)
	    client.addIO(io1);
	if (io2)
	    client.addIO(io2);
	if (io3)
	    client.addIO(io3);
	if (io4)
	    client.addIO(io4);
	if (io5)
	    client.addIO(io5);
	
	client.run();
	
	if (io1)
	    delete io1;
	if (io2)
	    delete io2;
	if (io3)
	    delete io3;
	if (io4)
	    delete io4;
	if (io5)
	    delete io5;
    }
	
    if (aiPlayer1)
	delete aiPlayer1;
    if (aiPlayer2)
	delete aiPlayer2;

    return 0;
}
