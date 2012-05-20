/*
    server.h

    Implements a multiple socket server to be used with Mancala
*/

#ifndef BEN_TAITELBAUM_SERVER_H
#define BEN_TAITELBAUM_SERVER_H

#ifdef _MSC_VER
#error MSVC will not prodice a working file, select fails on stdin
#endif

#include <string.h>
#include <string>
#include <sys/types.h>
#ifdef WIN32
#include <windows.h>
#endif
#include <stdlib.h>
#include <SDL/SDL.h>
#include <SDL/SDL_net.h>
#include <SDL/SDL_thread.h>
#include "tcputil.h"
#include "messages.h"
#include "io.h"

// A MancalaServer runs in a separate thread.
class MancalaServer : public MessageHandler
{
private:
    SDL_Thread *thread;
    std::vector<AIO*> inputs_outputs;
    bool player1Connected;
    bool player2Connected;
    std::string player1Name, player2Name;
    int binsPerSide, piecesPerBin, maxDepth, 
	timeLimit1, timeLimit2;
    int maxSpectators;
    int numSpectators;

    // send the same message to all outputs
    // it makes copies of the msg, so don't worry about memory management
    void sendAll(Message* msg);

public:
    MancalaServer(int bps=7, int ppb=4, int tl1=120000,
		  int tl2=120000, int md=7, int ms=15,
		  std::string gt="E");
    MancalaServer(RuleSet* ruleSet, int max_spectators=15);
    virtual ~MancalaServer();

    // a static function that calls the proper thread_func
    static int CreateThread(void* server);

    // the type of Game -- so far only "E" and "W" are supported
    std::string gameType;
    RuleSet* ruleSet;
    // has there been a request to shutdown the server gracefully?
    bool shutdown_requested;

    // returns the exit status or -1 if it's already running
    int run();

    // shut down gracefully
    void stop();

    // just kill the server -- this may cause some clients to hang
    void kill();
    bool isRunning();

    void addIO(AIO* io);
    void removeIO(AIO* io);

    // DO NOT CALL THESE METHODS -- they're internal
    int thread_func();
    void handleMessage(Message* msg, void* io);
    void handleTextMessage(TextMessage* msg, void* io);
    void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* io);
    void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* io);
    void handleConnectSpectatorMessage(ConnectSpectatorMessage* msg, void* io);
    void handleDisconnectMessage(DisconnectMessage* msg, void* io);
    void handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* io);
    void handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* io);
    void handlePlayer1MoveMessage(Player1MoveMessage* msg, void* io);
    void handlePlayer2MoveMessage(Player2MoveMessage* msg, void* io);
    // void handleGameStartMessage(GameStartMessage* msg, void* io);
    void handleGameOverMessage(GameOverMessage* msg, void* io);
    void handlePlayer1WinnerMessage(Player1WinnerMessage* msg, void* io);
    void handlePlayer2WinnerMessage(Player2WinnerMessage* msg, void* io);
    void handleTieMessage(TieMessage* msg, void* io);
    // void handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* io);
    void handlePlayer1ConnectedQuery(Player1ConnectedQuery* msg, void* io);
    void handlePlayer2ConnectedQuery(Player2ConnectedQuery* msg, void* io);
    void handlePlayer1NameQuery(Player1NameQuery* msg, void* io);
    void handlePlayer2NameQuery(Player2NameQuery* msg, void* io);
    void handlePlayer1TimeLimitQuery(Player1TimeLimitQuery* msg, void* io);
    void handlePlayer2TimeLimitQuery(Player2TimeLimitQuery* msg, void* io);
    void handleMaxSpectatorsQuery(MaxSpectatorsQuery* msg, void* io);
    void handleGameTypeQuery(GameTypeQuery* msg, void* io);
    void handleRuleSetQuery(RuleSetQuery* msg, void* io);
    void handleGameBoardQuery(GameBoardQuery* msg, void* io);
    void handleGameBoardResponse(GameBoardResponse *msg, void* io);
};

#endif // BEN_TAITELBAUM_SERVER_H
