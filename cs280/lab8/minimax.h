#ifndef CTREATMA_MINIMAX_H
#define CTREATMA_MINIMAX_H

#include "game_board.h"
#include "player.h"
#include <string>

class MinimaxPlayer1 : public APlayer
  {
  public:
    MinimaxPlayer1();
    virtual ~MinimaxPlayer1();
    virtual int getMove(GameBoard& gameBoard);
    virtual int alphaBeta(GameBoard& gameBoard, int bin, int alpha, int beta);
    virtual int heuristic(GameBoard& gameBoard, int bin);
  };
 
class MinimaxPlayer2 : public APlayer
  {
  public:
    MinimaxPlayer2();
    virtual ~MinimaxPlayer2();
    virtual int getMove(GameBoard& gameBoard);
    virtual int alphaBeta(GameBoard& gameBoard, int bin, int alpha, int beta);
    virtual int heuristic(GameBoard& gameBoard, int bin);
  };

#endif
