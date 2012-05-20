/*
  messages.h

  The networking will be done by passing and reading messages
  The way we tell the type of message a given message is by
  it's prefix string. Ideally this could be just a char, but
  we'll use strings to make it more readable for now

  when we send a message over the network, it will be sent as:
  "prefix:data"


  Messages implemented: 
  
  (ones with a * are superclasses, and have more
  specific subclasses. To use one of these superclasses, you must
  modify the constructor of MessageFactory to register a prefix
  for the class)

  message
  -------
  *Message
  TextMessage
  *ConnectMessage
  ConnectPlayer1Message -- IO sends this and either gets it back,
  ConnectPlayer2Message  + or gets an InvalidConnectionMessage
                           data should be the name of the player
  ConnectSpectatorMessage -- same
  DisconnectMessage -- IO sends this with data "1", "2", or "0"
                       for player1, player2, or spectator
		       Server sends this with "1","2","0"
		       or "-1" for server going down
  Player1MoveRequestMessage -- sent by the server, only the
  Player2MoveRequestMessage  + appropriate player shold respond
                               with a PlayerxMoveMessage
  Player1MoveMessage
  Player2MoveMessage
  GameStartMessage
  GameOverMessage
  Player1WinnerMessage
  Player2WinnerMessage
  TieMessage
  *WarningMessage
  *ErrorMessage
  InvalidConnectionMessage

  Player1ConnectedQuery
  Player2ConnectedQuery
  Player1NameQuery
  Player2NameQuery
  Player1TimeLimitQuery
  Player2TimeLimitQuery
  MaxSpectatorsQuery
  GameTypeQuery
  RuleSetQuery -- request a serialized RuleSet
  GameBoardQuery -- someone sends this to the server, who sends it
                    out to all clients, who in turn send back their
		    serializations of the current game board

  Player1ConnectedResponse -- data is "0" or "1" 
  Player2ConnectedResponse  + for false, true
  Player1NameResponse -- data is player's name
  Player2NameResponse  + data is player's name
  Player1TimeLimitResponse -- data is timeLimit1
  Player2TimeLimitResponse -- data is timeLimit2
  MaxSpectatorsResponse -- data is mexSpectators
  GameTypeResponse -- data is "E" or "W"
  RuleSetResponse -- returns a serialized RuleSet
  GameBoardResponse
*/

#ifndef BEN_TAITELBAUM_MESSAGES_H
#define BEN_TAITELBAUM_MESSAGES_H

#include <map>
#include <iostream>

class MessageHandler;

class Message
{
private:
    std::string data;
protected:
    std::string prefix;
public:
    Message(std::string prefix="", std::string data="");
    virtual ~Message();
    
    virtual std::string getPrefix();
    virtual std::string getData();

    // returns if this Message is the same type of Message
    // as message
    virtual bool isTypeOf(Message* message);

    // create a new Message with this same type
    // this class will not delete this new Message!
    // sort of like a clone() method in Java
    virtual Message* createNew(std::string data="");

    virtual void accept(MessageHandler* handler, void* params);
};

std::ostream& operator<<(std::ostream& sout, Message& message);

class TextMessage : public Message
{
public:
    TextMessage(std::string data="");
    virtual ~TextMessage();    
    virtual TextMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class ConnectMessage : public Message
{
public:
    ConnectMessage(std::string prefix="", std::string data="");
    virtual ~ConnectMessage();    
    virtual ConnectMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class ConnectPlayer1Message : public ConnectMessage
{
public:
    ConnectPlayer1Message(std::string data="");
    virtual ~ConnectPlayer1Message();    
    virtual ConnectPlayer1Message* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class ConnectPlayer2Message : public ConnectMessage
{
public:
    ConnectPlayer2Message(std::string data="");
    virtual ~ConnectPlayer2Message();    
    virtual ConnectPlayer2Message* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class ConnectSpectatorMessage : public ConnectMessage
{
public:
    ConnectSpectatorMessage(std::string data="");
    virtual ~ConnectSpectatorMessage();    
    virtual ConnectSpectatorMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class DisconnectMessage : public ConnectMessage
{
public:
    DisconnectMessage(std::string data="");
    virtual ~DisconnectMessage();    
    virtual DisconnectMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1MoveRequestMessage : public Message
{
public:
    Player1MoveRequestMessage(std::string data="");
    virtual ~Player1MoveRequestMessage();    
    virtual Player1MoveRequestMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2MoveRequestMessage : public Message
{
public:
    Player2MoveRequestMessage(std::string data="");
    virtual ~Player2MoveRequestMessage();    
    virtual Player2MoveRequestMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1MoveMessage : public Message
{
public:
    Player1MoveMessage(std::string data="");
    virtual ~Player1MoveMessage();    
    virtual Player1MoveMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2MoveMessage : public Message
{
public:
    Player2MoveMessage(std::string data="");
    virtual ~Player2MoveMessage();    
    virtual Player2MoveMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};



class GameStartMessage : public Message
{
public:
    GameStartMessage(std::string data="");
    virtual ~GameStartMessage();    
    virtual GameStartMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class GameOverMessage : public Message
{
public:
    GameOverMessage(std::string data="");
    virtual ~GameOverMessage();    
    virtual GameOverMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1WinnerMessage : public Message
{
public:
    Player1WinnerMessage(std::string data="");
    virtual ~Player1WinnerMessage();    
    virtual Player1WinnerMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2WinnerMessage : public Message
{
public:
    Player2WinnerMessage(std::string data="");
    virtual ~Player2WinnerMessage();    
    virtual Player2WinnerMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class TieMessage : public Message
{
public:
    TieMessage(std::string data="");
    virtual ~TieMessage();    
    virtual TieMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};



// a superclass of all the warning Messages
// this should not be instantiated by itself
class WarningMessage : public Message
{
public:
    WarningMessage(std::string prefix="", std::string data="");
    virtual ~WarningMessage();    
    virtual WarningMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

// a superclass of all the error Messages
// this should not be instantiated by itself
class ErrorMessage : public Message
{
public:
    ErrorMessage(std::string prefix="", std::string data="");
    virtual ~ErrorMessage();    
    virtual ErrorMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class InvalidConnectionMessage : public ErrorMessage
{
public:
    InvalidConnectionMessage(std::string data="");
    virtual ~InvalidConnectionMessage();        
    virtual InvalidConnectionMessage* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

// query and response messages: used to access info about the server
class Player1ConnectedQuery : public Message
{
public:
    Player1ConnectedQuery(std::string data="");
    virtual ~Player1ConnectedQuery();
    virtual Player1ConnectedQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2ConnectedQuery : public Message
{
public:
    Player2ConnectedQuery(std::string data="");
    virtual ~Player2ConnectedQuery();
    virtual Player2ConnectedQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1NameQuery : public Message
{
public:
    Player1NameQuery(std::string data="");
    virtual ~Player1NameQuery();
    virtual Player1NameQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2NameQuery : public Message
{
public:
    Player2NameQuery(std::string data="");
    virtual ~Player2NameQuery();
    virtual Player2NameQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1TimeLimitQuery : public Message
{
public:
    Player1TimeLimitQuery(std::string data="");
    virtual ~Player1TimeLimitQuery();
    virtual Player1TimeLimitQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2TimeLimitQuery : public Message
{
public:
    Player2TimeLimitQuery(std::string data="");
    virtual ~Player2TimeLimitQuery();
    virtual Player2TimeLimitQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class MaxSpectatorsQuery : public Message
{
public:
    MaxSpectatorsQuery(std::string data="");
    virtual ~MaxSpectatorsQuery();
    virtual MaxSpectatorsQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class GameTypeQuery : public Message
{
public:
    GameTypeQuery(std::string data="");
    virtual ~GameTypeQuery();
    virtual GameTypeQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class RuleSetQuery : public Message
{
public:
    RuleSetQuery(std::string data="");
    virtual ~RuleSetQuery();
    virtual RuleSetQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};


class GameBoardQuery : public Message
{
public:
    GameBoardQuery(std::string data="");
    virtual ~GameBoardQuery();
    virtual GameBoardQuery* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};
// responses

class Player1ConnectedResponse : public Message
{
public:
    Player1ConnectedResponse(std::string data="");
    virtual ~Player1ConnectedResponse();
    virtual Player1ConnectedResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2ConnectedResponse : public Message
{
public:
    Player2ConnectedResponse(std::string data="");
    virtual ~Player2ConnectedResponse();
    virtual Player2ConnectedResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1NameResponse : public Message
{
public:
    Player1NameResponse(std::string data="");
    virtual ~Player1NameResponse();
    virtual Player1NameResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2NameResponse : public Message
{
public:
    Player2NameResponse(std::string data="");
    virtual ~Player2NameResponse();
    virtual Player2NameResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player1TimeLimitResponse : public Message
{
public:
    Player1TimeLimitResponse(std::string data="");
    virtual ~Player1TimeLimitResponse();
    virtual Player1TimeLimitResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class Player2TimeLimitResponse : public Message
{
public:
    Player2TimeLimitResponse(std::string data="");
    virtual ~Player2TimeLimitResponse();
    virtual Player2TimeLimitResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class MaxSpectatorsResponse : public Message
{
public:
    MaxSpectatorsResponse(std::string data="");
    virtual ~MaxSpectatorsResponse();
    virtual MaxSpectatorsResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class GameTypeResponse : public Message
{
public:
    GameTypeResponse(std::string dat="");
    virtual ~GameTypeResponse();
    virtual GameTypeResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class RuleSetResponse : public Message
{
public:
    RuleSetResponse(std::string data="");
    virtual ~RuleSetResponse();
    virtual RuleSetResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};

class GameBoardResponse : public Message
{
public:
    GameBoardResponse(std::string data="");
    virtual ~GameBoardResponse();
    virtual GameBoardResponse* createNew(std::string data="");
    virtual void accept(MessageHandler* handler, void* params);
};


/*
  MessageFactory

  the main point of this class is to quickly create
  the appropriate Message from a string received over the network

  this is a static class, and will be initialized the first
  time one of its methods is called.
 */
class MessageFactory
{
private:
    // map prefixes to Messages
    std::map<std::string, Message*> messages;
    static MessageFactory* instance;
    int maxLength; // the largest message in bytes
    MessageFactory();
public:
    static MessageFactory* Singleton();
    
    // add a new prefix/message combo to the pair
    // the prefix will be the message->getPrefix()
    void registerPrefix(Message* message);

    // get the appropriate type of Message for this string
    // user is responsible for deleting return value
    Message* getMessage(std::string message);

    // returns the same as getMessage, but this is static
    // so you don't have to call MessageFactory::Singleton()->getMessage(...)
    static Message* createMessage(std::string message);

    // returns the largest possible message in bytes
    static int getMaxLength();
};

// subclass this class and override the methods you care about
class MessageHandler
{
public:
    // this is the default handler that will be called unless
    // the specific handler is overridden
    virtual void handleMessage(Message* msg, void* params);
    virtual void handleTextMessage(TextMessage* msg, void* params);
    virtual void handleConnectMessage(ConnectMessage* msg, void* params);
    virtual void handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params);
    virtual void handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params);
    virtual void handleConnectSpectatorMessage(ConnectSpectatorMessage* msg, void* params);
    virtual void handleDisconnectMessage(DisconnectMessage* msg, void* params);
    virtual void handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* params);
    virtual void handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* params);
    virtual void handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params);
    virtual void handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params);
    virtual void handleGameStartMessage(GameStartMessage* msg, void* params);
    virtual void handleGameOverMessage(GameOverMessage* msg, void* params);
    virtual void handlePlayer1WinnerMessage(Player1WinnerMessage* msg, void* params);
    virtual void handlePlayer2WinnerMessage(Player2WinnerMessage* msg, void* params);
    virtual void handleTieMessage(TieMessage* msg, void* params);
    virtual void handleWarningMessage(WarningMessage* msg, void* params);
    virtual void handleErrorMessage(ErrorMessage* msg, void* params);
    virtual void handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params);
    virtual void handlePlayer1ConnectedQuery(Player1ConnectedQuery* msg, void* params);
    virtual void handlePlayer2ConnectedQuery(Player2ConnectedQuery* msg, void* params);
    virtual void handlePlayer1NameQuery(Player1NameQuery* msg, void* params);
    virtual void handlePlayer2NameQuery(Player2NameQuery* msg, void* params);
    virtual void handlePlayer1TimeLimitQuery(Player1TimeLimitQuery* msg, void* params);
    virtual void handlePlayer2TimeLimitQuery(Player2TimeLimitQuery* msg, void* params);
    virtual void handleMaxSpectatorsQuery(MaxSpectatorsQuery* msg, void* params);
    virtual void handleGameTypeQuery(GameTypeQuery* msg, void* params);
    virtual void handleRuleSetQuery(RuleSetQuery* msg, void* params);
    virtual void handleGameBoardQuery(GameBoardQuery* msg, void* params);

    virtual void handlePlayer1ConnectedResponse(Player1ConnectedResponse* msg, void* params);
    virtual void handlePlayer2ConnectedResponse(Player2ConnectedResponse* msg, void* params);
    virtual void handlePlayer1NameResponse(Player1NameResponse* msg, void* params);
    virtual void handlePlayer2NameResponse(Player2NameResponse* msg, void* params);
    virtual void handlePlayer1TimeLimitResponse(Player1TimeLimitResponse* msg, void* params);
    virtual void handlePlayer2TimeLimitResponse(Player2TimeLimitResponse* msg, void* params);
    virtual void handleMaxSpectatorsResponse(MaxSpectatorsResponse* msg, void* params);
    virtual void handleGameTypeResponse(GameTypeResponse* msg, void* params);
    virtual void handleRuleSetResponse(RuleSetResponse* msg, void* params);
    virtual void handleGameBoardResponse(GameBoardResponse* msg, void* params);
};

#endif // !BENTAITELBAUM_TAITELBAUM_MESSAGES_H
