// player_factory.h
#include "player.h"
#include "greedy_player.h"
#include "random_player.h"


APlayer* PlayerFactory(int type) {
  switch (type) {
  case 0 : return new GreedyPlayer::GreedyPlayer;
  case 1 : return new RandomPlayer::RandomPlayer;
