#ifndef BEN_TAITELBAUM_PLAYER_H
#define BEN_TAITELBAUM_PLAYER_H

#include "game_board.h"
#include <string>

class APlayer
{
public:
    virtual ~APlayer();
    // this is because I want to allow more than just numbers as responses
    virtual std::string getMoveString(GameBoard& gameBoard);

    // for an AI, just override this method
    virtual int getMove(GameBoard& gameBoard);
};

class TextPlayer : public APlayer
{
public:
    TextPlayer();
    virtual ~TextPlayer();
    virtual std::string getMoveString(GameBoard& gameBoard);
};

// greedy player that chooses the highest number bin it can
class GreedyPlayer : public APlayer
{
public: 
    GreedyPlayer();
    virtual ~GreedyPlayer();
    virtual int getMove(GameBoard& gameBoard);
};

// greedy player that chooses the lowest number bin it can
class GreedyRPlayer : public APlayer
{
public: 
    GreedyRPlayer();
    virtual ~GreedyRPlayer();
    virtual int getMove(GameBoard& gameBoard);
};

class RandomPlayer : public APlayer
{
public: 
    RandomPlayer();
    virtual ~RandomPlayer();
    virtual int getMove(GameBoard& gameBoard);
};

#endif // !BEN_TAITELBAUM_PLAYER_H
