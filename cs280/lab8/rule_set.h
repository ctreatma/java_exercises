#ifndef BEN_TAITELBAUM_RULE_SET_H
#define BEN_TAITELBAUM_RULE_SET_H

#include <vector>
#include <string>
#include "exceptions.h"

class RuleSet
{
protected:
    int binsPerSide; // how many bins per side (includes the kalaha)
    int piecesPerBin; // how many pieces per bin initially
    int maxDepth; // maxDepth is the maximum DEPTH of a minimax search
    std::string rules; // a little help text to explain this version
    int timeLimit1; // player 1's time limit ( in milliseconds )
    int timeLimit2; // player 2's time limit ( in milliseconds )
    int stale_moves;
    int playing_pieces;
public:
    virtual ~RuleSet();

    // read a serialized string and create an appropriate RuleSet
    // @returns: a RuleSet* or NULL if no appropriate RuleSet exists
    // YOU are responsible deleting this RuleSet*
    static RuleSet* createRuleSet(std::string serializedRuleSet);

    virtual const std::string& getRules() const;
    virtual int getBinsPerSide() const;
    virtual int getInitialPiecesPerBin() const;
    virtual int getMaxDepth() const;
    virtual int getTimeLimit1() const;
    virtual int getTimeLimit2() const;
    virtual std::vector<int> createInitialBoard()const = 0;

    /*
      The reason this tests if it's INvalid instead of valid, is so
      If we feel like it, we can return a number corresponding
      to the broken rule. Or, we can return -1 if this is not
      applicable to our implementation. Otherwise, we return 0.
      
      N.B. Just don't have a rule #0, okay?
      
      @returns: 0                   if no rule has been broken
                -1                  if a rule was broken but someone was lazy
		                    or the depth is too large
                positive number     the rule that was broken

      this should NOT change board
    */
    virtual int isInvalidMove(const std::vector<int>& board, 
			      int player, int move) const = 0;

    // @throws: InvalidMoveException, GameOverException
    // @returns: the player whose turn is next (1 or 2)
    // this WILL NOT copy board, and WILL change the board
    virtual int makeMove(std::vector<int>& board, 
			 int player, int move)
	throw(InvalidMoveException, GameOverException) = 0;

    // serialize RuleSet into a string to send over the network 
    virtual std::string serialize() = 0;

    // how many moves have gone by without a capture?
    virtual int getStaleMoves() const;

    // how many pieces are currently in play? (not in kalahas)
    virtual int getNumPlayingPieces() const;

    virtual RuleSet* clone() const = 0;
};

class EgyptianRuleSet : public RuleSet
{
public:
    EgyptianRuleSet(int binsPerSide, int piecesPerBin, int maxDepth,
		    int timeLimit1, int timeLimit2);
    virtual ~EgyptianRuleSet();
    virtual std::vector<int> createInitialBoard() const;
    virtual int isInvalidMove(const std::vector<int>& board, 
			      int player, int move) const;
    // throws InvalidMoveException, GameOverException
    virtual int makeMove(std::vector<int>& board, 
			 int player, int move)
	throw(InvalidMoveException, GameOverException);
    virtual std::string serialize();
    virtual EgyptianRuleSet* clone() const;
};


class WariRuleSet : public RuleSet
{
protected:
    // a recursive helper function
    virtual bool hasValidMove(const std::vector<int>& board,
			      int player, int move, int skip_move) const;
public:
    WariRuleSet(int binsPerSide, int piecesPerBin, int maxDepth,
		int timeLimit1, int timeLimit2);
    virtual ~WariRuleSet();
    virtual std::vector<int> createInitialBoard() const;
    virtual int isInvalidMove(const std::vector<int>& board, 
			      int player, int move) const;
    // throws InvalidMoveException, GameOverException
    virtual int makeMove(std::vector<int>& board, 
			 int player, int move)
	throw(InvalidMoveException, GameOverException);
    virtual std::string serialize();
    virtual WariRuleSet* clone() const;
};


#endif // BEN_TAITELBAUM_RULE_SET_H
