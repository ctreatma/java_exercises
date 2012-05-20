#include "greedy_player.cc"
#include "random_fun.h"
#include <vector>

using namespace std;

int RandomPlayer::getMove(GameBoard& gameBoard) {
  int first = (gameBoard.getCurrentPlayer()==1 ? 0 : gameBoard.getBinsPerSide());
  int last = first + gameBoard.getBinsPerSide() - 2;

  vector<int> vec;
  vec.reserve(gameBoard.getBinsPerSide()-1);

  for (int i=last; i >= first; i--)
    if (!gameBoard.isInvalidMove(i))
      vec.push_back(i);

  return vec[equilikely(0, vec.size()-1)];
}
