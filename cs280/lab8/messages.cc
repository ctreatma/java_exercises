#include "messages.h"
#include <sstream>
#include <iostream>

using namespace std;

Message::Message(string prefix, string data)
{
    this->prefix = prefix;
    this->data = data;
}
Message::~Message() {}

string Message::getPrefix() { return prefix; }
string Message::getData() { return data; }

// returns if this Message is the same type of Message
// as message
bool Message::isTypeOf(Message* message)
{
    if (message == NULL) return false;

    return (prefix == message->getPrefix());
}
Message* Message::createNew(string data)
{
   return new Message(prefix, data);
}

ostream& operator<<(ostream& sout, Message& message)
{
    sout << message.getPrefix() << ":" << message.getData();
    return sout;
}

TextMessage::TextMessage(string data)
    : Message("TXT", data)
{}
TextMessage::~TextMessage() {}
TextMessage* TextMessage::createNew(string data)
{
    return new TextMessage(data);
}

ConnectMessage::ConnectMessage(string prefix, string data)
    : Message(prefix, data)
{}
ConnectMessage::~ConnectMessage() {}
ConnectMessage* ConnectMessage::createNew(string data)
{
    return new ConnectMessage(prefix, data);
}

ConnectPlayer1Message::ConnectPlayer1Message(string data)
    : ConnectMessage("CONP1", data)
{}
ConnectPlayer1Message::~ConnectPlayer1Message() {}
ConnectPlayer1Message* ConnectPlayer1Message::createNew(string data)
{
    return new ConnectPlayer1Message(data);
}

ConnectPlayer2Message::ConnectPlayer2Message(string data)
    : ConnectMessage("CONP2", data)
{}
ConnectPlayer2Message::~ConnectPlayer2Message() {}
ConnectPlayer2Message* ConnectPlayer2Message::createNew(string data)
{
    return new ConnectPlayer2Message(data);
}

ConnectSpectatorMessage::ConnectSpectatorMessage(string data)
    : ConnectMessage("CONSP", data)
{}
ConnectSpectatorMessage::~ConnectSpectatorMessage() {}
ConnectSpectatorMessage* ConnectSpectatorMessage::
createNew(string data)
{
    return new ConnectSpectatorMessage(data);
}

DisconnectMessage::DisconnectMessage(string data)
    : ConnectMessage("DISC", data)
{}
DisconnectMessage::~DisconnectMessage() {}
DisconnectMessage* DisconnectMessage::
createNew(string data)
{
    return new DisconnectMessage(data);
}


Player1MoveRequestMessage::Player1MoveRequestMessage(string data)
    : Message("P1MREQ", data)
{}
Player1MoveRequestMessage::~Player1MoveRequestMessage() {}
Player1MoveRequestMessage* Player1MoveRequestMessage::
createNew(string data)
{
    return new Player1MoveRequestMessage(data);
}

Player2MoveRequestMessage::Player2MoveRequestMessage(string data)
    : Message("P2MREQ", data)
{}
Player2MoveRequestMessage::~Player2MoveRequestMessage() {}
Player2MoveRequestMessage* Player2MoveRequestMessage::
createNew(string data)
{
    return new Player2MoveRequestMessage(data);
}

Player1MoveMessage::Player1MoveMessage(string data)
    : Message("P1M", data)
{}
Player1MoveMessage::~Player1MoveMessage() {}
Player1MoveMessage* Player1MoveMessage::createNew(string data)
{
    return new Player1MoveMessage(data);
}

Player2MoveMessage::Player2MoveMessage(string data)
    : Message("P2M", data)
{}
Player2MoveMessage::~Player2MoveMessage() {}
Player2MoveMessage* Player2MoveMessage::createNew(string data)
{
    return new Player2MoveMessage(data);
}


GameStartMessage::GameStartMessage(string data)
    : Message("GAMESTART", data)
{}
GameStartMessage::~GameStartMessage() {}
GameStartMessage* GameStartMessage::createNew(string data)
{
    return new GameStartMessage(data);
}

GameOverMessage::GameOverMessage(string data)
    : Message("GAMEOVER", data)
{}
GameOverMessage::~GameOverMessage() {}
GameOverMessage* GameOverMessage::createNew(string data)
{
    return new GameOverMessage(data);
}

Player1WinnerMessage::Player1WinnerMessage(string data)
    : Message("P1W", data)
{}
Player1WinnerMessage::~Player1WinnerMessage() {}
Player1WinnerMessage* Player1WinnerMessage::createNew(string data)
{
    return new Player1WinnerMessage(data);
}

Player2WinnerMessage::Player2WinnerMessage(string data)
    : Message("P2W", data)
{}
Player2WinnerMessage::~Player2WinnerMessage() {}
Player2WinnerMessage* Player2WinnerMessage::createNew(string data)
{
    return new Player2WinnerMessage(data);
}

TieMessage::TieMessage(string data)
    : Message("TIE", data)
{}
TieMessage::~TieMessage() {}
TieMessage* TieMessage::createNew(string data)
{
    return new TieMessage(data);
}



WarningMessage::
WarningMessage(string prefix, string data)
    : Message("WARNING", data)
{}
WarningMessage::~WarningMessage() {}
WarningMessage* WarningMessage::createNew(string data)
{
    return new WarningMessage(prefix, data);
}

ErrorMessage::ErrorMessage(string prefix, string data)
    : Message("ERROR", data)
{}
ErrorMessage::~ErrorMessage() {}
ErrorMessage* ErrorMessage::createNew(string data)
{
    return new ErrorMessage(prefix, data);
}

InvalidConnectionMessage::InvalidConnectionMessage(string data)
    : ErrorMessage("INVCON", data)
{}
InvalidConnectionMessage::~InvalidConnectionMessage() {}
InvalidConnectionMessage* InvalidConnectionMessage
::createNew(string data)
{
    return new InvalidConnectionMessage(data);
}


// queries
Player1ConnectedQuery::Player1ConnectedQuery(string data)
    : Message("QP1C", data)
{}
Player1ConnectedQuery::~Player1ConnectedQuery() {}
Player1ConnectedQuery* Player1ConnectedQuery::
createNew(string data)
{
    return new Player1ConnectedQuery(data);
}

Player2ConnectedQuery::Player2ConnectedQuery(string data)
    : Message("QP2C", data)
{}
Player2ConnectedQuery::~Player2ConnectedQuery() {}
Player2ConnectedQuery* Player2ConnectedQuery::
createNew(string data)
{
    return new Player2ConnectedQuery(data);
}

Player1NameQuery::Player1NameQuery(string data)
    : Message("QP1N", data)
{}
Player1NameQuery::~Player1NameQuery() {}
Player1NameQuery* Player1NameQuery::
createNew(string data)
{
    return new Player1NameQuery(data);
}

Player2NameQuery::Player2NameQuery(string data)
    : Message("QP2N", data)
{}
Player2NameQuery::~Player2NameQuery() {}
Player2NameQuery* Player2NameQuery::
createNew(string data)
{
    return new Player2NameQuery(data);
}

Player1TimeLimitQuery::Player1TimeLimitQuery(string data)
    : Message("QP1TL", data)
{}
Player1TimeLimitQuery::~Player1TimeLimitQuery() {}
Player1TimeLimitQuery* Player1TimeLimitQuery::
createNew(string data)
{
    return new Player1TimeLimitQuery(data);
}

Player2TimeLimitQuery::Player2TimeLimitQuery(string data)
    : Message("QP2TL", data)
{}
Player2TimeLimitQuery::~Player2TimeLimitQuery() {}
Player2TimeLimitQuery* Player2TimeLimitQuery::
createNew(string data)
{
    return new Player2TimeLimitQuery(data);
}

MaxSpectatorsQuery::MaxSpectatorsQuery(string data)
    : Message("QMC", data)
{}
MaxSpectatorsQuery::~MaxSpectatorsQuery() {}
MaxSpectatorsQuery* MaxSpectatorsQuery::
createNew(string data)
{
    return new MaxSpectatorsQuery(data);
}

GameTypeQuery::GameTypeQuery(string data)
    : Message("QGT", data)
{}
GameTypeQuery::~GameTypeQuery() {}
GameTypeQuery* GameTypeQuery::
createNew(string data)
{
    return new GameTypeQuery(data);
}

RuleSetQuery::RuleSetQuery(string data)
    : Message("QRS", data)
{}
RuleSetQuery::~RuleSetQuery() {}
RuleSetQuery* RuleSetQuery::
createNew(string data)
{
    return new RuleSetQuery(data);
}

GameBoardQuery::GameBoardQuery(string data)
    : Message("QGB", data)
{}
GameBoardQuery::~GameBoardQuery() {}
GameBoardQuery* GameBoardQuery::
createNew(string data)
{
    return new GameBoardQuery(data);
}


// responses

Player1ConnectedResponse::Player1ConnectedResponse(string data)
    : Message("RP1C", data)
{}
Player1ConnectedResponse::~Player1ConnectedResponse() {}
Player1ConnectedResponse* Player1ConnectedResponse::
createNew(string data)
{
    return new Player1ConnectedResponse(data);
}

Player2ConnectedResponse::Player2ConnectedResponse(string data)
    : Message("RP2C", data)
{}
Player2ConnectedResponse::~Player2ConnectedResponse() {}
Player2ConnectedResponse* Player2ConnectedResponse::
createNew(string data)
{
    return new Player2ConnectedResponse(data);
}

Player1NameResponse::Player1NameResponse(string data)
    : Message("RP1N", data)
{}
Player1NameResponse::~Player1NameResponse() {}
Player1NameResponse* Player1NameResponse::
createNew(string data)
{
    return new Player1NameResponse(data);
}

Player2NameResponse::Player2NameResponse(string data)
    : Message("RP2N", data)
{}
Player2NameResponse::~Player2NameResponse() {}
Player2NameResponse* Player2NameResponse::
createNew(string data)
{
    return new Player2NameResponse(data);
}

Player1TimeLimitResponse::Player1TimeLimitResponse(string data)
    : Message("RP1TL", data)
{}
Player1TimeLimitResponse::~Player1TimeLimitResponse() {}
Player1TimeLimitResponse* Player1TimeLimitResponse::
createNew(string data)
{
    return new Player1TimeLimitResponse(data);
}

Player2TimeLimitResponse::Player2TimeLimitResponse(string data)
    : Message("RP2TL", data)
{}
Player2TimeLimitResponse::~Player2TimeLimitResponse() {}
Player2TimeLimitResponse* Player2TimeLimitResponse::
createNew(string data)
{
    return new Player2TimeLimitResponse(data);
}

MaxSpectatorsResponse::MaxSpectatorsResponse(string data)
    : Message("RMC", data)
{}
MaxSpectatorsResponse::~MaxSpectatorsResponse() {}
MaxSpectatorsResponse* MaxSpectatorsResponse::
createNew(string data)
{
    return new MaxSpectatorsResponse(data);
}

GameTypeResponse::GameTypeResponse(string data)
    : Message("RGT", data)
{}
GameTypeResponse::~GameTypeResponse() {}
GameTypeResponse* GameTypeResponse::
createNew(string data)
{
    return new GameTypeResponse(data);
}

RuleSetResponse::RuleSetResponse(string data)
    : Message("RRS", data)
{}
RuleSetResponse::~RuleSetResponse() {}
RuleSetResponse* RuleSetResponse::
createNew(string data)
{
    return new RuleSetResponse(data);
}

GameBoardResponse::GameBoardResponse(string data)
    : Message("RGB", data)
{}
GameBoardResponse::~GameBoardResponse() {}
GameBoardResponse* GameBoardResponse::
createNew(string data)
{
    return new GameBoardResponse(data);
}


MessageFactory* MessageFactory::instance = NULL;

MessageFactory* MessageFactory::Singleton()
{
    if (instance == NULL)
    {
	instance = new MessageFactory();
    }
    return instance;
}
    
// add a new prefix/message combo to the pair
void MessageFactory::registerPrefix(Message* message)
{
    messages[message->getPrefix()] = message;
}

// get the appropriate type of Message for this string
Message* MessageFactory::getMessage(string message)
{
    ostringstream oss_prefix;
    ostringstream oss_data;
    istringstream iss(message);

    bool inPrefix = true;
    string::iterator iter = message.begin();
    for(;iter != message.end();iter++)
    {
	if (inPrefix)
	{
	    if (*iter == ':')
		inPrefix = false;
	    else
		oss_prefix << *iter;
	}
	else
	{
	    oss_data << *iter;
	}
    }

    if (oss_prefix.str() == "" || messages[oss_prefix.str()] == NULL)
    {
	return new Message(oss_data.str());
    }
    return messages[oss_prefix.str()]->createNew(oss_data.str());
}

Message* MessageFactory::createMessage(string message)
{
    return Singleton()->getMessage(message);
}

int MessageFactory::getMaxLength()
{
    return Singleton()->maxLength;
}

MessageFactory::MessageFactory()
{
    maxLength = 1024; // 1 KB is more than enough for now...

    // register all the prefixes
    registerPrefix(new ConnectPlayer1Message());
    registerPrefix(new ConnectPlayer2Message());
    registerPrefix(new ConnectSpectatorMessage());
    registerPrefix(new DisconnectMessage());
    registerPrefix(new Player1MoveRequestMessage());
    registerPrefix(new Player2MoveRequestMessage());
    registerPrefix(new Player1MoveMessage());
    registerPrefix(new Player2MoveMessage());
    registerPrefix(new GameStartMessage());
    registerPrefix(new GameOverMessage());
    registerPrefix(new Player1WinnerMessage());
    registerPrefix(new Player2WinnerMessage());
    registerPrefix(new TieMessage());
    registerPrefix(new InvalidConnectionMessage());
    registerPrefix(new Player1ConnectedQuery());
    registerPrefix(new Player2ConnectedQuery());
    registerPrefix(new Player1NameQuery());
    registerPrefix(new Player2NameQuery());
    registerPrefix(new Player1TimeLimitQuery());
    registerPrefix(new Player2TimeLimitQuery());
    registerPrefix(new MaxSpectatorsQuery());
    registerPrefix(new GameTypeQuery());
    registerPrefix(new RuleSetQuery());
    registerPrefix(new GameBoardQuery());
    registerPrefix(new Player1ConnectedResponse());
    registerPrefix(new Player2ConnectedResponse());
    registerPrefix(new Player1NameResponse());
    registerPrefix(new Player2NameResponse());
    registerPrefix(new Player1TimeLimitResponse());
    registerPrefix(new Player2TimeLimitResponse());
    registerPrefix(new MaxSpectatorsResponse());
    registerPrefix(new GameTypeResponse());
    registerPrefix(new RuleSetResponse());
    registerPrefix(new GameBoardResponse());
}

// all the message handling stuff..just easier
// to put it all together
void MessageHandler::
handleMessage(Message* msg, void* params)
{}
void MessageHandler::
handleTextMessage(TextMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleConnectMessage(ConnectMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleConnectSpectatorMessage(ConnectSpectatorMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleDisconnectMessage(DisconnectMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleGameStartMessage(GameStartMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleGameOverMessage(GameOverMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1WinnerMessage(Player1WinnerMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2WinnerMessage(Player2WinnerMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleTieMessage(TieMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleWarningMessage(WarningMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleErrorMessage(ErrorMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1ConnectedQuery(Player1ConnectedQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2ConnectedQuery(Player2ConnectedQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1NameQuery(Player1NameQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2NameQuery(Player2NameQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1TimeLimitQuery(Player1TimeLimitQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2TimeLimitQuery(Player2TimeLimitQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleMaxSpectatorsQuery(MaxSpectatorsQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleGameTypeQuery(GameTypeQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleRuleSetQuery(RuleSetQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleGameBoardQuery(GameBoardQuery* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1ConnectedResponse(Player1ConnectedResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2ConnectedResponse(Player2ConnectedResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1NameResponse(Player1NameResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2NameResponse(Player2NameResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer1TimeLimitResponse(Player1TimeLimitResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handlePlayer2TimeLimitResponse(Player2TimeLimitResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleMaxSpectatorsResponse(MaxSpectatorsResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleGameTypeResponse(GameTypeResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleRuleSetResponse(RuleSetResponse* msg, void* params)
{
    handleMessage(msg, params);
}
void MessageHandler::
handleGameBoardResponse(GameBoardResponse* msg, void* params)
{
    handleMessage(msg, params);
}



void Message::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Message::accept called on:" << this << endl;
#endif
    handler->handleMessage(this, params);
}
void TextMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: TextMessage::accept called on:" << this << endl;
#endif

    handler->handleTextMessage(this, params);
}
void ConnectMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: ConnectMessage::accept called on:" << this << endl;
#endif

    handler->handleConnectMessage(this, params);
}
void ConnectPlayer1Message::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: ConnectPlayer1Message::accept called on:" << this << endl;
#endif

    handler->handleConnectPlayer1Message(this, params);
}
void ConnectPlayer2Message::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: ConnectPlayer2Message::accept called on:" << this << endl;
#endif

    handler->handleConnectPlayer2Message(this, params);
}
void ConnectSpectatorMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: ConnectSpectatorMessage::accept called on:" << this << endl;
#endif

    handler->handleConnectSpectatorMessage(this, params);
}
void DisconnectMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: DisconnectMessage::accept called on:" << this << endl;
#endif

    handler->handleDisconnectMessage(this, params);
}
void Player1MoveRequestMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1MoveRequestMessage::accept called on:" << this << endl;
#endif

    handler->handlePlayer1MoveRequestMessage(this, params);
}
void Player2MoveRequestMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2MoveRequestMessage::accept called on:" << this << endl;
#endif

    handler->handlePlayer2MoveRequestMessage(this, params);
}
void Player1MoveMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1MoveMessage::accept called on:" << this << endl;
#endif

    handler->handlePlayer1MoveMessage(this, params);
}
void Player2MoveMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2MoveMessage::accept called on:" << this << endl;
#endif

    handler->handlePlayer2MoveMessage(this, params);
}
void GameStartMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: GameStartMessage::accept called on:" << this << endl;
#endif

    handler->handleGameStartMessage(this, params);
}
void GameOverMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: GameOverMessage::accept called on:" << this << endl;
#endif

    handler->handleGameOverMessage(this, params);
}
void Player1WinnerMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1WinnerMessage::accept called on:" << this << endl;
#endif

    handler->handlePlayer1WinnerMessage(this, params);
}
void Player2WinnerMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2WinnerMessage::accept called on:" << this << endl;
#endif

    handler->handlePlayer2WinnerMessage(this, params);
}
void TieMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: TieMessage::accept called on:" << this << endl;
#endif

    handler->handleTieMessage(this, params);
}
void WarningMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: WarningMessage::accept called on:" << this << endl;
#endif

    handler->handleWarningMessage(this, params);
}
void ErrorMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: ErrorMessage::accept called on:" << this << endl;
#endif

    handler->handleErrorMessage(this, params);
}
void InvalidConnectionMessage::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: InvalidConnectionMessage::accept called on:" << this << endl;
#endif

    handler->handleInvalidConnectionMessage(this, params);
}
void Player1ConnectedQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1ConnectedQuery::accept called on:" << this << endl;
#endif

    handler->handlePlayer1ConnectedQuery(this, params);
}
void Player2ConnectedQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2ConnectedQuery::accept called on:" << this << endl;
#endif

    handler->handlePlayer2ConnectedQuery(this, params);
}
void Player1NameQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1NameQuery::accept called on:" << this << endl;
#endif

    handler->handlePlayer1NameQuery(this, params);
}
void Player2NameQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2NameQuery::accept called on:" << this << endl;
#endif

    handler->handlePlayer2NameQuery(this, params);
}
void Player1TimeLimitQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1TimeLimitQuery::accept called on:" << this << endl;
#endif

    handler->handlePlayer1TimeLimitQuery(this, params);
}
void Player2TimeLimitQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2TimeLimitQuery::accept called on:" << this << endl;
#endif

    handler->handlePlayer2TimeLimitQuery(this, params);
}
void MaxSpectatorsQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: MaxSpectatorsQuery::accept called on:" << this << endl;
#endif

    handler->handleMaxSpectatorsQuery(this, params);
}
void GameTypeQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: GameTypeQuery::accept called on:" << this << endl;
#endif

    handler->handleGameTypeQuery(this, params);
}
void RuleSetQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: RuleSetQuery::accept called on:" << this << endl;
#endif

    handler->handleRuleSetQuery(this, params);
}
void GameBoardQuery::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: GameBoardQuery::accept called on:" << this << endl;
#endif

    handler->handleGameBoardQuery(this, params);
}
void Player1ConnectedResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1ConnectedResponse::accept called on:" << this << endl;
#endif

    handler->handlePlayer1ConnectedResponse(this, params);
}
void Player2ConnectedResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2ConnectedResponse::accept called on:" << this << endl;
#endif

    handler->handlePlayer2ConnectedResponse(this, params);
}
void Player1NameResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1NameResponse::accept called on:" << this << endl;
#endif

    handler->handlePlayer1NameResponse(this, params);
}
void Player2NameResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2NameResponse::accept called on:" << this << endl;
#endif

    handler->handlePlayer2NameResponse(this, params);
}
void Player1TimeLimitResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player1TimeLimitResponse::accept called on:" << this << endl;
#endif

    handler->handlePlayer1TimeLimitResponse(this, params);
}
void Player2TimeLimitResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: Player2TimeLimitResponse::accept called on:" << this << endl;
#endif

    handler->handlePlayer2TimeLimitResponse(this, params);
}
void MaxSpectatorsResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: MaxSpectatorsResponse::accept called on:" << this << endl;
#endif

    handler->handleMaxSpectatorsResponse(this, params);
}
void GameTypeResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: GameTypeResponse::accept called on:" << this << endl;
#endif

    handler->handleGameTypeResponse(this, params);
}
void RuleSetResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: RuleSetResponse::accept called on:" << this << endl;
#endif

    handler->handleRuleSetResponse(this, params);
}
void GameBoardResponse::accept(MessageHandler* handler, void* params)
{
#ifdef MANCALA_MESSAGE_DEBUG
    cout << "Message Debug: GameBoardResponse::accept called on:" << this << endl;
#endif

    handler->handleGameBoardResponse(this, params);
}
