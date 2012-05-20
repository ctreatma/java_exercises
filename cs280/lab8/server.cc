//#include "game_board.h"
//#include "exceptions.h"
#include <iostream>
#include <sstream>
#include <ctype.h>
#include "server.h"

using namespace std;

MancalaServer::MancalaServer(int bps, int ppb, int tl1, int tl2,
			     int md, int ms, string gt)
{
    thread = NULL;
    shutdown_requested = false;
    player1Name = "(not connected)";
    player2Name = "(not connected)";
    player1Connected = false;
    player2Connected = false;
    binsPerSide = bps;
    piecesPerBin = ppb;
    maxDepth = md;
    timeLimit1 = tl1;
    timeLimit2 = tl2;
    maxSpectators = ms;
    numSpectators = 0;
    gameType = gt;
}
MancalaServer::MancalaServer(RuleSet* ruleSet, int max_spectators)
{
    std::istringstream iss (ruleSet->serialize());

    iss >> gameType;
    iss >> binsPerSide;
    iss >> piecesPerBin;
    iss >> maxDepth;
    iss >> timeLimit1;
    iss >> timeLimit2;

    thread = NULL;
    shutdown_requested = false;
    player1Name = "(not connected)";
    player2Name = "(not connected)";
    player1Connected = false;
    player2Connected = false;

    maxSpectators = max_spectators;
    numSpectators = 0;
}
MancalaServer::~MancalaServer() {}
int MancalaServer::CreateThread(void* server)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: CreateThread called: " << endl;
#endif
    if (server == NULL)
	return -1;
    else
	return static_cast<MancalaServer*>(server)
	    ->thread_func();
}
void MancalaServer::addIO(AIO* io)
{
    inputs_outputs.push_back(io);
}
void MancalaServer::removeIO(AIO* io)
{
    vector<AIO*>::iterator i = 
	find(inputs_outputs.begin(), 
	     inputs_outputs.end(), io);
    if (i != inputs_outputs.end())
	inputs_outputs.erase(i);
}

int MancalaServer::run()
{
    if (thread == NULL)
    {
	shutdown_requested = false;
	player1Connected = false;
	player2Connected = false;
	numSpectators = 0;
	thread = SDL_CreateThread(MancalaServer::CreateThread, this);
	int status;
	SDL_WaitThread(thread, &status);
	thread = NULL;
	return status;
    }
    else
	return -1;
}

void MancalaServer::stop()
{
    // should wrap this in a mutex
    shutdown_requested = true;
    // end mutex
}

void MancalaServer::kill()
{
    SDL_KillThread(thread);
    thread = NULL;
}

bool MancalaServer::isRunning()
{
    return (! (thread == NULL) );
}

int MancalaServer::thread_func()
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: thread_func() called on: " << this << endl;
#endif
    while (! shutdown_requested)
    {
	for (unsigned int i = 0; i < 
		 inputs_outputs.size(); i++)
	{
	    Message* msg = inputs_outputs[i]->getMessage();
	    while (msg != NULL)
	    {
		msg->accept(this, inputs_outputs[i]);
		delete msg;
		msg = inputs_outputs[i]->getMessage();
	    }
	}
    }

    // a shutdown was requested
    // send everyone a Disconnect Message
    DisconnectMessage msg("-1");
    sendAll(&msg);

    return 0;
}

void MancalaServer::sendAll(Message* msg)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: sendAll: " << *msg << endl;
#endif
    for (unsigned int i = 0; i < inputs_outputs.size(); i++)
    {
	Message* clone = msg->createNew(msg->getData());
	inputs_outputs[i]->putMessage(clone);
    }
}

void MancalaServer::handleMessage(Message* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: server default handler: " << *msg << endl;
#endif
    sendAll(msg);
}
void MancalaServer::handleTextMessage(TextMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: TextMessage handler: " << *msg << endl;
#endif
    sendAll(msg);
}
void MancalaServer::handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: ConnectPlayer1Message handler: " << *msg << endl;
#endif

    if (player1Connected)
    {
	InvalidConnectionMessage* msg 
	    = new InvalidConnectionMessage();
	static_cast<AIO*>(io)->putMessage(msg);
    }
    else
    {
	static_cast<AIO*>(io)->putMessage(msg->createNew(msg->getData()));
	player1Connected = true;
	player1Name = msg->getData();
	if (player2Connected)
	{
	    GameStartMessage gsm;
	    Player1MoveRequestMessage p1mrm;
	    sendAll(&gsm);
	    sendAll(&p1mrm);
	}
    }
}
void MancalaServer::handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: ConnectPlayer2Message handler: " << *msg << endl;
#endif

    if (player2Connected)
    {
	InvalidConnectionMessage* msg 
	    = new InvalidConnectionMessage();
	static_cast<AIO*>(io)->putMessage(msg);
    }
    else
    {
	static_cast<AIO*>(io)->putMessage(msg->createNew(msg->getData()));
	player2Connected = true;
	player2Name = msg->getData();
	if (player1Connected)
	{
	    GameStartMessage gsm;
	    Player1MoveRequestMessage p1mrm;
	    sendAll(&gsm);
	    sendAll(&p1mrm);
	}
    }
}
void MancalaServer::handleConnectSpectatorMessage(ConnectSpectatorMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: ConnectSpectatorMessage handler: " << *msg << endl;
#endif

    if (numSpectators == maxSpectators)
    {
	InvalidConnectionMessage* msg 
	    = new InvalidConnectionMessage();
	static_cast<AIO*>(io)->putMessage(msg);
    }
    else
    {
	static_cast<AIO*>(io)->putMessage(msg->createNew(msg->getData()));
	numSpectators++;
    }
}
void MancalaServer::handleDisconnectMessage(DisconnectMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: DisconnectMessage handler: " << *msg << endl;
#endif

    if (msg->getData() == "0")
    {
	numSpectators = max(numSpectators, 0);
    }
    else if (msg->getData() == "1")
    {
	player1Connected = false;
	sendAll(msg);
    }
    else if (msg->getData() == "2")
    {
	player2Connected = false;
	sendAll(msg);
    }
}
void MancalaServer::handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player1MoveRequestMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}
void MancalaServer::handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player2MoveRequestMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}
void MancalaServer::handlePlayer1MoveMessage(Player1MoveMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player1MoveMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}
void MancalaServer::handlePlayer2MoveMessage(Player2MoveMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player2MoveMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}
void MancalaServer::handleGameOverMessage(GameOverMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: GameOverMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
    DisconnectMessage dm("-1");
    sendAll(&dm);
    shutdown_requested = true;
}
void MancalaServer::handlePlayer1WinnerMessage(Player1WinnerMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player1WinnerMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}
void MancalaServer::handlePlayer2WinnerMessage(Player2WinnerMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player2WinnerMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}
void MancalaServer::handleTieMessage(TieMessage* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: TieMessage handler: " << *msg << endl;
#endif

    sendAll(msg);
}

void MancalaServer::handlePlayer1ConnectedQuery(Player1ConnectedQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player1ConnectedQuery handler: " << *msg << endl;
#endif

    if (player1Connected)
    {
	Player1ConnectedResponse* msg =
	    new Player1ConnectedResponse("1");
	static_cast<AIO*>(io)->putMessage(msg);
    }
    else
    {
	Player1ConnectedResponse* msg =
	    new Player1ConnectedResponse("0");
	static_cast<AIO*>(io)->putMessage(msg);
    }
}
void MancalaServer::handlePlayer2ConnectedQuery(Player2ConnectedQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player2ConnectedQuery handler: " << *msg << endl;
#endif

    if (player2Connected)
    {
	Player2ConnectedResponse* msg =
	    new Player2ConnectedResponse("1");
	static_cast<AIO*>(io)->putMessage(msg);
    }
    else
    {
	Player2ConnectedResponse* msg =
	    new Player2ConnectedResponse("0");
	static_cast<AIO*>(io)->putMessage(msg);
    }
}
void MancalaServer::handlePlayer1NameQuery(Player1NameQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player1NameQuery handler: " << *msg << endl;
#endif

    Player1NameResponse* p1nr =
	new Player1NameResponse(player1Name);
    static_cast<AIO*>(io)->putMessage(p1nr);
}
void MancalaServer::handlePlayer2NameQuery(Player2NameQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player2NameQuery handler: " << *msg << endl;
#endif

    Player2NameResponse* p2nr =
	new Player2NameResponse(player2Name);
    static_cast<AIO*>(io)->putMessage(p2nr);
}
void MancalaServer::handlePlayer1TimeLimitQuery(Player1TimeLimitQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player1TimeLimitQuery handler: " << *msg << endl;
#endif

    ostringstream oss;
    oss << timeLimit1;
    Player1TimeLimitResponse* tl1 =
	new Player1TimeLimitResponse(oss.str());
    static_cast<AIO*>(io)->putMessage(tl1);
}
void MancalaServer::handlePlayer2TimeLimitQuery(Player2TimeLimitQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: Player2TimeLimitQuery handler: " << *msg << endl;
#endif

    ostringstream oss;
    oss << timeLimit2;
    Player2TimeLimitResponse* tl2 =
	new Player2TimeLimitResponse(oss.str());
    static_cast<AIO*>(io)->putMessage(tl2);
}
void MancalaServer::handleMaxSpectatorsQuery(MaxSpectatorsQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: MaxSpectatorsQuery handler: " << *msg << endl;
#endif

    ostringstream oss;
    oss << maxSpectators;
    MaxSpectatorsResponse* msr =
	new MaxSpectatorsResponse(oss.str());
    static_cast<AIO*>(io)->putMessage(msr);
}
void MancalaServer::handleGameTypeQuery(GameTypeQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: GameTypeQuery handler: " << *msg << endl;
#endif

    GameTypeResponse* gtr =
	new GameTypeResponse(gameType);
    static_cast<AIO*>(io)->putMessage(gtr);
}
void MancalaServer::handleRuleSetQuery(RuleSetQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: RuleSetQuery handler: " << *msg << endl;
#endif

    ostringstream oss;
    oss << gameType << " " << binsPerSide << " " << piecesPerBin
	<< " " << maxDepth << " " << timeLimit1 << " "
	<< timeLimit2;
    RuleSetResponse* ruleSetResponse =
	new RuleSetResponse(oss.str());
    static_cast<AIO*>(io)->putMessage(ruleSetResponse);
}

void MancalaServer::handleGameBoardQuery(GameBoardQuery* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: GameBoardQuery handler: " << *msg << endl;
#endif
    sendAll(msg);
}

void MancalaServer::handleGameBoardResponse(GameBoardResponse* msg, void* io)
{
#ifdef MANCALA_SERVER_DEBUG
    cout << "Server Debug: GameBoardResponse handler: " << *msg << endl;
#endif
    sendAll(msg);
}

