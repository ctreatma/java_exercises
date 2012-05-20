#include "player.h"
#include <sstream>
#include <iostream>
#include "random_fun.h"

using namespace std;

APlayer::~APlayer() {}
std::string APlayer::getMoveString(GameBoard& gameBoard)
{
    ostringstream oss;
    oss << getMove(gameBoard);
    return oss.str();
}
int APlayer::getMove(GameBoard& gameBoard)
{
    return -1;
}


TextPlayer::TextPlayer() {}
TextPlayer::~TextPlayer() {}
string TextPlayer::getMoveString(GameBoard& gameBoard)
{
    string move_str;
    cin >> move_str;
    return move_str;
}

GreedyPlayer::GreedyPlayer() {}
GreedyPlayer::~GreedyPlayer() {}
int GreedyPlayer::getMove(GameBoard& gameBoard) {
  int first = (gameBoard.getCurrentPlayer()==1 ? 0 : gameBoard.getBinsPerSide());
  int last = first + gameBoard.getBinsPerSide() - 2;

  for (int i=last; i >= first; i--)
    if (!gameBoard.isInvalidMove(i))
      return i;

  return 0;   // Will never be reached.
}

GreedyRPlayer::GreedyRPlayer() {}
GreedyRPlayer::~GreedyRPlayer() {}
int GreedyRPlayer::getMove(GameBoard& gameBoard) {
  int first = (gameBoard.getCurrentPlayer()==1 ? 0 : gameBoard.getBinsPerSide());
  int last = first + gameBoard.getBinsPerSide() - 2;

  for (int i=first; i <=last; i++)
    if (!gameBoard.isInvalidMove(i))
      return i;

  return 0;   // Will never be reached.
}

RandomPlayer::RandomPlayer() {}
RandomPlayer::~RandomPlayer() {}
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
