#include "game_board.h"
#include "exceptions.h"
#include <iostream>
#include <sstream>
#include <ctype.h>
#include "io.h"
#include "messages.h"
#include "server.h"

using namespace std;

AIO::AIO() 
{
    message_count = 0;
    connected = false;
    gameStarted = false;
    ruleSet = NULL;
    gameBoard = NULL;
    player1Name = "waiting for connection";
    player2Name = "waiting for connection";
    player = NULL;
}
AIO::~AIO() {}

void AIO::showGameBoard() {} // stub
void AIO::showHelp() {} // stub

void AIO::enqueueMessage(Message* msg)
{
    out_buffer.push(msg);
}
void AIO::resetDepth(GameBoard& gb)
{
    gb.resetDepth();
}
GameBoard* AIO::createGameBoard(RuleSet* ruleSet, string data)
{
    return new GameBoard(ruleSet, data);
}

APlayer* AIO::getPlayer()
{
    return player;
}

void AIO::setPlayer(APlayer* player)
{
    this->player = player;
}

Message* AIO::getMessage()
{
    if (! out_buffer.empty())
    {
	Message* msg = out_buffer.front();
	out_buffer.pop();
	return msg;
    }
    else
	return NULL;
}
void AIO::putMessage(Message* msg)
{
    msg->accept(this, NULL);
    delete msg;
}

void AIO::resetTimer()
{
    GameBoard::ResetTimer();
}

int AIO::getTimerValue()
{
    return GameBoard::GetTimerValue();
}


void AIO::handleGameBoardQuery(GameBoardQuery* msg, void* params)
{
    gameBoardQueryPending = true;
}

void AIO::handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") AIO::ConnectPlayer1Message handler: " << *msg << endl;
#endif

    // this should be overridden by Player1TextIO
    player1Name = msg->getData();
}
void AIO::handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") AIO::ConnectPlayer2Message handler: " << *msg << endl;
#endif

    // this should be overridden by Player2TextIO
    player2Name = msg->getData();
}

void AIO::handlePlayer1NameResponse(Player1NameResponse* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") AIO::Player1NameResponse handler: " << *msg << endl;
#endif

    player1Name = msg->getData();
}

void AIO::handlePlayer2NameResponse(Player2NameResponse* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") AIO::Player2NameResponse handler: " << *msg << endl;
#endif

    player2Name = msg->getData();
}

void AIO::handleRuleSetResponse(RuleSetResponse* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") AIO::RuleSetResponse handler: " << *msg << endl;
#endif

    if (gameBoard == NULL)
    {
	ruleSet = RuleSet::createRuleSet(msg->getData());
	if (ruleSet != NULL)
	{
	    gameBoard = createGameBoard(ruleSet);
	    TextMessage* tm1 = 
		new TextMessage("*server*: welcome to mancala");
	    string gameType;
	    string serializedRS = ruleSet->serialize();
	    if (serializedRS[0] == 'E')
		gameType = "Egyptian Rules";
	    else if (serializedRS[0] == 'W')
		gameType = "Wari Rules";
	    else
		gameType = "unknown";
	    string tm2str = "*server*: game type is ";
	    tm2str = tm2str + gameType;
	    TextMessage* tm2 = 
		new TextMessage(tm2str);
	    TextMessage* tm3 = 
		new TextMessage("*server*: ******* RULES *******");
	    TextMessage* tm4 = 
		new TextMessage(ruleSet->getRules());
	    
	    putMessage(tm1);
	    putMessage(tm2);
	    putMessage(tm3);
	    putMessage(tm4);
	}
    }
}


void TextIO::showGameBoard()
{
    if (gameBoard != NULL)
    {
	int bins = gameBoard->getBinsPerSide();
	ostringstream oss;
	oss << endl;
	if (bins > 8)
	{
	    // use crappy display
	    oss << "   ";
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		num <<
		    gameBoard->getPiecesInBin(2*(bins-1) - i);
		while (num.str().size() < 3)
		    num << " ";
		oss << num.str();
		if (i < bins-2)
		    oss << "  ";
	    }
	    oss << endl;

	    ostringstream num;
	    num <<
		gameBoard->getPiecesInBin(gameBoard->getKalaha2());
	    while (num.str().size() < 3)
		num << " ";
	    oss << num.str();

	    string s(3*2*(bins-2)+1, ' ');
	    oss << s;

	    ostringstream num2;
	    num2 <<
		gameBoard->getPiecesInBin(gameBoard->getKalaha1());
	    while (num2.str().size() < 3)
		num2 << " ";
	    oss << num2.str();
	    oss << endl;

	    oss << "   ";
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		num <<
		    gameBoard->getPiecesInBin(i);
		while (num.str().size() < 3)
		    num << " ";
		oss << num.str();
		if (i < bins-2)
		    oss << "  ";
	    }
	    oss << endl;
	    cout << oss.str();
	}
	else // draw less crappy boxes
	{
	    // draw player 2's bins first
	    string spacer(5, ' ');
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		num << " " << 2*(bins-1)-i;
		while (num.str().size() < 5)
		    num << " ";
		oss << num.str();
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		oss << " ___ ";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		oss << "|   |";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		oss << "|";
		ostringstream num;
		num <<
		    gameBoard->getPiecesInBin(2*(bins-1) - i);
		string left_space(3 - num.str().size(), ' ');
		oss << left_space;
		oss << num.str();
		oss << "|";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		oss << "|___|";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;


	    // draw the kalahas
	    string k_spacer(((bins-1) * 2 +1) * 5, ' ');
	    int k_1 = gameBoard->getPiecesInBin(gameBoard->getKalaha1());
	    int k_2 = gameBoard->getPiecesInBin(gameBoard->getKalaha2());
	    ostringstream k_1ss, k_2ss;
	    k_1ss << k_1; k_2ss << k_2;
	    while (k_1ss.str().size() < 3)
		k_1ss << " ";
	    while (k_2ss.str().size() < 3)
		k_2ss << " ";
	    oss << " ___ " << k_spacer << " ___ " << endl;
	    oss << "|   |" << k_spacer << "|   |" << endl;
	    oss << "|"<<k_2ss.str()<<"|"<<k_spacer<<"|"
		<<k_1ss.str()<<"|"<<endl;
	    oss << "|___|" << k_spacer << "|___|" << endl;

	    // draw player 1's bins
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		num << " " << i;
		while (num.str().size() < 5)
		    num << " ";
		oss << num.str();
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		oss << " ___ ";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		oss << "|   |";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		oss << "|";
		ostringstream num;
		num <<
		    gameBoard->getPiecesInBin(i);
		string left_space(3 - num.str().size(), ' ');
		oss << left_space;
		oss << num.str();
		oss << "|";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    oss << spacer << spacer;
	    for (int i = 0; i < bins-1; i++)
	    {
		ostringstream num;
		oss << "|___|";
		if (i < bins-2)
		    oss << spacer;
	    }
	    oss << endl;
	    cout << oss.str();
	}
    }
}


TextIO::TextIO()
    : AIO()
{
    player = new TextPlayer();
    enqueueMessage(new RuleSetQuery());
}

void TextIO::showHelp()
{
    cout << endl << ruleSet->getRules() << endl;
}

TextIO::~TextIO() 
{
    if (ruleSet)
	delete ruleSet;
    if (gameBoard)
	delete gameBoard;
}


void TextIO::handleMessage(Message* message, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO default Message handler: " << *message << endl;
#endif
}

void TextIO::handleTextMessage(TextMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::TextMessage handler: " << *msg << endl;
#endif
    cout << msg->getData() << endl;
}
void TextIO::handleDisconnectMessage(DisconnectMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::DisconnectMessage handler: " << *msg << endl;
#endif

    if (msg->getData() == "-1")
    {
	//cout << "server shutting down..."<<endl;
	//exit(0);
    }
    else if (msg->getData() == "1")
    {
	cout << "player 1 disconnected"<<endl;
    }
    else if (msg->getData() == "2")
    {
	cout << "player 2 disconnected"<<endl;
    }
}

// these MoveMessage functions should be overriden --
// the basic rule is: if you made the move, you should begin the next request
void TextIO::handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::Player1MoveMessage handler: " << *msg << endl;
#endif

    // this is overriden in PlayerQTextIO::handlePlayerQMoveMessage
    gameBoardQueryPending = false;

    if (gameBoard == NULL)
    {
	cout << "no game board initialized" << endl;
	return;
    }

    if (gameBoard->getCurrentPlayer() == 1)
    {
	istringstream iss(msg->getData());
	int move = -1;
	iss >> move;
	cout << player1Name<<": "<<move<<endl;
	if (move != -1 && gameBoard != NULL)
	{
	    try
	    {
		resetDepth(*gameBoard);
		gameBoard->makeMove(move);
	    }
	    catch (InvalidMoveException& e1)
	    {
		// this shouldn't happen
		cerr << player1Name << " fudged up. aborting..."<<endl;
		cout << player2Name << " wins!"<<endl;
		
		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage("2"));
		//exit(1);
	    }
	    catch (GameOverException& e1)
	    {
		ostringstream oss;
		oss << e1.getWinner();
		if (e1.getWinner() == 1)
		{
		    enqueueMessage(new Player1WinnerMessage());
		}
		else if (e1.getWinner() == 2)
		{
		    enqueueMessage(new Player2WinnerMessage());
		}
		else
		{
		    enqueueMessage(new TieMessage());
		}
		enqueueMessage(new GameOverMessage(oss.str()));
	    }
	    showGameBoard();
	}
    }
}
void TextIO::handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::Player2MoveMessage handler: " << *msg << endl;
#endif

    // this is overriden in PlayerQTextIO::handlePlayerQMoveMessage
    gameBoardQueryPending = false;

    if (gameBoard == NULL)
	return;

    if (gameBoard->getCurrentPlayer() == 2)
    {
	istringstream iss(msg->getData());
	int move = -1;
	iss >> move;
	cout << player2Name<<": "<<move<<endl;
	if (move != -1 && gameBoard != NULL)
	{
	    try
	    {
		resetDepth(*gameBoard);
		gameBoard->makeMove(move);
	    }
	    catch (InvalidMoveException& e1)
	    {
		// this shouldn't happen
		cerr << player2Name << " fudged up. aborting..."<<endl;
		cout << player1Name << " wins!"<<endl;

		enqueueMessage(new Player1WinnerMessage());
		enqueueMessage(new GameOverMessage("1"));
		//exit(1);
	    }
	    catch (GameOverException& e1)
	    {
		ostringstream oss;
		oss << e1.getWinner();
		if (e1.getWinner() == 1)
		{
		    enqueueMessage(new Player1WinnerMessage());
		}
		else if (e1.getWinner() == 2)
		{
		    enqueueMessage(new Player2WinnerMessage());
		}
		else
		{
		    enqueueMessage(new TieMessage());
		}
		enqueueMessage(new GameOverMessage(oss.str()));
	    }
	    showGameBoard();
	}
    }
}
void TextIO::handleGameStartMessage(GameStartMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::GameStartMessage handler: " << *msg << endl;
#endif

    // do stuff here that you didn't want to do
    // before everyone was connected

    gameStarted = true;
    if (gameBoard == NULL) // this shouldn't happen, but who knows
    {
	enqueueMessage(new RuleSetQuery());
    }
    else
	showGameBoard();
    enqueueMessage(new Player1NameQuery());
    enqueueMessage(new Player2NameQuery());
}
void TextIO::handleGameOverMessage(GameOverMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::GameOverMessage handler: " << *msg << endl;
#endif

    cout << "Game Over"<<endl;
    showGameBoard();
    gameStarted = false;
}
void TextIO::handlePlayer1WinnerMessage(Player1WinnerMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::Player1WinnerMessage handler: " << *msg << endl;
#endif

    cout << "Winner: "<<player1Name<<endl;
}
void TextIO::handlePlayer2WinnerMessage(Player2WinnerMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::Player2WinnerMessage handler: " << *msg << endl;
#endif

    cout << "Winner: "<<player2Name<<endl;
}
void TextIO::handleTieMessage(TieMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") TextIO::TieMessage handler: " << *msg << endl;
#endif

    cout << "Tie Game"<<endl;
}



Player1TextIO::Player1TextIO(string name)
    : TextIO()
{
    this->player1Name = name;
    enqueueMessage(new ConnectPlayer1Message(name));
}
Player1TextIO::~Player1TextIO() {}
void Player1TextIO::handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player1TextIO::ConnectPlayer1Message handler: " << *msg << endl;
#endif

    if (msg->getData() == player1Name)
    {
	connected = true;
    }
}
void Player1TextIO::handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player1TextIO::InvalidConnectionMessage handler: " << *msg << endl;
#endif

    cerr << "invalid connection, please try again"<<endl;
}
void Player1TextIO::handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player1TextIO::Player1MoveRequestMessage handler: " << *msg << endl;
#endif

    if (!gameStarted || !gameBoard || gameBoard->getCurrentPlayer() != 1)
	return;

    cout << player1Name << ": ";
    GameBoard gameBoardCopy = gameBoard->clone();

    // start the timer
    resetTimer();

    string move_str = player->getMoveString(gameBoardCopy);

    if (move_str == "?" || move_str == "" ||
	!isdigit(move_str[0]))
    {
	showHelp();
	showGameBoard();
	handlePlayer1MoveRequestMessage(msg, params);
	return;
    }
    
    istringstream iss(move_str);
    int move;
    iss >> move;

    // test if it's a valid move
    int isInvalid = gameBoard->isInvalidMove(move);
    if (isInvalid)
    {
	cout << "invalid move. ";
	if (isInvalid != -1)
	    cout << "see rule #"<<isInvalid;
	cout << endl;
	handlePlayer1MoveRequestMessage(msg, params);
	return;
    }

    enqueueMessage(new Player1MoveMessage(move_str));
}
void Player1TextIO::handlePlayer1MoveMessage(Player1MoveMessage* msg, 
					     void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player1TextIO::Player1MoveMessage handler: " << *msg << endl;
#endif

    if (gameBoard == NULL)
    {
	cout << "no game board initialized" << endl;
	return;
    }

    if (gameBoard->getCurrentPlayer() == 1)
    {
	istringstream iss(msg->getData());
	int move = -1;
	iss >> move;
	cout << player1Name<<": "<<move<<endl;
	if (move != -1 && gameBoard != NULL)
	{
	    try
	    {
		resetDepth(*gameBoard);
		gameBoard->makeMove(move);
	    }
	    catch (InvalidMoveException& e1)
	    {
		// this shouldn't happen
		cerr << player1Name << " fudged up. aborting..."<<endl;
		cout << player2Name << " wins!"<<endl;
		
		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage("2"));
		//exit(1);
	    }
	    catch (GameOverException& e1)
	    {
		ostringstream oss;
		oss << e1.getWinner();
		if (e1.getWinner() == 1)
		{
		    enqueueMessage(new Player1WinnerMessage());
		}
		else if (e1.getWinner() == 2)
		{
		    enqueueMessage(new Player2WinnerMessage());
		}
		else
		{
		    enqueueMessage(new TieMessage());
		}
		enqueueMessage(new GameOverMessage(oss.str()));
	    }
	    int time = getTimerValue();
	    if (time > ruleSet->getTimeLimit1())
	    {
		cout << "Sorry, you ran over your time limit" 
		     << endl;
		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage());
	    }
	    else
	    {
		showGameBoard();
		if (gameBoardQueryPending)
		{
		    enqueueMessage(new GameBoardResponse(
				       gameBoard->serialize()));
		    gameBoardQueryPending = false;
		}
	    }
	}
	if (gameBoard->getCurrentPlayer() == 1)
	    enqueueMessage(new Player1MoveRequestMessage());
	else
	    enqueueMessage(new Player2MoveRequestMessage());
    }
}

Player2TextIO::Player2TextIO(string name)
    : TextIO()
{
    this->player2Name = name;
    enqueueMessage(new ConnectPlayer2Message(name));
}
Player2TextIO::~Player2TextIO() {}
void Player2TextIO::handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player2TextIO::ConnectPlayer2Message handler: " << *msg << endl;
#endif

    if (msg->getData() == player2Name)
    {
	connected = true;
    }
}
void Player2TextIO::handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player2TextIO::InvalidConnectionMessage handler: " << *msg << endl;
#endif

    cerr << "invalid connection, please try again"<<endl;
}
void Player2TextIO::handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player2TextIO::Player2MoveRequestMessage handler: " << *msg << endl;
#endif

    if (!gameStarted || !gameBoard || gameBoard->getCurrentPlayer() != 2)
	return;

    cout << player2Name << ": ";
    GameBoard gameBoardCopy = gameBoard->clone();


    // start the timer
    resetTimer();

    string move_str = player->getMoveString(gameBoardCopy);

    if (move_str == "?" || move_str == "" ||
	!isdigit(move_str[0]))
    {
	showHelp();
	showGameBoard();
	handlePlayer2MoveRequestMessage(msg, params);
	return;
    }
    
    istringstream iss(move_str);
    int move;
    iss >> move;

    // test if it's a valid move
    int isInvalid = gameBoard->isInvalidMove(move);
    if (isInvalid)
    {
	cout << "invalid move. ";
	if (isInvalid != -1)
	    cout << "see rule #"<<isInvalid;
	cout << endl;
	handlePlayer2MoveRequestMessage(msg, params);
	return;
    }

    enqueueMessage(new Player2MoveMessage(move_str));
}
void Player2TextIO::handlePlayer2MoveMessage(Player2MoveMessage* msg, 
					     void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player2TextIO::Player2MoveMessage handler: " << *msg << endl;
#endif

    if (gameBoard == NULL)
    {
	cout << "no game board initialized" << endl;
	return;
    }

    if (gameBoard->getCurrentPlayer() == 2)
    {
	istringstream iss(msg->getData());
	int move = -1;
	iss >> move;
	cout << player2Name<<": "<<move<<endl;
	if (move != -1 && gameBoard != NULL)
	{
	    try
	    {
		resetDepth(*gameBoard);
		gameBoard->makeMove(move);
	    }
	    catch (InvalidMoveException& e1)
	    {
		// this shouldn't happen
		cerr << player2Name << " fudged up. aborting..."<<endl;
		cout << player1Name << " wins!"<<endl;

		enqueueMessage(new Player1WinnerMessage());
		enqueueMessage(new GameOverMessage("1"));
		//exit(1);
	    }
	    catch (GameOverException& e1)
	    {
		ostringstream oss;
		oss << e1.getWinner();
		if (e1.getWinner() == 1)
		{
		    enqueueMessage(new Player1WinnerMessage());
		}
		else if (e1.getWinner() == 2)
		{
		    enqueueMessage(new Player2WinnerMessage());
		}
		else
		{
		    enqueueMessage(new TieMessage());
		}
		enqueueMessage(new GameOverMessage(oss.str()));
	    }
	    int time = getTimerValue();
	    if (time > ruleSet->getTimeLimit2())
	    {
		cout << "Sorry, you ran over your time limit" << endl;
		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage());
	    }
	    else
	    {
		showGameBoard();
		if (gameBoardQueryPending)
		{
		    enqueueMessage(new GameBoardResponse(
				       gameBoard->serialize()));
		    gameBoardQueryPending = false;
		}
	    }
	}
	if (gameBoard->getCurrentPlayer() == 1)
	    enqueueMessage(new Player1MoveRequestMessage());
	else
	    enqueueMessage(new Player2MoveRequestMessage());
    }
}

Player12TextIO::Player12TextIO(string player1Name, string player2Name)
    : TextIO()
{
    this->player1Name = player1Name;
    this->player2Name = player2Name;
    this->player2 = new TextPlayer();
    enqueueMessage(new ConnectPlayer1Message(player1Name));
    enqueueMessage(new ConnectPlayer2Message(player2Name));
}
Player12TextIO::~Player12TextIO() {}
APlayer* Player12TextIO::getPlayer1() { return AIO::getPlayer(); }
void Player12TextIO::setPlayer1(APlayer* player1) 
{ 
    AIO::setPlayer(player1);
}
APlayer* Player12TextIO::getPlayer2() { return player2; }
void Player12TextIO::setPlayer2(APlayer* player2) 
{ 
    this->player2 = player2; 
}
void Player12TextIO::
handlePlayer1MoveRequestMessage(Player1MoveRequestMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player12TextIO::Player1MoveRequestMessage handler: " << *msg << endl;
#endif

    if (!gameStarted || gameBoard == NULL ||
	gameBoard->getCurrentPlayer() != 1)
	return;

    cout << player1Name << ": ";
    GameBoard gameBoardCopy = gameBoard->clone();

    // start the timer
    resetTimer();

    string move_str = player->getMoveString(gameBoardCopy);

    if (move_str == "?" || move_str == "" ||
	!isdigit(move_str[0]))
    {
	showHelp();
	showGameBoard();
	handlePlayer1MoveRequestMessage(msg, params);
	return;
    }
    
    istringstream iss(move_str);
    int move;
    iss >> move;

    // test if it's a valid move
    int isInvalid = gameBoard->isInvalidMove(move);
    if (isInvalid)
    {
	cout << "invalid move. ";
	if (isInvalid != -1)
	    cout << "see rule #"<<isInvalid;
	cout << endl;
	handlePlayer1MoveRequestMessage(msg, params);
	return;
    }

    enqueueMessage(new Player1MoveMessage(move_str));
}
void Player12TextIO::
handlePlayer1MoveMessage(Player1MoveMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player12TextIO::Player1MoveMessage handler: " << *msg << endl;
#endif

    if (gameBoard == NULL)
    {
	cout << "no game board initialized" << endl;
	return;
    }

    if (gameBoard->getCurrentPlayer() == 1)
    {
	istringstream iss(msg->getData());
	int move = -1;
	iss >> move;
	cout << player1Name<<": "<<move<<endl;
	if (move != -1 && gameBoard != NULL)
	{
	    try
	    {
		resetDepth(*gameBoard);
		gameBoard->makeMove(move);
	    }
	    catch (InvalidMoveException& e1)
	    {
		// this shouldn't happen
		cerr << player1Name << " fudged up. aborting..."<<endl;
		cout << player2Name << " wins!"<<endl;

		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage("2"));
		//exit(1);
	    }
	    catch (GameOverException& e1)
	    {
		ostringstream oss;
		oss << e1.getWinner();
		if (e1.getWinner() == 1)
		{
		    enqueueMessage(new Player1WinnerMessage());
		}
		else if (e1.getWinner() == 2)
		{
		    enqueueMessage(new Player2WinnerMessage());
		}
		else
		{
		    enqueueMessage(new TieMessage());
		}
		enqueueMessage(new GameOverMessage(oss.str()));
	    }
	    int time = getTimerValue();
	    if (time > ruleSet->getTimeLimit1())
	    {
		cout << "Sorry, you ran over your time limit" << endl;
		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage());
	    }
	    else
		showGameBoard();
	}
	if (gameBoard->getCurrentPlayer() == 1)
	    enqueueMessage(new Player1MoveRequestMessage());
	else
	    enqueueMessage(new Player2MoveRequestMessage());
    }
}
void Player12TextIO::
handlePlayer2MoveRequestMessage(Player2MoveRequestMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player12TextIO::Player2MoveRequestMessage handler: " << *msg << endl;
#endif

    if (!gameStarted || gameBoard == NULL ||
	gameBoard->getCurrentPlayer() != 2)
	return;

    cout << player2Name << ": ";
    GameBoard gameBoardCopy = gameBoard->clone();

    // start the timer
    resetTimer();

    string move_str = player2->getMoveString(gameBoardCopy);

    if (move_str == "?" || move_str == "" ||
	!isdigit(move_str[0]))
    {
	showHelp();
	showGameBoard();
	handlePlayer2MoveRequestMessage(msg, params);
	return;
    }
    
    istringstream iss(move_str);
    int move;
    iss >> move;

    // test if it's a valid move
    int isInvalid = gameBoard->isInvalidMove(move);
    if (isInvalid)
    {
	cout << "invalid move. ";
	if (isInvalid != -1)
	    cout << "see rule #"<<isInvalid;
	cout << endl;
	handlePlayer2MoveRequestMessage(msg, params);
	return;
    }

    enqueueMessage(new Player2MoveMessage(move_str));
}
void Player12TextIO::
handlePlayer2MoveMessage(Player2MoveMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") Player12TextIO::Player2MoveMessage handler: " << *msg << endl;
#endif

    if (gameBoard == NULL)
    {
	cout << "no game board initialized" << endl;
	return;
    }

    if (gameBoard->getCurrentPlayer() == 2)
    {
	istringstream iss(msg->getData());
	int move = -1;
	iss >> move;
	cout << player2Name<<": "<<move<<endl;
	if (move != -1 && gameBoard != NULL)
	{
	    try
	    {
		resetDepth(*gameBoard);
		gameBoard->makeMove(move);
	    }
	    catch (InvalidMoveException& e1)
	    {
		// this shouldn't happen
		cerr << player2Name << " fudged up. aborting..."<<endl;
		cout << player1Name << " wins!"<<endl;

		enqueueMessage(new Player1WinnerMessage());
		enqueueMessage(new GameOverMessage("1"));
		//exit(1);
	    }
	    catch (GameOverException& e1)
	    {
		ostringstream oss;
		oss << e1.getWinner();
		if (e1.getWinner() == 1)
		{
		    enqueueMessage(new Player1WinnerMessage());
		}
		else if (e1.getWinner() == 2)
		{
		    enqueueMessage(new Player2WinnerMessage());
		}
		else
		{
		    enqueueMessage(new TieMessage());
		}
		enqueueMessage(new GameOverMessage(oss.str()));
	    }
	    int time = getTimerValue();
	    if (time > ruleSet->getTimeLimit2())
	    {
		cout << "Sorry, you ran over your time limit" << endl;
		enqueueMessage(new Player2WinnerMessage());
		enqueueMessage(new GameOverMessage());
	    }
	    else
		showGameBoard();
	}
	if (gameBoard->getCurrentPlayer() == 1)
	    enqueueMessage(new Player1MoveRequestMessage());
	else
	    enqueueMessage(new Player2MoveRequestMessage());
    }
}



SpectatorTextIO::SpectatorTextIO(string name)
    : TextIO()
{
    this->name= name;
    enqueueMessage(new ConnectSpectatorMessage(name));
}
SpectatorTextIO::~SpectatorTextIO() {}
void SpectatorTextIO::handleConnectSpectatorMessage(ConnectSpectatorMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") SpectatorTextIO::ConnectSpectatorMessage handler: " << *msg << endl;
#endif

    if (msg->getData() == name)
    {
	connected = true;
	haveGameBoard = false;
	// I've already send a RuleSetQuery, so this should be okay
	enqueueMessage(new GameBoardQuery());
	enqueueMessage(new Player1NameQuery());
	enqueueMessage(new Player2NameQuery());
    }
}
void SpectatorTextIO::handleInvalidConnectionMessage(InvalidConnectionMessage* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") SpectatorTextIO::InvalidConnectionMessage handler: " << *msg << endl;
#endif

    cerr << "sorry, already at maximum spectators"<<endl;
}

void SpectatorTextIO::handleGameBoardQuery(GameBoardQuery* gb, void* params)
{
    // oh yeah..blank method...
}

void SpectatorTextIO::handleGameBoardResponse(GameBoardResponse* msg, void* params)
{
#ifdef MANCALA_IO_DEBUG
    cout << "IO Debug: (this="<<this<<") SpectatorTextIO::GameBoardResponse handler: " << *msg << endl;
#endif
    if (! haveGameBoard)
    {
	if (gameBoard)
	    delete gameBoard;
	gameBoard = createGameBoard(ruleSet, msg->getData());
	showGameBoard();
	haveGameBoard = true;
    }
}

void SpectatorTextIO::
handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
    enqueueMessage(new Player1NameQuery());
    haveGameBoard = false;
    enqueueMessage(new GameBoardQuery());
}

void SpectatorTextIO::
handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
    enqueueMessage(new Player2NameQuery());
    haveGameBoard = false;
    enqueueMessage(new GameBoardQuery());
}

ServerNetIO::ServerNetIO(unsigned int port)
{
    IPaddress ip;
    TCPsocket tcpsock;
    thread = NULL;
    isRunning = false;
    data = (char*) malloc(MessageFactory::getMaxLength());

#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: resolving server ip" << endl;
#endif
    int errorCode = 0;
    if(SDLNet_ResolveHost(&ip,NULL,port)==-1)
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: error resolving server ip" << endl;
	cout << "Net Debug: SDLNet_ResolveHost: " << SDLNet_GetError() << endl;
#endif

	cerr << "could not resolve server ip. networking will be disabled" 
	     << endl;
	errorCode = 1;
    }
    
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: opening server socket" << endl;
#endif
    sock=SDLNet_TCP_Open(&ip);
    if(!sock) 
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: error opening server ip" << endl;
	cout << "Net Debug: SDLNet_TCP_Open: "<< SDLNet_GetError() << endl;
#endif
	cerr << "error starting server. networking will be disabled" << endl;
	errorCode = 2;
    }

    maxSockets = 16; // sounds good, no?
    socketSet = SDLNet_AllocSocketSet(maxSockets);

    if (!errorCode)
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: server successfully created" << endl;
#endif
    }
    
}

ServerNetIO::~ServerNetIO()
{
    if (data)
	free(data);
    SDLNet_FreeSocketSet(socketSet);
}

int ServerNetIO::CreateThread(void* serverNetIO)
{
    if (serverNetIO == NULL)
    {
	return -1;
    }
    else
	return static_cast<ServerNetIO*>(serverNetIO)
	    ->thread_func();
}

int ServerNetIO::thread_func()
{
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: ServerNetIO::thread_func() called" << endl;
#endif
    if (!sock) // networking wasn't enabled
    {
	return -1;
    }
    while(true)
    {
	// first see if we have any incoming connections
	TCPsocket newsock;
	newsock=SDLNet_TCP_Accept(sock);
	if(newsock)
	{
	    SDLNet_TCP_AddSocket(socketSet, newsock);
	    sockets.push_back(newsock);
#ifdef MANCALA_NET_DEBUG
	    IPaddress *ipAddress = SDLNet_TCP_GetPeerAddress(newsock);
	    cout << "Net Debug: socket to "
		 << SDLNet_ResolveIP(ipAddress) << ":"
		 << ipAddress->port << " created"<<endl;
#endif
	}
	// now poll all of the current connections for data
	if (sockets.size() > 0 &&
	    SDLNet_CheckSockets(socketSet, 0)) // don't wait
	{
	    for (unsigned int i = 0; i < sockets.size(); i++)
	    {
		if (SDLNet_SocketReady(sockets[i]))
		{
#ifdef MANCALA_NET_DEBUG
		    IPaddress *ipAddress 
			= SDLNet_TCP_GetPeerAddress(sockets[i]);
		    cout << "Net Debug: polling " 
			 << SDLNet_ResolveIP(ipAddress) << ":"
			 << ipAddress->port << " for data"<<endl;
#endif
		    
		    if (!getNetMsg(sockets[i], &data))
		    {
#ifdef MANCALA_NET_DEBUG
			cout << "Net Debug: no data or network error" << endl;
#endif
		    }
		    else
		    {
#ifdef MANCALA_NET_DEBUG
			cout << "Net Debug: received: " << data << endl;
#endif
			string msg_str(data);
			enqueueMessage(MessageFactory::createMessage(msg_str));
		    }
		}
	    }
	}
    }
}

void ServerNetIO::start()
{
    if (!isRunning)
    {
	isRunning = true;
	thread = SDL_CreateThread(ServerNetIO::CreateThread, this);
    }
}

void ServerNetIO::stop()
{
    if (isRunning)
    {
	shutdown_requested = true;
	int status;
	SDL_WaitThread(thread, &status);
	isRunning = false;
    }
}

void ServerNetIO::kill()
{
    if (isRunning)
    {
	SDL_KillThread(thread);
	thread = NULL;
	isRunning = false;
	shutdown_requested = false;
    }
}

void ServerNetIO::
handleMessage(Message* msg, void* params)
{
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: ServerNetIO::handleMessage() called on "<<*msg<<endl;
#endif
    for (unsigned int i = 0; i < sockets.size(); i++)
    {
	TCPsocket netsock = sockets[i];
#ifdef MANCALA_NET_DEBUG
	IPaddress *ipAddress = SDLNet_TCP_GetPeerAddress(netsock);
	cout << "Net Debug: sending message to " 
	     << SDLNet_ResolveIP(ipAddress) << ":"
	     << ipAddress->port <<endl;
#endif
	ostringstream oss;
	oss << *msg;
	if (!putNetMsg(netsock, (char*) oss.str().c_str()))
	{
#ifdef MANCALA_NET_DEBUG
	    cout << "Net Debug: error sending" << endl;
#endif
	    sockets.erase(find(sockets.begin(), sockets.end(), netsock));
	    SDLNet_TCP_DelSocket(socketSet, netsock);
	}
	else
	{
#ifdef MANCALA_NET_DEBUG
	    cout << "Net Debug: message sent properly" << endl;
#endif
	}
    }
}

void ServerNetIO::
handleDisconnectMessage(DisconnectMessage* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
    if (msg->getData() == "-1") // server going down
    {
	//exit(0); // exit normally
	shutdown_requested = true;
    }
}

void ServerNetIO::
handleGameBoardQuery(GameBoardQuery* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ServerNetIO::
handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ServerNetIO::
handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ServerNetIO::
handlePlayer1NameResponse(Player1NameResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ServerNetIO::
handlePlayer2NameResponse(Player2NameResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ServerNetIO::
handleRuleSetResponse(RuleSetResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}


ClientNetIO::ClientNetIO(std::string server, unsigned int port)
{
    IPaddress ip;
    TCPsocket tcpsock;
    thread = NULL;
    isRunning = false;
    shutdown_requested = false;
    data = (char*) malloc(MessageFactory::getMaxLength());

#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: resolving host " << server << ":"<<port<<endl;
#endif

    int errorCode = 0;
    if(SDLNet_ResolveHost(&ip,(char*)server.c_str(),port)==-1)
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: SDLNet_ResolveHost: " << SDLNet_GetError() << endl;
#endif
	cerr << "error resolving host. networking will be disabled" << endl;
	errorCode = 1;
    }
    
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: opening host " << server << ":"<<port<<endl;
#endif

    sock=SDLNet_TCP_Open(&ip);
    if(!sock) 
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: SDLNet_TCP_Open: "<< SDLNet_GetError() << endl;
#endif
	cerr << "error connecting to host. networking will be disabled" << endl;
	errorCode = 2;
    }
    else
    {
	socketSet = SDLNet_AllocSocketSet(1);
	SDLNet_TCP_AddSocket(socketSet, sock);
    }

    if (!errorCode)
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: socket succesfully created"<<endl;
#endif
    }
    
}

ClientNetIO::~ClientNetIO()
{
    if (data)
	free(data);
    SDLNet_FreeSocketSet(socketSet);
}

int ClientNetIO::CreateThread(void* clientNetIO)
{
    if (clientNetIO == NULL)
    {
	return -1;
    }
    else
	return static_cast<ClientNetIO*>(clientNetIO)
	    ->thread_func();
}

int ClientNetIO::thread_func()
{
    if (!sock) // networking wasn't enabled
	return -1;
    
    while(! shutdown_requested)
    {
	// poll all of the current io's for messages
	// to delete and then send over the network
	for (unsigned int i = 0; i < inputs_outputs.size(); i++)
	{
	    Message* msg = inputs_outputs[i]->getMessage();
	    if (msg != NULL)
	    {
#ifdef MANCALA_NET_DEBUG
		IPaddress *ipAddress = SDLNet_TCP_GetPeerAddress(sock);
		cout << "Net Debug: sending message: "
		     << *msg << "  to: " 
		     << SDLNet_ResolveIP(ipAddress) << ":"
		     << ipAddress->port << endl;
#endif
		ostringstream oss;
		oss << *msg;
		delete msg;
		
		if (!putNetMsg(sock, (char*)oss.str().c_str()))
		{
#ifdef MANCALA_NET_DEBUG
		    cout << "Net Debug: couldn't send message" << endl;
#endif
		    cerr << "error contacting server - aborting..."<<endl;
		    SDLNet_TCP_DelSocket(socketSet, sock);
		    //exit(1);
		    shutdown_requested = true;
		}
		else
		{
#ifdef MANCALA_NET_DEBUG
		    cout << "Net Debug: sent message" << endl;
#endif
		}
	    }
	}
	
	// now deliver any messages to all our connected io's
	if (SDLNet_CheckSockets(socketSet, 0))
	{
	    if (SDLNet_SocketReady(sock))
	    {
#ifdef MANCALA_NET_DEBUG
		IPaddress *ipAddress = SDLNet_TCP_GetPeerAddress(sock);
		cout << "Net Debug: polling " 
		     << SDLNet_ResolveIP(ipAddress) << ":"
		     << ipAddress->port << " for data"<<endl;
#endif
		
		if (!getNetMsg(sock, &data))
		{
#ifdef MANCALA_NET_DEBUG
		    cout << "Net Debug: no data or network error" << endl;
#endif
		}
		else
		{
#ifdef MANCALA_NET_DEBUG
		    cout << "Net Debug: received: " << data << endl;
#endif
		    string msg_str(data);
		    
		    // calls msg->accept which calls the appropriate handler
		    // and then deletes the message
		    AIO::putMessage(MessageFactory::createMessage(msg_str));
		}
	    }
	}
    } // shutdown_requested

    shutdown_requested = false;
    return 0;
}

int ClientNetIO::run()
{
    if (!isRunning)
    {
	isRunning = true;
	thread = SDL_CreateThread(ClientNetIO::CreateThread, this);
	int status;
	SDL_WaitThread(thread, &status);
	isRunning = false;
	return status;
    }
    else
	return -1;
}

void ClientNetIO::stop()
{
    shutdown_requested = true;
}

void ClientNetIO::kill()
{
    SDL_KillThread(thread);
    thread = NULL;
    isRunning = false;
    shutdown_requested = false;
}

void ClientNetIO::addIO(AIO* io)
{
    inputs_outputs.push_back(io);
}
void ClientNetIO::removeIO(AIO* io)
{
    vector<AIO*>::iterator i = 
	find(inputs_outputs.begin(), 
	     inputs_outputs.end(), io);
    if (i != inputs_outputs.end())
	inputs_outputs.erase(i);
}

void ClientNetIO::
handleMessage(Message* msg, void* params)
{
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: ClientNetIO::handleMessage called on "<<*msg<<endl;
#endif
    for (unsigned int i = 0; i < inputs_outputs.size(); i++)
    {
	inputs_outputs[i]->putMessage(msg->createNew(msg->getData()));
    }
}

void ClientNetIO::
handleDisconnectMessage(DisconnectMessage* msg, void* params)
{
    // send the message out to everyone first
    handleMessage(msg, params);
    if (msg->getData() == "-1") // server going down
    {
	//exit(0); // exit normally
	shutdown_requested = true;
    }
}

void ClientNetIO::
handleGameBoardQuery(GameBoardQuery* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ClientNetIO::
handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ClientNetIO::
handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ClientNetIO::
handlePlayer1NameResponse(Player1NameResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ClientNetIO::
handlePlayer2NameResponse(Player2NameResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void ClientNetIO::
handleRuleSetResponse(RuleSetResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}



SinglePlayerMultiServerNetIO::
SinglePlayerMultiServerNetIO(RuleSet* ruleSet, APlayer* player,
			     std::string playerName, unsigned int port,
			     int max_sockets)
{
    IPaddress ip;
    isRunning = false;
    shutdown_requested = false;
    this->player = player;
    this->serverPlayerName = playerName;
    this->ruleSet = ruleSet;
    this->maxSockets = max_sockets;

    serverPlayer = -1;

#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: resolving server ip" << endl;
#endif
    int errorCode = 0;
    if(SDLNet_ResolveHost(&ip,NULL,port)==-1)
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: error resolving server ip" << endl;
	cout << "Net Debug: SDLNet_ResolveHost: " << SDLNet_GetError() << endl;
#endif

	cerr << "could not resolve server ip. networking will be disabled" 
	     << endl;
	errorCode = 1;
    }
    
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: opening server socket" << endl;
#endif
    sock=SDLNet_TCP_Open(&ip);
    if(!sock) 
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: error opening server ip" << endl;
	cout << "Net Debug: SDLNet_TCP_Open: "<< SDLNet_GetError() << endl;
#endif
	cerr << "error starting server. networking will be disabled" << endl;
	errorCode = 2;
    }

    if (!errorCode)
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: server successfully created" << endl;
#endif
    }

    server = NULL;
    serverAIO = NULL;
    data = (char*) malloc(MessageFactory::getMaxLength());
}

SinglePlayerMultiServerNetIO::
SinglePlayerMultiServerNetIO(RuleSet* ruleSet, APlayer* player,
			     std::string playerName, 
			     TCPsocket client_sock,
			     int max_sockets)
{
    this->client_sock = client_sock;
    this->player = player;
    this->serverPlayerName = playerName;
    this->ruleSet = ruleSet;
    shutdown_requested = false;
    isRunning = false;
    this->maxSockets = max_sockets;

    server = new MancalaServer(ruleSet);
    server->addIO(this);

    serverAIO = NULL;
    serverPlayer = -1;
    data = (char*) malloc(MessageFactory::getMaxLength());
}


SinglePlayerMultiServerNetIO::~SinglePlayerMultiServerNetIO()
{
    if (player != NULL)
	delete player;
    if (ruleSet != NULL)
	delete ruleSet;
    if (serverAIO != NULL)
	delete serverAIO;
    if (server != NULL)
	delete server;
    if (data != NULL)
	delete data;

    for (unsigned int i = 0; i < extra_ios.size(); i++)
    {
	delete extra_ios[i];
    }
}

int SinglePlayerMultiServerNetIO::CreateThread(void* serverNetIO)
{
    if (serverNetIO == NULL)
    {
	return -1;
    }
    else
	return static_cast<SinglePlayerMultiServerNetIO*>(serverNetIO)
	    ->thread_func();
}

int SinglePlayerMultiServerNetIO::CreateServerThread(void* serverNetIO)
{
    if (serverNetIO == NULL)
	return -1;
    else
    {
	return
	    static_cast<SinglePlayerMultiServerNetIO*>(serverNetIO)
	    ->server->run();
    }
}

int SinglePlayerMultiServerNetIO::thread_func()
{
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: SinglePlayerMultiServerNetIO::thread_func() called" << endl;
#endif

    isRunning = true;
    while(!shutdown_requested)
    {
	// poll all of the current connections for data
	if (!getNetMsg(client_sock, &data))
	{
#ifdef MANCALA_NET_DEBUG
	    cout << "Net Debug: no data or network error" << endl;
#endif
	}
	else
	{
#ifdef MANCALA_NET_DEBUG
	    cout << "Net Debug: received: " << data << endl;
#endif
	    string msg_str(data);
	    if (serverPlayer == -1)
		putMessage(MessageFactory::createMessage(msg_str));
	    
	    // always enqueue
	    enqueueMessage(MessageFactory::createMessage(msg_str));
	}
    }
    isRunning = false;
    shutdown_requested = false;
}

int SinglePlayerMultiServerNetIO::run()
{
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: SinglePlayerMultiServerNetIO::run() called" << endl;
#endif
    if (!sock) // networking wasn't enabled
    {
	return -1;
    }

    isRunning = true;
    while(! shutdown_requested)
    {
	// see if we have any incoming connections
	// wait 0.5 seconds
	SDL_Delay(500);
	
	TCPsocket newsock=SDLNet_TCP_Accept(sock);
	if(newsock)
	{
#ifdef MANCALA_NET_DEBUG
	    IPaddress *ipAddress = SDLNet_TCP_GetPeerAddress(newsock);
	    cout << "Net Debug: socket to "
		 << SDLNet_ResolveIP(ipAddress) << ":"
		 << ipAddress->port << " created"<<endl;
#endif
	    SinglePlayerMultiServerNetIO* newIO
		= new SinglePlayerMultiServerNetIO(ruleSet, player, 
						   serverPlayerName,
						   newsock,
						   maxSockets);
	    extra_ios.push_back(newIO);
	    
	    SDL_Thread* new_thread = SDL_CreateThread(
		SinglePlayerMultiServerNetIO::CreateThread, newIO);
	}
    }
    isRunning = false;
    shutdown_requested = false;
    return 0;
}

void SinglePlayerMultiServerNetIO::stop()
{
    if (isRunning)
    {
	shutdown_requested = true;
    }
}

void SinglePlayerMultiServerNetIO::kill()
{
    stop();
}

void SinglePlayerMultiServerNetIO::
handleMessage(Message* msg, void* params)
{
#ifdef MANCALA_NET_DEBUG
    cout << "Net Debug: SinglePlayerMultiServerNetIO::handleMessage() called on "<<*msg<<endl;
#endif

#ifdef MANCALA_NET_DEBUG
    IPaddress *ipAddress = SDLNet_TCP_GetPeerAddress(client_sock);
    cout << "Net Debug: sending message to " 
	 << SDLNet_ResolveIP(ipAddress) << ":"
	 << ipAddress->port <<endl;
#endif

    ostringstream oss;
    oss << *msg;
    if (!putNetMsg(client_sock, (char*) oss.str().c_str()))
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: error sending" << endl;
#endif
	stop();
    }
    else
    {
#ifdef MANCALA_NET_DEBUG
	cout << "Net Debug: message sent properly" << endl;
#endif
    }
    
}

void SinglePlayerMultiServerNetIO::
handleDisconnectMessage(DisconnectMessage* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
    if (msg->getData() == "-1") // server going down
    {
	stop();
    }
}

void SinglePlayerMultiServerNetIO::
handleGameBoardQuery(GameBoardQuery* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void SinglePlayerMultiServerNetIO::
handleConnectPlayer1Message(ConnectPlayer1Message* msg, void* params)
{
    // create the server's player
    if (serverPlayer == -1)
    {
	serverPlayer = 2;
	serverAIO = new Player2TextIO(serverPlayerName);
	serverAIO->setPlayer(player);
	server->addIO(serverAIO);
	SDL_Thread* new_thread = SDL_CreateThread(
	    SinglePlayerMultiServerNetIO::CreateServerThread, this);
    }

    // send the message out to everyone
    handleMessage(msg, params);
}
void SinglePlayerMultiServerNetIO::
handleConnectPlayer2Message(ConnectPlayer2Message* msg, void* params)
{
    // create the server's player
    if (serverPlayer == -1)
    {
	serverPlayer = 1;
	serverAIO = new Player1TextIO(serverPlayerName);
	serverAIO->setPlayer(player);
	server->addIO(serverAIO);
	SDL_Thread* new_thread = SDL_CreateThread(
	    SinglePlayerMultiServerNetIO::CreateServerThread, this);
    }

    // send the message out to everyone
    handleMessage(msg, params);
}
void SinglePlayerMultiServerNetIO::
handlePlayer1NameResponse(Player1NameResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void SinglePlayerMultiServerNetIO::
handlePlayer2NameResponse(Player2NameResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
void SinglePlayerMultiServerNetIO::
handleRuleSetResponse(RuleSetResponse* msg, void* params)
{
    // send the message out to everyone
    handleMessage(msg, params);
}
