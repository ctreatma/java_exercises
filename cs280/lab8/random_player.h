#include "player.h"

#ifndef CS280_RANDOM_PLAYER_H
#define CS280_RANDOM_PLAYER_H

class RandomPlayer : public APlayer
{
public: 
  RandomPlayer();
  virtual ~RandomPlayer();
  virtual int getMove(GameBoard& gameBoard);
};

#endif
