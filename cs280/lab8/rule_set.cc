#include "rule_set.h"
#include <cstdlib>
#include <fstream>
#include <sstream>

RuleSet::~RuleSet() {}
RuleSet* RuleSet::createRuleSet(std::string serializedRuleSet)
{
    std::istringstream iss(serializedRuleSet);
    std::string id;
    int bPS,pPB,mD,tL1,tL2;
    std::vector<int*> int_vals(5);
    int_vals[0] = &bPS;
    int_vals[1] = &pPB;
    int_vals[2] = &mD;
    int_vals[3] = &tL1;
    int_vals[4] = &tL2;
    if (! (iss >> id))
	return NULL;
    for (unsigned int i = 0; i < int_vals.size(); i++)
    {
	if (! (iss >> *int_vals[i]) )
	{
	    return NULL;
	}
    }

    if (id == "E")
    {
	return new EgyptianRuleSet(bPS, pPB, mD, tL1, tL2);
    }
    else if (id == "W")
    {
	return new WariRuleSet(bPS, pPB, mD, tL1, tL2);
    }
}
const std::string& RuleSet::getRules() const { return rules; }
int RuleSet::getBinsPerSide() const { return binsPerSide; }
int RuleSet::getInitialPiecesPerBin() const { return piecesPerBin; }
int RuleSet::getMaxDepth() const { return maxDepth; }
int RuleSet::getTimeLimit1() const { return timeLimit1; }
int RuleSet::getTimeLimit2() const { return timeLimit2; }

int RuleSet::getStaleMoves() const { return stale_moves; }
int RuleSet::getNumPlayingPieces() const { return playing_pieces; }

EgyptianRuleSet::EgyptianRuleSet(int binsPerSide, 
				 int piecesPerBin, int maxDepth,
				 int timeLimit1, int timeLimit2)
{
    this->binsPerSide = binsPerSide;
    this->piecesPerBin = piecesPerBin;
    this->maxDepth = maxDepth;
    this->timeLimit1 = timeLimit1;
    this->timeLimit2 = timeLimit2;

    stale_moves = 0;
    playing_pieces = piecesPerBin * (binsPerSide - 2);

    std::ostringstream oss;
    std::ifstream ifs("e_rules.txt");
    if (ifs)
    {
	char ch;
	while (ifs.get(ch)) 
	    oss.put(ch);
    }
    rules = oss.str();
}
EgyptianRuleSet::~EgyptianRuleSet() {}
std::vector<int> EgyptianRuleSet::createInitialBoard() const
{
    std::vector<int> board;
    for(int i = 0; i < 2; i++)
    {
	for(int j = 0; j < binsPerSide-1; j++)
	{
	    board.push_back(piecesPerBin);
	}
	board.push_back(0);
    }
    return board;
}

int EgyptianRuleSet::isInvalidMove(const std::vector<int>& board, 
				   int player, int move) const
{
    if (move < 0 || move >= board.size()-1
	|| board[move] == 0)
	return 1;
	
    else if (player==1) // Player 1
    {
	if (move >= (binsPerSide-1))
	    return 1;
    }
    else             // Player 2
    {
	if (move < binsPerSide)
	    return 1;
    }

    // okay, so now we have to see if this move will leave the opponent
    // with no pieces
    int binsPerSide = board.size() / 2;
    if ( board[move] >= binsPerSide - (move % binsPerSide) )
    {
	return 0;
    }
    else
    {
	int lastBin = (move+board[move]) % board.size();
	if ( (lastBin+1) % binsPerSide == 0)
	    return 0;

	if ( board[lastBin] == 0 )
	{
	    int oppLastBin = board.size() - 2 - lastBin;
	    if (player == 2)
	    {
		bool okay = false;
		for (int b = 0; b < binsPerSide - 1; b++)
		{
		    if ( (b != oppLastBin) && (board[b] != 0) )
		    {
			okay = true;
			break;
		    }
		}
		if (okay)
		{
		    return 0;
		}
		else // !okay
		{
		    // check if the player had a valid move

		    // the magic number
		    int mn = board[move] + (move - binsPerSide); 

		    int okay2 = true;
		    for (int b = binsPerSide; b < board.size() - 1; b++)
		    {
			if (b != move && board[b] != mn && board[b]>0)
			{
			    okay2 = false;
			    break;
			}
			mn--;
		    }
		    if (okay2)
		    {
			return 0;
		    }
		    else
		    {
			return 7; // the tricky rule
		    }
		}
	    }
	    else // player 1
	    {
		bool okay = false;
		for (int b = binsPerSide; b < board.size() - 1; b++)
		{
		    if ( (b != oppLastBin) && (board[b] != 0) )
		    {
			okay = true;
			break;
		    }
		}
		if (okay)
		{
		    return 0;
		}
		else
		{
		    // check if the player had a valid move

                      // the magic number
		    int mn = board[move] + move;

		    int okay2 = true;
		    for (int b = 0; b < binsPerSide - 1; b++)
		    {
			if (b != move && board[b] != mn && board[b] > 0)
			{
			    okay2 = false;
			    break;
			}
			mn--;
		    }
		    if (okay2)
		    {
			return 0;
		    }
		    else
		    {
			return 7; // the tricky rule
		    }
		}
	    }
	}
	else
	{
	    // no need to check here...
	    return 0;
	}
    }
}

// throws InvalidMoveException, GameOverException
int EgyptianRuleSet::makeMove(std::vector<int>& board, 
			      int player, int move)
    throw(InvalidMoveException, GameOverException)
{
    int i = isInvalidMove(board, player, move);
    if (i)
    {
	throw InvalidMoveException(player, move, i);
    }
    else
    {
	int binsPerSide = board.size() / 2;
	int oppKalaha = 2*binsPerSide - 1;
	if (player == 2)
	    oppKalaha = binsPerSide - 1;
	int myKalaha = binsPerSide -1;
	if (player == 2)
	    myKalaha = 2*binsPerSide - 1;
	std::vector<int> new_board = board;
	int playing_pieces_old = playing_pieces;

	int pieces = board[move];
	board[move] = 0;
	while (pieces > 0)
	{
	    move = ( (move+1) % board.size());
	    if (move != oppKalaha)
	    {
		board[move]++;
		pieces--;
	    }
	}

	int nextPlayer = (player % 2) + 1;

	if (move == myKalaha)
	{
	    nextPlayer = player;
	}

	// trickier part
	else if (board[move] == 1 && myKalaha > move && 
		 myKalaha - move < binsPerSide)
	{
	    int oppBin = board.size() - 2 - move;
	    board[myKalaha] += board[oppBin] + board[move];
	    playing_pieces -= (board[oppBin] + board[move]);
	    board[oppBin] = 0;
	    board[move] = 0;
	}

	// check if the game is over -- this is O(n), sorry...
	bool player1Dead = true;
	for (int b = 0; b < binsPerSide-1; b++)
	{
	    if (board[b] > 0)
	    {
		player1Dead = false;
		break;
	    }
	}
	bool player2Dead = true;
	for (int b = binsPerSide; b < board.size()-1; b++)
	{
	    if (board[b] > 0)
	    {
		player2Dead = false;
		break;
	    }
	}

	if (player1Dead || player2Dead)
	{
	    for (int b = binsPerSide; b < board.size()-1; b++)
	    {
		board[board.size() - 1] += board[b];
		playing_pieces -= board[b];
		board[b] = 0;
	    }
	    for (int b = 0; b < binsPerSide-1; b++)
	    {
		board[binsPerSide - 1] += board[b];
		playing_pieces -= board[b];
		board[b] = 0;
	    }
	}

	if (player1Dead || player2Dead)
	{
	    int winningPlayer = -1; 
	    if (board[binsPerSide - 1] > board[board.size() - 1])
		winningPlayer = 1;
	    else if (board[board.size() - 1] > board[binsPerSide - 1])
		winningPlayer = 2;
	    throw GameOverException(winningPlayer);
	}
	
	if (playing_pieces == playing_pieces_old)
	    stale_moves++;
	else
	    stale_moves = 0;

	return nextPlayer;
    }
}

std::string EgyptianRuleSet::serialize()
{
    std::ostringstream oss;
    oss << "E " << binsPerSide << " " << piecesPerBin << " "
	<< maxDepth << " " << timeLimit1 << " " << timeLimit2;
    return oss.str();
}

EgyptianRuleSet* EgyptianRuleSet::clone() const
{
    EgyptianRuleSet* rs =
	new EgyptianRuleSet(binsPerSide, piecesPerBin,
			    maxDepth, timeLimit1, timeLimit2);
    rs->stale_moves = stale_moves;
    rs->playing_pieces = playing_pieces;
    return rs;
}


WariRuleSet::WariRuleSet(int binsPerSide, 
			 int piecesPerBin, int maxDepth,
			 int timeLimit1, int timeLimit2)
{
    this->binsPerSide = binsPerSide;
    this->piecesPerBin = piecesPerBin;
    this->maxDepth = maxDepth;
    this->timeLimit1 = timeLimit1;
    this->timeLimit2 = timeLimit2;

    stale_moves = 0;
    playing_pieces = piecesPerBin * (2*binsPerSide - 2);

    std::ostringstream oss;
    std::ifstream ifs("w_rules.txt");
    if (ifs)
    {
	char ch;
	while (ifs.get(ch)) 
	    oss.put(ch);
    }
    rules = oss.str();
}
WariRuleSet::~WariRuleSet() {}
std::vector<int> WariRuleSet::createInitialBoard() const
{
    std::vector<int> board;
    for(int i = 0; i < 2; i++)
    {
	for(int j = 0; j < binsPerSide-1; j++)
	{
	    board.push_back(piecesPerBin);
	}
	board.push_back(0);
    }
    return board;
}

bool WariRuleSet::hasValidMove(const std::vector<int>& board,
			       int player, int move, int skip_move) const
{
    if (move == skip_move)
	return hasValidMove(board, player, move+1, skip_move);

    if (player == 1)
    {
	if (move >= binsPerSide - 1) // base case
	    return false;
    }
    else // player 2
    {
	if (move >= board.size() - 1) // base case
	    return false;
    }

    if (board[move] == 0)
	return hasValidMove(board, player, move+1, skip_move);

    std::vector<int> board_copy = board; // COPY of board
    int current_bin = move;
    int pieces = board_copy[move];
    board_copy[move] = 0;
    while(pieces > 0)
    {
	current_bin = (current_bin+1) % board_copy.size();
	if (current_bin != binsPerSide - 1 &&
	    current_bin != board_copy.size()-1 &&
	    current_bin != move)
	{
	    board_copy[current_bin]++;
	    pieces--;
	}
    }

    if (player == 1)
    {
        while(current_bin >= binsPerSide &&
	      current_bin < board_copy.size()-1 &&
	      (board_copy[current_bin] == 2 ||
	       board_copy[current_bin] == 3))
	{
	    board_copy[binsPerSide - 1] += board_copy[current_bin];
	    board_copy[current_bin] = 0;
	    current_bin--;
	}
    }
    else // player 2
    {
        while(current_bin >= 0 &&
	      current_bin < binsPerSide - 1 &&
	      (board_copy[current_bin] == 2 ||
	       board_copy[current_bin] == 3))
	{
	    board_copy[board_copy.size() - 1]+=board_copy[current_bin];
	    board_copy[current_bin] = 0;
	    current_bin--;
	}
    }

    // is the other player's side empty?
    bool otherSideEmpty = true;
    if (player == 1)
    {
	for (int b = binsPerSide; b < board_copy.size() - 1; b++)
	{
	    if (board_copy[b] > 0)
	    {
		otherSideEmpty = false;
		break;
	    }
	}
    }
    else // player 2
    {
	for (int b = 0; b < binsPerSide - 1; b++)
	{
	    if (board_copy[b] > 0)
	    {
		otherSideEmpty = false;
		break;
	    }
	}
    }

    if (otherSideEmpty)
    {
	return hasValidMove(board, player, move+1, skip_move);
    }
    else return true;
}

int WariRuleSet::isInvalidMove(const std::vector<int>& board, 
			       int player, int move) const
{
    if (move < 0 || move >= board.size()-1
	|| board[move] == 0)
	return 1;
	
    else if (player==1) // Player 1
    {
	if (move >= (binsPerSide-1))
	    return 1;
    }
    else             // Player 2
    {
	if (move < binsPerSide)
	    return 1;
    }

    /* quick and dirty approach: simulate the move and see if
     * it leaves the board in a valid position
     */
    std::vector<int> board_copy = board; // a COPY of board
    int current_bin = move;
    int pieces = board_copy[move];
    board_copy[move] = 0;
    while(pieces > 0)
    {
	current_bin = (current_bin+1) % board_copy.size();
	if (current_bin != binsPerSide - 1 &&
	    current_bin != board_copy.size()-1 &&
	    current_bin != move)
	{
	    board_copy[current_bin]++;
	    pieces--;
	}
    }

    if (player == 1)
    {
        while(current_bin >= binsPerSide &&
	      current_bin < board_copy.size()-1 &&
	      (board_copy[current_bin] == 2 ||
	       board_copy[current_bin] == 3))
	{
	    board_copy[binsPerSide - 1] += board_copy[current_bin];
	    board_copy[current_bin] = 0;
	    current_bin--;
	}
    }
    else // player 2
    {
        while(current_bin >= 0 &&
	      current_bin < binsPerSide - 1 &&
	      (board_copy[current_bin] == 2 ||
	       board_copy[current_bin] == 3))
	{
	    board_copy[board_copy.size() - 1] += board_copy[current_bin];
	    board_copy[current_bin] = 0;
	    current_bin--;
	}
    }

    // is the other player's side empty?
    bool otherSideEmpty = true;
    if (player == 1)
    {
	for (int b = binsPerSide; b < board_copy.size() - 1; b++)
	{
	    if (board_copy[b] > 0)
	    {
		otherSideEmpty = false;
		break;
	    }
	}
    }
    else // player 2
    {
	for (int b = 0; b < binsPerSide - 1; b++)
	{
	    if (board_copy[b] > 0)
	    {
		otherSideEmpty = false;
		break;
	    }
	}
    }

    if (otherSideEmpty)
    {
	// this is only an invalid move if there was a move
	// that didn't leave the other player's side empty
	int first_try = 0;
	if (player == 2)
	    first_try = binsPerSide;
	if (hasValidMove(board, player, first_try, move))
	{
	    return 5; // rule #5
	}
    }
    
    return 0; // valid move

}

// throws InvalidMoveException, GameOverException
int WariRuleSet::makeMove(std::vector<int>& board, 
			      int player, int move)
    throw(InvalidMoveException, GameOverException)
{
    int i = isInvalidMove(board, player, move);
    if (i)
    {
	throw InvalidMoveException(player, move, i);
    }

    int playing_pieces_old = playing_pieces;
    int current_bin = move;
    int pieces = board[move];
    board[move] = 0;
    while(pieces > 0)
    {
	current_bin = (current_bin+1) % board.size();
	if (current_bin != binsPerSide - 1 &&
	    current_bin != board.size()-1 &&
	    current_bin != move)
	{
	    board[current_bin]++;
	    pieces--;
	}
    }

    if (player == 1)
    {
        while(current_bin >= binsPerSide &&
	      current_bin < board.size()-1 &&
	      (board[current_bin] == 2 ||
	       board[current_bin] == 3))
	{
	    board[binsPerSide - 1] += board[current_bin];
	    playing_pieces -= board[current_bin];
	    board[current_bin] = 0;
	    current_bin--;
	}
    }
    else // player 2
    {
        while(current_bin >= 0 &&
	      current_bin < binsPerSide - 1 &&
	      (board[current_bin] == 2 ||
	       board[current_bin] == 3))
	{
	    board[board.size() - 1] += board[current_bin];
	    playing_pieces -= board[current_bin];
	    board[current_bin] = 0;
	    current_bin--;
	}
    }

    int nextPlayer;

    // who goes next?
    // did this player capture any pieces?
    if (playing_pieces_old > playing_pieces)
	nextPlayer = player;
    else
	nextPlayer = ( (player%2) + 1);



    // is the game over?
    // are the next player's bins empty?

    bool nextPlayerEmpty = true;
    if (nextPlayer == 1)
    {
	for (int b = 0; b < binsPerSide - 1; b++)
	{
	    if (board[b] > 0)
	    {
		nextPlayerEmpty = false;
		break;
	    }
	}
    }
    else // next player is player2
    {
	for (int b = binsPerSide; b < board.size() - 1; b++)
	{
	    if (board[b] > 0)
	    {
		nextPlayerEmpty = false;
		break;
	    }
	}
    }



    if (nextPlayerEmpty) // game's over
    {
	if (nextPlayer == 1)
	{
	    for (int b = binsPerSide; b < board.size()-1; b++)
	    {
		board[board.size() - 1] += board[b];
		playing_pieces -= board[b];
		board[b] = 0;
	    }
	}
	else // next player is player 2
	{
	    for (int b = 0; b < binsPerSide - 1; b++)
	    {
		board[binsPerSide - 1] += board[b];
		playing_pieces -= board[b];
		board[b] = 0;
	    }
	}

	if (board[binsPerSide - 1] > board[board.size() - 1])
	{
	    throw GameOverException(1);
	}
	else if (board[board.size() - 1] > board[binsPerSide - 1])
	{
	    throw GameOverException(2);
	}
	else
	{
	    throw GameOverException(-1);
	}
    }
    
    // is this a stale move?
    if (playing_pieces_old == playing_pieces)
    {
	stale_moves++;
    }
    else
	stale_moves = 0;


    // can this game be won?
    if (stale_moves >= playing_pieces * (binsPerSide - 1))
    {
	std::cerr << "playing pieces: "<<playing_pieces<<std::endl;
	std::cerr << "stale_moves: "<<stale_moves<<std::endl;

	// clear player 1's side
	for (int b = 0; b < binsPerSide - 1; b++)
	{
	    playing_pieces -= board[b];
	    board[b] = 0;
	}
	
	// clear player 2's side
	for (int b = binsPerSide; b < board.size()-1; b++)
	{
	    playing_pieces -= board[b];
	    board[b] = 0;
	}

	if (board[binsPerSide - 1] > board[board.size() - 1])
	{
	    throw GameOverException(1);
	}
	else if (board[board.size() - 1] > board[binsPerSide - 1])
	{
	    throw GameOverException(2);
	}
	else
	{
	    throw GameOverException(-1);
	}
    }

    return nextPlayer;
}

std::string WariRuleSet::serialize()
{
    std::ostringstream oss;
    oss << "W " << binsPerSide << " " << piecesPerBin << " "
	<< maxDepth << " " << timeLimit1 << " " << timeLimit2;
    return oss.str();
}

WariRuleSet* WariRuleSet::clone() const
{
    WariRuleSet* rs =
	new WariRuleSet(binsPerSide, piecesPerBin,
			    maxDepth, timeLimit1, timeLimit2);
    rs->stale_moves = stale_moves;
    rs->playing_pieces = playing_pieces;
    return rs;
}

