#include "player.h"

#ifndef CS280_GREEDY_PLAYER_H
#define CS280_GREEDY_PLAYER_H

class GreedyPlayer : public APlayer
{
public: 
  GreedyPlayer();
  virtual ~GreedyPlayer();
  virtual int getMove(GameBoard& gameBoard);
};

#endif
