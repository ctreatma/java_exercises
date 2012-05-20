#include "greedy_player.h"
#include "exceptions.h"
#include <vector>

using namespace std;

int GreedyPlayer::getMove(GameBoard& gameBoard) {
  int first = (gameBoard.getCurrentPlayer()==1 ? 0 : gameBoard.getBinsPerSide());
  int last = first + gameBoard.getBinsPerSide() - 2;

  for (int i=last; i >= first; i--)
    if (!gameBoard.isInvalidMove(i))
      return i;

  return 0;   // Will never be reached.
}

