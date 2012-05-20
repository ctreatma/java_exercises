//Charles Treatman ctreatma
//Mancala AI: This A.I is designed to prefer moves that result in a capture.
//            If there are no capture moves, then a move is rated by the 
//            number of pieces in the playing bin.

#include "minimax.h"

using namespace std;

MinimaxPlayer1::MinimaxPlayer1() { }

MinimaxPlayer1::~MinimaxPlayer1() { }

int MinimaxPlayer1::getMove(GameBoard& gameBoard) {
  int bestBin = 0;
  int currentBest = 0;
  //if every move results in a loss, then we have to pick a losing move.
  //thus totalBest is set to the minimum score for a valid move
  int totalBest = -1;
  for(int i = 0; i < gameBoard.getBinsPerSide() - 1; ++i) {
    if (!gameBoard.isInvalidMove(i)) {
      currentBest = alphaBeta(gameBoard, i, totalBest, 0);
      if(currentBest >= totalBest) {
	totalBest = currentBest;
	bestBin = i;
      }
    }
  }
  
  return bestBin;
}

int MinimaxPlayer1::alphaBeta(GameBoard& gameBoard, int bin, int alpha, int beta) {
  if(gameBoard.getTestDepth() == gameBoard.getMaxDepth()) {
    return heuristic(gameBoard, bin);
  }
  
  //determine the scoring method based on whose turn it is
  //this accounts for skipping the other player with a capture
  if(gameBoard.getCurrentPlayer() == 1) {
    try {
      GameBoard g = gameBoard.clone();
      g.makeMove(bin);
      for(int i = 0; i < g.getBinsPerSide() - 1; ++i) {
	if (!g.isInvalidMove(i)) {
	  GameBoard k = g.clone();
	  int alphaPrime = alphaBeta(k, i, alpha, beta);
	  if(alphaPrime > beta) {
	    return beta;
	  }
	  alpha = (alphaPrime > alpha)? alphaPrime : alpha;
	}      
      }
      return alpha;
    }
    //if this player wins, return the score; if it's a tie, return 0
    //if the other player wins, we don't want to make the move
    catch(GameOverException e) {
      if (e.getWinner() == 1)
	return alpha;
      else if (e.getWinner() == 2)
	return -1;
      else
	return 0;
    }
  }
  else {
    try {
      GameBoard g = gameBoard.clone();
      g.makeMove(bin);
      for(int i = 0; i < g.getBinsPerSide() - 1; ++i) {
	if (!g.isInvalidMove(i)) {
	  GameBoard k = g.clone();
	  int betaPrime = alphaBeta(k, i, alpha, beta);
	  if(betaPrime < alpha) {
	    return alpha;
	  }
	beta = (betaPrime < beta)? betaPrime : beta;
	}
      }
      return beta;
    }
    catch(GameOverException e) {
      if (e.getWinner() == 1)
	return beta;
      else if (e.getWinner() == 2)
	return -1;
      else
	return 0;
    }
  }
}

int MinimaxPlayer1::heuristic(GameBoard& gameBoard, int bin) {
  GameBoard g = gameBoard.clone();
  int current_score = g[g.getKalaha1()];
  //if the move is valid, and doesn't break any rules, then make it
  //if the move results in a capture (increase in score) then return
  //the a very high rating, otherwise return the number of pieces in the bin;
  if (!g.isInvalidMove(bin)) {
    g.makeMove(bin);
    if (g[g.getKalaha1()] > current_score)
      return gameBoard[bin]*(g[g.getKalaha1()]);
    else
      return gameBoard[bin];
  }
  else
  return -2;
}

MinimaxPlayer2::MinimaxPlayer2() {}
MinimaxPlayer2::~MinimaxPlayer2() {}

int MinimaxPlayer2::getMove(GameBoard& gameBoard) {
  int bestBin = 2*gameBoard.getBinsPerSide() - 2;
  int currentBest = 0;
  int totalBest = -1;
  for(int i = bestBin; i >= gameBoard.getBinsPerSide(); --i) {
    if (gameBoard[i] > 0 && !gameBoard.isInvalidMove(i)) {
      currentBest = alphaBeta(gameBoard, i, totalBest, 0);
      if(currentBest >= totalBest) {
	totalBest = currentBest;
	bestBin = i;
      }
    }
  }
    
  return bestBin;
}

int MinimaxPlayer2::alphaBeta(GameBoard& gameBoard, int bin, int alpha, int beta) {
  if(gameBoard.getTestDepth() == gameBoard.getMaxDepth()) {
    return heuristic(gameBoard, bin);
  }
  
  if(gameBoard.getCurrentPlayer() == 2) {
    try {
      GameBoard g = gameBoard.clone();
      g.makeMove(bin);
      for(int i = g.getBinsPerSide(); i < 2*g.getBinsPerSide() - 1; ++i) {
	if (!g.isInvalidMove(i)) {
	  GameBoard k = g.clone();
	  int alphaPrime = alphaBeta(k, i, alpha, beta);
	  if(alphaPrime > beta) {
	    return beta;
	  }
	  alpha = (alphaPrime > alpha)? alphaPrime : alpha;
	}      
      }
      return alpha;
    }
    //if this player wins, return the score; if it's a tie, return 0
    //if the other player wins, we don't want to make the move
    catch(GameOverException e) {
      if (e.getWinner() == 2)
	return alpha;
      else if (e.getWinner() == 1)
	return -1;
      else
	return 0;
    }
  }
  else {
    try {
      GameBoard g = gameBoard.clone();
      g.makeMove(bin);
      for(int i = g.getBinsPerSide(); i < 2*g.getBinsPerSide() - 1; ++i) {
	if (!g.isInvalidMove(i)) {
	  GameBoard k = g.clone();
	  int betaPrime = alphaBeta(k, i, alpha, beta);
	  if(betaPrime < alpha) {
	    return alpha;
	  }
	  beta = (betaPrime < beta)? betaPrime : beta;
	}
      }
      return beta;
    }
    catch(GameOverException e) {
      if (e.getWinner() == 2)
	return beta;
      else if (e.getWinner() == 1)
	return -1;
      else
	return 0;
    }
  }
}

int MinimaxPlayer2::heuristic(GameBoard& gameBoard, int bin) {
  GameBoard g = gameBoard.clone();
  int current_score = g[g.getKalaha2()];
  //if the move is valid, and doesn't break any rules, then make it
  //if the move results in a capture (increase in score) then return
  //a high score, otherwise return number of pieces in bin;
  if (!g.isInvalidMove(bin)) {
    g.makeMove(bin);
    if (g[g.getKalaha2()] > current_score)
      return gameBoard[bin]*(g[g.getKalaha2()]);
    else
      return gameBoard[bin];
  }
  else
    return -2;
}

