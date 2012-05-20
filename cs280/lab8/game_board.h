#ifndef BEN_TAITELBAUM_GAME_BOARDS_H
#define BEN_TAITELBAUM_GAME_BOARDS_H

#include <vector>
#include "rule_set.h"

class GameBoard
{
    friend class AIO;
protected:
    int testDepth;
    int currentPlayer;
    RuleSet* ruleSet;
    std::vector<int> board;
    GameBoard(RuleSet* ruleSet, std::string data = "");
    void resetDepth();
    static int last_time;
    static void ResetTimer();
public:
    GameBoard(const GameBoard& gb);
    ~GameBoard();

    // +how many milliseconds have passed since the last time
    // the timer was reset?
    // +the timer will be reset by some subclass of AIO, but this
    // will be transparent to the user...you just need this function
    // +this (as well as ResetTimer()) assumes that SDL_Init(SDL_INIT_TIMER)
    // has been called inside the main() method of the program
    static int GetTimerValue();

    // the current level of an AI minimax search
    int getTestDepth() const;
    // the max level of an AI minimax search
    int getMaxDepth() const;

    // player 1's time limit in milliseconds
    int getTimeLimit1() const;
    // player 2's time limit in milliseconds
    int getTimeLimit2() const;
    
    // @returns: the number of bins per side, including the kalaha
    int getBinsPerSide() const;
    // @returns: how many pieces we started with in each bin
    int getInitialPiecesPerBin() const;
    // @returns: the number of pieces in Bin bin, or -1 on error
    int getPiecesInBin(int bin) const;
    // @returns: the bin # of Player 1's kalaha
    int getKalaha1() const;
    // @returns: the bin # of Player 2's kalaha
    int getKalaha2() const;
    // @returns: 1 or 2
    int getCurrentPlayer() const;
    /* 
       @returns: 0            if the move is valid for the current player
                 positive #   rule broken if this move were made
	        -1            invalid move, lazy programmer (or depth too big)
    */
    int isInvalidMove(int move) const;

    const RuleSet* getRuleSet() const;

    
    // return the number of moves since a capture
    int getStaleMoves() const;

    // return the number of pieces currently in play (not in kalahas)
    int getNumPlayingPieces() const;

    /*
      @params: move is in [0, size of the board - 1]
      @throws: InvalidMoveException, GameOverException
    */
    void makeMove(int move)
	throw(InvalidMoveException, GameOverException);

    /* 
     * returns a COPY of the gameboard
    */
    GameBoard clone() const;

    // so you don't have to type getPiecesInBin all the time
    int operator[](int bin) const;

    std::string serialize() const;
};


#endif //BEN_TAITELBAUM_GAME_BOARDS_H
