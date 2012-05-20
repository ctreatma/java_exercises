#include "game_board.h"
#include <sstream>
#include <SDL/SDL_timer.h>

GameBoard::GameBoard(const GameBoard& gb)
{
    this->testDepth = gb.testDepth; // just to make this really a copy constructor
    this->currentPlayer = gb.currentPlayer;
    this->ruleSet = gb.ruleSet->clone();
    //this->ruleSet = gb.ruleSet;
    this->board = gb.board; // this will copy it element by element
}
GameBoard::GameBoard(RuleSet* rs, std::string data)
{
    testDepth = 0;
    board = rs->createInitialBoard();
    this->currentPlayer = 1;
    this->ruleSet = rs;

    if (data != "")
    {
	std::istringstream iss(data);
	
	int player;
	if (iss >> player)
	    currentPlayer = player;
	
	int pieces;
	this->board.resize(0);
	while (iss >> pieces)
	{
	    board.push_back(pieces);
	}
    }
}
GameBoard::~GameBoard() {}
void GameBoard::resetDepth() { testDepth = 0; }

int GameBoard::last_time = 0;

void GameBoard::ResetTimer()
{
    last_time = SDL_GetTicks();
}

// this will be fine, unless the program has been running for
// 49 days, in which case there will be one incorrect (negative)
// return value
int GameBoard::GetTimerValue()
{
    return (SDL_GetTicks() - last_time);
}
int GameBoard::getTestDepth() const  { return testDepth; }
int GameBoard::getMaxDepth() const
{
    return ruleSet->getMaxDepth();
}
int GameBoard::getTimeLimit1() const
{
    return ruleSet->getTimeLimit1();
}
int GameBoard::getTimeLimit2() const
{
    return ruleSet->getTimeLimit2();
}

int GameBoard::getBinsPerSide() const
{
    return ruleSet->getBinsPerSide();
}
int GameBoard::getInitialPiecesPerBin() const
{
    return ruleSet->getInitialPiecesPerBin();
}
int GameBoard::getPiecesInBin(int bin) const
{
    if (bin < 0 || bin >= board.size())
	return -1;
    return board[bin];
}
int GameBoard::getKalaha1() const
{
    return getBinsPerSide() - 1;
}
int GameBoard::getKalaha2() const
{
    return board.size() - 1;
}
int GameBoard::getCurrentPlayer() const { return currentPlayer; }
int GameBoard::isInvalidMove(int move) const
{
    if (testDepth >= getMaxDepth())
	return -1;
    return ruleSet->isInvalidMove(board, currentPlayer, move);
}
const RuleSet* GameBoard::getRuleSet() const
{
    return ruleSet;
}

int GameBoard::getStaleMoves() const
{
    return ruleSet->getStaleMoves();
}

int GameBoard::getNumPlayingPieces() const
{
    return ruleSet->getNumPlayingPieces();
}


void GameBoard::makeMove(int move)
    throw(InvalidMoveException, GameOverException)
{
    if (testDepth >= getMaxDepth())
	throw InvalidMoveException(currentPlayer, move, -1);
    currentPlayer = ruleSet->makeMove(board, currentPlayer, move);
    testDepth++;
}
GameBoard GameBoard::clone() const
{
    GameBoard gb(*this);
    return gb;
}

int GameBoard::operator[](int bin) const
{
    return getPiecesInBin(bin);
}

std::string GameBoard::serialize() const
{
    std::ostringstream oss;
    oss << getCurrentPlayer() << " ";
    for (unsigned int i = 0; i < board.size(); i++)
    {
	oss << board[i];
	if (i < board.size() - 1)
	    oss << " ";
    }
    return oss.str();
}
