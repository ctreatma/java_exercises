/*
    io.h

    These are all the methods and classes dealing with IO for mancala v2.0
    The NetIO class relies heavily on the SDL_net package, and a lot
    of code was taken from the SDL_net demo package available at:
    http://jcatki.no-ip.org/SDL_net/

    When we send a network message, the format will be:
    <id>:<message>

    for example, when Player1 sends a message, it will be something like:
    Player1:this is a my name

 */

#ifndef BEN_TAITELBAUM_IO_H
#define BEN_TAITELBAUM_IO_H

#ifdef _MSC_VER
#error MSVC will not prodice a working file, select fails on stdin
#endif

#ifdef MANCALA_IO_DEBUG
#define MANCALA_NET_DEBUG
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
#include <queue>
#include <map>
#include "tcputil.h"
#include "messages.h"
#include "game_board.h"
#include "rule_set.h"
#include "player.h"
#include "minimax.h"

class Message;
class MancalaServer;

class AIO : public MessageHandler
{
protected:
    std::queue<Message*> out_buffer;
    int message_count;

    // stick a message into the queue to be read through getMessage
    virtual void enqueueMessage(Message* msg);

    // so I can leave GameBoard::resetDepth() protected and only
    // have to befriend class AIO
    virtual void resetDepth(GameBoard& gameBoard);

    // same reason as above
    virtual GameBoard* createGameBoard(RuleSet* ruleSet, std::string data="");

    bool connected; // initialized to false
    bool gameStarted; // initialized to false

    bool gameBoardQueryPending; // has someone asked for the gameBoard?

    std::string player1Name;
    std::string player2Name;

    GameBoard* gameBoard;
    RuleSet* ruleSet;

    APlayer* player; // normally just a TextPlayer, but can also
                     // be a MinimaxPlayerX or 
                     // GreedyPlayer, or RandomPlayer, ...

    // overridden by subclasses
    virtual void showGameBoard();
    virtual void showHelp();

    virtual void resetTimer();
    virtual int getTimerValue();

public:
    AIO(); // just a default constructor...shouldn't use it though...
    virtual ~AIO();

    // think of this as a polling method
    // AInput can return NULL whenever this is called and
    // it doesn't have any messages to send
    // don't worry about deleting the Message*, this will
    // be taken care of by the server
    // (or the NetIO wrapper)
    virtual Message* getMessage();

    // AIO is responsible for deleting this Message*,
    // so if you're going to send a message to many AIO's,
    // be sure to clone it!
    // this calls handleMessage(Message* message, NULL);
    virtual void putMessage(Message* msg);

    virtual APlayer* getPlayer();

    // this does NOT (I repeat: NOT) delete the current player,
    // so you better do that if you know it's dead
    virtual void setPlayer(APlayer* player);

    virtual void handleGameBoardQuery(GameBoardQuery* msg, void* params);
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
    virtual void handlePlayer1NameResponse(Player1NameResponse* msg, void* params);
    virtual void handlePlayer2NameResponse(Player2NameResponse* msg, void* params);
    virtual void handleRuleSetResponse(RuleSetResponse* msg, void* params);
};


/*
  standard procedure for TextIO's:

  1. initialize a TextIO for the proper player and connect it to the server
     (or a NetIO wrapper)

  2. get the game parameters:
     send a RuleSetQuery and respond to a RuleSetResponse
  
  3. send a PlayerXConnect or SpectatorConnect message
     with my name as data (should probably send a timestamp as data,
                           but oh well...)
  4. when I receive the PlayerXConnect or SpectatorConnect message back,
     I set connected to true
     if I get an InvalidPlayerMessage, connect as a spectator
     or give up...

  5. wait for a GameStartMessage, and request player names

  6. respond to MoveRequests and whatever else is nice to respond to
*/

class TextIO : public AIO
{
protected:
    virtual void showGameBoard();
    virtual void showHelp();
public:
    // sets up some client server communication
    // should call this from sublasses 
    // (you know, " : TextIO() " after the constructor)
    TextIO();
    virtual ~TextIO();

    // TextIO is responsible for deleting these msg's
    virtual void handleMessage(Message* msg, void* params);
    virtual void handleTextMessage(TextMessage* msg, void* params);
    virtual void handleDisconnectMessage(DisconnectMessage* msg, void* params);
    virtual void handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params);
    virtual void handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params);
    virtual void handleGameStartMessage(GameStartMessage* msg, void* params);
    virtual void handleGameOverMessage(GameOverMessage* msg, void* params);
    virtual void handlePlayer1WinnerMessage(Player1WinnerMessage* msg, void* params);
    virtual void handlePlayer2WinnerMessage(Player2WinnerMessage* msg, void* params);
    virtual void handleTieMessage(TieMessage* msg, void* params);
};

class Player1TextIO : public TextIO
{
public:
    Player1TextIO(std::string name);
    virtual ~Player1TextIO();
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params);
    virtual void handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* params);
    virtual void handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params);
};

class Player2TextIO : public TextIO
{
public:
    Player2TextIO(std::string name);
    virtual ~Player2TextIO();
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
    virtual void handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params);
    virtual void handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* params);
    virtual void handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params);
};

// for when both players are on the same console
class Player12TextIO : public TextIO
{
protected:
    APlayer* player2;
public:
    Player12TextIO(std::string player1Name, std::string player2Name);
    virtual ~Player12TextIO();

    virtual APlayer* getPlayer1();
    virtual void setPlayer1(APlayer* player1);
    virtual APlayer* getPlayer2();
    virtual void setPlayer2(APlayer* player2);

    virtual void handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* params);
    virtual void handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params);
    virtual void handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* params);
    virtual void handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params);
};

class SpectatorTextIO : public TextIO
{
private:
    std::string name;
    bool haveGameBoard; // initialized to false
public:
    SpectatorTextIO(std::string name);
    virtual ~SpectatorTextIO();
    virtual void handleConnectSpectatorMessage(ConnectSpectatorMessage* msg, void* params);
    virtual void handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params);

    // yeah, don't forget to override this or you'll be responding
    // to your own query..moron...
    virtual void handleGameBoardQuery(GameBoardQuery* msg, void* params);
    virtual void handleGameBoardResponse(GameBoardResponse* msg, void* params);
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
};




/**************************** NetIO ******************************
 *
 * a ServerNetIO should be connected to a server wishing to receive
 * network connections. it is basically a multi socket server that
 * runs on its own thread. this is an AIO because it needs to
 * connect to the server
 *
 * a ClientNetIO is a client socket that relays messages to
 * the AIO's connected to it. This runs on it's own thread as well.
 * this is an AIO for stylistic reasons, since it's more than
 * just a message handler
 *
*/

// you must call ServerNetIO::start() to spawn the thread,
// which will not be destroyed until
// the server sends a disconnect message
class ServerNetIO : public AIO
{
protected:
    SDL_Thread *thread;
    TCPsocket sock;
    SDLNet_SocketSet socketSet;
    bool isRunning;
    bool shutdown_requested; // currently serves no purpose
    std::vector<TCPsocket> sockets;
    char* data;
    int maxSockets;

    // a static function because SDL_net requires a static function
    static int CreateThread(void* serverNetIO);
    int thread_func();
    
public:
    ServerNetIO(unsigned int port);
    virtual ~ServerNetIO();

    virtual void start();
    virtual void stop();
    virtual void kill();

    // this generic message handler should be good for most purposes
    // except for the disconnect message
    virtual void handleMessage(Message* msg, void* params);
    virtual void handleDisconnectMessage(DisconnectMessage* msg, void* params);

    // override the AIO methods also
    virtual void handleGameBoardQuery(GameBoardQuery* msg, void* params);
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
    virtual void handlePlayer1NameResponse(Player1NameResponse* msg, void* params);
    virtual void handlePlayer2NameResponse(Player2NameResponse* msg, void* params);
    virtual void handleRuleSetResponse(RuleSetResponse* msg, void* params);
};


// you must call ClientNetIO::start() to spawn the thread
// which will not be terminated until the server sends
// a disconnect message or requestShutdown() or stop() is called
class ClientNetIO : public AIO
{
protected:
    TCPsocket sock;
    SDLNet_SocketSet socketSet; // if we stick sock in this,
                                // no waiting for getNetMsg() to return...
    char* data;
    std::vector<AIO*> inputs_outputs;
    SDL_Thread* thread;
    bool isRunning, shutdown_requested;

    // a static function because SDL_net requires a static function
    static int CreateThread(void* serverNetIO);
    int thread_func();
public:
    ClientNetIO(std::string server, unsigned int port);
    virtual ~ClientNetIO();

    // returns the exit status or -1 if it's already running
    virtual int run();

    // ask thread to end gracefully
    virtual void stop();
    // kill the thread -- probably a bad idea...
    virtual void kill();

    virtual void addIO(AIO* io);
    virtual void removeIO(AIO* io);

    // this generic message handler should be good for most purposes
    // except for the disconnect message
    // when i decode a network message, i call msg->accept(this, NULL)
    // so that the appropriate handleMessage function is called
    // then inside handleMessage, I pass the message off to all the
    // io's in inputs_outputs
    virtual void handleMessage(Message* msg, void* params);
    virtual void handleDisconnectMessage(DisconnectMessage* msg, void* params);
    
    // override the AIO methods also
    virtual void handleGameBoardQuery(GameBoardQuery* msg, void* params);
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
    virtual void handlePlayer1NameResponse(Player1NameResponse* msg, void* params);
    virtual void handlePlayer2NameResponse(Player2NameResponse* msg, void* params);
    virtual void handleRuleSetResponse(RuleSetResponse* msg, void* params);
};


// SinglePlayerMultiServerNetIO
// when this IO receives a connect message, it will spawn off
// a new thread (keeping the existing connection), with a new 
// SinglePlayuerMultiServerNetIO (start() won't be called again though)
// and start up
// a new server for that game. This enables you to run multiple
// games over the same port.

// you must call start() to have anything happen
class SinglePlayerMultiServerNetIO : public AIO
{
protected:
    SDL_Thread *thread;
    TCPsocket sock;
    TCPsocket client_sock;
    bool isRunning;
    bool shutdown_requested; // currently serves no purpose
    int maxSockets;
    int serverPlayer; // 1 if player1, 2 if player2, -1 if still undecided
    APlayer* player; // same for all games
    std::string serverPlayerName; // again same for all games
    RuleSet* ruleSet; // will be the same for all games
    AIO* serverAIO; // just to hold the value so can be deleted
    
    // keep them in a vector so they won't be destroyed and yet
    // I won't have to deal with pointer crap
    std::vector<SinglePlayerMultiServerNetIO*> extra_ios;

    MancalaServer* server;
    char* data;

    // a static function because SDL_net requires a static function
    static int CreateThread(void* serverNetIO);
    static int CreateServerThread(void* serverNetIO);
    int thread_func();
    
public:
    SinglePlayerMultiServerNetIO(RuleSet* ruleSet, APlayer* player, 
				 std::string playerName,
				 unsigned int port, int max_sockets = 15);
    SinglePlayerMultiServerNetIO(RuleSet* ruleSet, APlayer* player, 
				 std::string playerName,
				 TCPsocket client_sock, 
				 int max_sockets = 15);
    virtual ~SinglePlayerMultiServerNetIO();

    virtual int run();
    virtual void stop();
    virtual void kill();

    // this generic message handler should be good for most purposes
    // except for the disconnect message
    virtual void handleMessage(Message* msg, void* params);
    virtual void handleDisconnectMessage(DisconnectMessage* msg, void* params);

    // override the AIO methods also
    virtual void handleGameBoardQuery(GameBoardQuery* msg, void* params);
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
    virtual void handlePlayer1NameResponse(Player1NameResponse* msg, void* params);
    virtual void handlePlayer2NameResponse(Player2NameResponse* msg, void* params);
    virtual void handleRuleSetResponse(RuleSetResponse* msg, void* params);
};


#endif // BEN_TAITELBAUM_IO_H
