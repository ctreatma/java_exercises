#include "exceptions.h"
#include <sstream>

MancalaException::MancalaException(std::string message)
{
    this->message = message;
}
MancalaException::~MancalaException() {}
std::string MancalaException::getMessage() { return message; }
std::string MancalaException::toString()
{
    std::string str = "Mancala Exception: ";
    str += getMessage();
    return str;
}

FileNotFoundException::FileNotFoundException(std::string& filename)
    : MancalaException("File Not Found: ")
{
    this->filename = filename;
}
FileNotFoundException::~FileNotFoundException() {}
std::string& FileNotFoundException::getFilename() { return filename; }
std::string FileNotFoundException::toString()
{
    std::string str = "File Not Found: ";
    str += filename;
    return str;
}

GameOverException::GameOverException(int player)
    : MancalaException("Game Over!")
{
    winningPlayer = player;
}
GameOverException::~GameOverException() {}
int GameOverException::getWinner()
{
    return winningPlayer;
}
std::string GameOverException::toString() 
{ 
    std::ostringstream oss;
    oss << message;
    oss << "\nWinner: ";
    oss << winningPlayer;
    return oss.str();
}

InvalidMoveException::InvalidMoveException(int player, int move,
					   int rule)
    :MancalaException("Invalid Move!")
{
    this->player = player;
    this->move = move;
    this->rule = rule;
}
InvalidMoveException::~InvalidMoveException() {}
int InvalidMoveException::getPlayer() { return player; }
int InvalidMoveException::getMove() { return move; }
int InvalidMoveException::getRule() { return rule; }
std::string InvalidMoveException::toString() 
{ 
    std::ostringstream oss;
    oss << message << "\nMade by player " << player;
    if (rule > 0)
	oss << "\nSee Rule #" << rule;
    return oss.str();
}

TimeOutException::TimeOutException(int player)
    : MancalaException("Time Out!")
{
    this->player = player;
}
TimeOutException::~TimeOutException() {}
int TimeOutException::getPlayer()
{
    return player;
}
std::string TimeOutException::toString() 
{ 
    std::ostringstream oss;
    oss << message;
    oss << "\nWinner: ";
    oss << !player;
    return oss.str();
}


std::ostream& operator<<(std::ostream& sout, MancalaException& e) 
{
    sout << e.toString() << std::endl;
    return sout;
}

void mancala_default_exception_handler()
{
    // this hack was taken from Stroustrup Special Edition p. 380
    try
    {
	throw;
    }
    catch(InvalidMoveException& e)
    {
	std::cerr << "Uncaught InvalidMoveException: " << e << std::endl;
	exit(1);
    }
    catch(GameOverException& e)
    {
	std::cerr << "Uncaught GameOverException: " << e << std::endl;
	exit(1);
    }
    catch(MancalaException& e)
    {
	std::cerr << "A MancalaException was caught by the default handler"
		  << ", which means you didn\'t catch it where you should have:\n"
		  << e << std::endl;
	exit(1);
    }
    catch(std::exception& e)
    {
	std::cerr << "A standard library exception was caught:\n"
	     << e.what() << std::endl;
	exit(1);
    }
    catch(...)
    {
	std::cerr << "Sorry, but some unexpected exception occurred.\n"
	     << "gdb is smarter than I am, so try using it to find the error"
	     << std::endl;
	exit(1);
    }
}
