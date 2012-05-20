import scala.io._
import scala.swing._
import scala.util.Random
import java.io.File

/**
 * Implements the Eliza program
 * @author Charles Treatman
 */
object Eliza {

  /**
   * Runs the Eliza program
   * @param args Not used.
   */
  def main(args:Array[String]) {
    val (patterns,keywords,generic) = chooseFile() match {
      case Some(file) => readRules(file)
      case None => exit
    }
    println("Welcome to Eliza!")
    controlLoop(patterns, keywords, generic, List[String]());
  }
  
  /**
   * Reads a file to determine rules for generating responses
   * to user input.
   * @param file The file containing the rules
   * @return A Tuple3 containing the list of pattern rules, the
   * list of keyword rules, and the list of generic rules
   */
  def readRules(file:File) = {
    val lines = Source.fromFile(file).getLines().toList
    (readPatternRules(lines),readKeywordRules(lines),readGenericRules(lines))
  }
  
  /**
   * Find pattern rules in a list of rules.  Pattern rules
   * are productions (containing =>) where part of the
   * user's input is used in the response ($x)
   * @param lines A list of Strings to search for pattern rules
   * @return A List of Pairs of (user input pattern, response pattern)
   */
  def readPatternRules(lines:List[String]) = {
    for (line <- lines; if ((line contains "=>") && (line contains "$x")))
      yield (line.split("=>").head.trim,line.split("=>").tail.head.trim)
  }
  
  /**
   * Find keyword rules in a list of rules.  Keyword rules
   * are productions (containing =>) that do not use the
   * user's input as part of the response.
   * @param lines A list of Strings to search for keyword rules
   * @return A List of Pairs of (keyword, response)
   */
  def readKeywordRules(lines:List[String]) = {
    for (line <- lines; if ((line contains "=>") && !(line contains "$x")))
      yield (line.split("=>").head.trim,line.split("=>").tail.head.trim)
  }
  
  /**
   * Find generic rules in a list of rules.  Generic rules
   * are simply strings.  They are used to generate a response
   * when the user's input did not match any keyword or pattern
   * rules
   * @param lines A list of Strings to search for generic rules
   * @return A List of generic response Strings
   */
  def readGenericRules(lines:List[String]) = {
    for (line <- lines; if !(line contains "=>"))
      yield (line.trim)
  }
  
  /**
   * The control loop for the Eliza program.  Requests input
   * from the user.  If the input is "quit," the program exits.
   * Otherwise, a response is generated from the input, and the
   * method recurs.
   * @param patterns A List of Pairs representing the pattern rules
   * @param keywords A List of Pairs representing the keyword rules
   * @param generics A List of Strings representing the generic rules
   * @param topics A List of Strings representing the topics discussed so far
   */
  def controlLoop(patterns:List[Pair[String,String]],
          keywords:List[Pair[String,String]],
          generic:List[String],
          topics:List[String]) {
    print("You: ");
    val input = Console.readLine() toLowerCase;
    input match {
      case "quit" => println("goodbye")
      case _ =>
        val topic = handleInput(input, patterns, keywords, generic, topics)
        topic match {
          case Some(string) => controlLoop(patterns, keywords, generic, string :: topics)
          case None => controlLoop(patterns, keywords, generic, topics)
        }
    }
  }       
      
  /**
   * Generates a response to the user's input using the various rules.
   * @param input The user's input
   * @param patterns A List of Pairs representing the pattern rules
   * @param keywords A List of Pairs representing the keyword rules
   * @param generics A List of Strings representing the generic rules
   * @param topics A List of Strings representing the topics discussed so far
   * @return The topic of the user's input if the user's input matched a pattern
   * rule, or None if the user's input did not match a pattern rule.
   */
  def handleInput(input:String, patterns:List[Pair[String,String]],
          keywords:List[Pair[String,String]],
          generic:List[String],
          topics:List[String]):Option[String] = {
    print("Eliza: ")
    val s=input.trim().toList.map(stripPunctuation).mkString("")
    val patternResponses = findPatternAnswer(s, patterns)
    patternResponses match {
      case first :: rest =>
        val (topic, response) = getRandomElement(first :: rest)
        println(response)
        return Some(topic toString)
      case _ =>
        val keywordResponses = findKeywordAnswer(s, keywords)
        keywordResponses match {
          case first :: rest => println(getRandomElement(first :: rest))
          case _ =>
            findGenericAnswer(s, generic, topics) match {
              case Some(response) => println(response)
              case None => println("I'm at a loss for words.")
            }
        }
    }
    return None
  }
  
  /**
   * Picks a random element from a list
   * @param items The list from which to pick an element
   * @return A random element of the input list
   */
  def getRandomElement(items:List[Any]) = {     
    val random = new Random().nextInt(items.length)
    items(random)
  }

  /**
   * Finds all pattern rules that match the user's input
   * @param input The user's input
   * @param patterns The pattern rules
   * @return A List of Pairs of (topic, response) for the user's input
   */
  def findPatternAnswer(input:String,patterns:List[Pair[String,String]]) = {
    val responses =
    for (Pair(key,resp)<-patterns; if matches(key.toLowerCase,input);
         topic = getTopic(key.toLowerCase, input))
        yield (topic, makeResponse(topic, resp))
    responses
  }

  /**
   * Finds all keyword rules that match the user's input
   * @param input The user's input
   * @param keywords The keyword rules
   * @return A List of responses to the user's input
   */
  def findKeywordAnswer(input:String,keywords:List[Pair[String,String]]) = {
    val responses =
    for (Pair(key,resp)<-keywords; if input contains key.toLowerCase )
        yield makeResponse(input, resp)
    responses
  }
  
  /**
   * Finds all generic rules that match the user's input
   * @param input The user's input
   * @param generics The generic rules
   * @param topics The topics discussed so far
   * @return A List of responses to the user's input
   */
  def findGenericAnswer(input:String,generics:List[String],topics:List[String]) = {
    val responses = generics filter {s =>
      !((s contains "$x") && topics == Nil)
    } map {s =>
      if ((s contains "$x"))
        s.replace("$x",getRandomElement(topics) toString)
      else s
    }
    
    if (responses == Nil)
      None
    else
      Some(getRandomElement(responses))
  }

  /**
   * Checks if a string matches a regular expression
   * @param compare A String containing the regular expression to test
   * @param input The input to test for a match
   * @return A Boolean indicating whether the input matched
   */
  def matches(compare:String, input:String) = {
    try {
      val regex = compare.r
      input match {
        case regex(topic) => true
        case _ => false
      }
    }
    catch {
      case ex:MatchError => false
    }
  }

  /**
   * Gets the topic from a pattern rule match
   * @param compare A String containing the regular expression to match
   * @param input The input to search for a topic
   * @return The topic of the input
   */
  def getTopic(compare:String, input:String) = {
    val regex = compare.r
    val regex(topic) = input
    words(topic) map switchPerspective mkString(" ")
  }
     
  /**
   * Splits a string into words (on whitespace)
   * @param s The String to split
   * @return A List of the words in s
   */
  def words(s:String):List[String] = {
    s split("[\\s+,]") toList
  } 
  
  /**
   * Performs pronoun switching on a string containing a single word
   * @param s The String the switch
   * @return The pronoun-switched string, or s if it did not need switching
   */
  def switchPerspective(s:String):String = {
    s match {
      case "i" => "you"
      case "me" => "you"
      case "my" => "your"
      case "you" => "me"
      case "your" => "my"
      case "i'm" => "you're"
      case "you're" => "I'm"
      case _ => s
    }
  }

  /**
   * Forms a response using a topic and a response
   * @param topic The topic to use for the response
   * @param response The response to complete using topic
   * @return The response, with any "$x" replaced with topic
   */
  def makeResponse(topic:String, response:String) = {
    response.replace("$x",topic)
  }
  
  /**
   * Utility function to strip most punctuation from a string
   * @param c A character in the string being stripped
   * @return If c is a letter, digit, whitespace, ',', or ' ' ', then c.
   * Otherwise an empty space is returned.
   */
  def stripPunctuation(c:Char):Char = {
    c match {
      case ',' => c
      case '\'' => c
      case _ if c.isLetterOrDigit => c
      case _ if c.isWhitespace => c
      case _ => ' '
    }
  }
  
  /** Ask the user to choose an input file.
    * @return The file (or `None`). */
  def chooseFile(): Option[File] = {
    val chooser = new FileChooser()
    import FileChooser.Result._
    val theFileResult = chooser.showOpenDialog(null)
    theFileResult match {
      case Approve =>
        Some(chooser.selectedFile)
      case Cancel => None
      case Error =>
        println( "Error: Unable to open file.")
        None
    }
  }
}