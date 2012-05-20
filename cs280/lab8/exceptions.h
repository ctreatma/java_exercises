#ifndef BEN_TAITELBAUM_EXCEPTIONS_H
#define BEN_TAITELBAUM_EXCEPTIONS_H

#include <string>
#include <iostream>

class MancalaException
{
protected:
    std::string message;
public:
    MancalaException(std::string message="");
    virtual ~MancalaException();
    virtual std::string getMessage();
    virtual std::string toString();
};

class FileNotFoundException : public MancalaException
{
private:
    std::string filename;
public:
    FileNotFoundException(std::string& filename);
    virtual ~FileNotFoundException();
    std::string& getFilename();
    virtual std::string toString();
};

class GameOverException : public MancalaException
{
private:
    int winningPlayer;
public:
    GameOverException(int player);
    virtual ~GameOverException();

    // @returns: 1  (Player 1 Won)
    //           2  (Player 2 Won)
    //           -1 (Tie Game)
    int getWinner();
    virtual std::string toString();
};

class InvalidMoveException : public MancalaException
{
private:
    int player;
    int move;

    // an optional reference to the rule that was violated
    // default is -1
    int rule;
public:
    InvalidMoveException(int player, int move, int rule=-1);
    virtual ~InvalidMoveException();
    int getPlayer();
    int getMove();
    int getRule();
    virtual std::string toString();
};

class TimeOutException : public MancalaException
{
private:
    int player;
public:
    TimeOutException(int player);
    virtual ~TimeOutException();

    // @returns: 1  (Player 1 Timed Out)
    //           2  (Player 2 Timed Out)
    int getPlayer();
    virtual std::string toString();
};

std::ostream& operator<<(std::ostream& sout, MancalaException& e);

// this will be the new default exception handler
// there MUST be a line somewhere in main that calls
// std::set_terminate(mancala_default_exception_handler);
void mancala_default_exception_handler();

#endif //BEN_TAITELBAUM_EXCEPTIONS_H
