/**
 * Implements the Playfair cipher
 * 
 * @author Charles Treatman
 */
object Playfair {
  val alphabet = "abcdefghiklmnopqrstuvwxyz"
  val Header = """([a-zA-Z]+)\s+([a-zA-Z]+[\s[a-zA-Z]]*)""" r

  /**
   * Displays a JFileChooser and runs the Playfair cipher on
   * the selected file.
   * 
   * @param args Not used.
   */
  def main(args: Array[String]) {
    import java.io._
    import scala.io.Source
    import javax.swing.JFileChooser
    
    val fileChooser = new JFileChooser()
    fileChooser.showOpenDialog(null)
    
    val lines = Source.fromFile(fileChooser.getSelectedFile).getLines().toList
    
    try {
      val Header(direction, keyword) = (lines head).toLowerCase.replaceAll("j","i")
      
      val cipher = (keyword + alphabet).replaceAll("""\s+""","").toList.distinct
      val message = (lines tail).reduceLeft(_ + _).toLowerCase.replaceAll("j","i").replaceAll("""[^a-zA-Z]""","")
      
      // Literal function to get the coordinates of a character in the cipher.
      val cipherCoordinates = (x:Char) => {
        Tuple2((cipher.indexOf(x))/5,
               cipher.indexOf(x) % 5)
      }
       
      // Literal function to get a character in the cipher by its coordinates.
      val charAt = (row:Int,col:Int) => {
        val index = {
          if (col < 0) {
            (5 * row) + col + 5
          }
          else if (col >= 5) {
            (5 * row) + col - 5
          }
          else {
            (5 * row) + col
          }
        }
        if (index < 0) {
          cipher(index + cipher.length).toString
        }
        else if (index >= cipher.length) {
          cipher(index - cipher.length).toString
        }
        else {
          cipher(index).toString
        }
      }
    
      direction match {
        case "encipher" => println(formatMessage(encipherMessage(cipherCoordinates, charAt, message), 1))
        case "decipher" => println(decipherMessage(cipherCoordinates, charAt, message))
        case _ => println("Error!")
      }
    }
    catch {
      case ex: MatchError => println("""Error:  First line of file must be 'encipher <keyword>' or 'decipher <keyword>'""")
      case ex: MalformedMessageException => println("""Error:  Message to be deciphered does not have an even number of characters""")
    }
  }

  /**
   * Formats the output message for display as specified in
   * the assignment description.
   * 
   * @param message The message to format.
   * @param count The number of characters seen so far
   * @return The message, in blocks of 5 characters separated by spaces, with newlines every 50 characters.
   */
  def formatMessage(message: String, count: Int):String = {
    if (message.length > 0) {
      if (count % 50 == 0) {
        message.charAt(0) + System.getProperty("line.separator") + formatMessage(message.substring(1), count+1)
      }
      else if (count % 5 == 0) {
        message.charAt(0) + " " + formatMessage(message.substring(1), count+1)
      }
      else {
        message.charAt(0) + formatMessage(message.substring(1), count+1)
      }
    }
    else {
      message
    }
  }

  /**
   * Enciphers the input message.
   * 
   * @param cipherCoordinates A function to get the row and column of a character in the cipher.
   * @param charAt A function to get the cipher character at the given row and column.
   * @param message The message to encipher.
   * @return The enciphered message.
   */
  def encipherMessage(cipherCoordinates: Char => Tuple2[Int,Int], charAt: (Int,Int) => String, message:String):String = {  
    if (message.length > 0) {
      if (message.length == 1 || message.charAt(0) == message.charAt(1)) {
        val (firstRow, firstCol) = cipherCoordinates(message.charAt(0))
        val (secondRow, secondCol) = cipherCoordinates('x')

        if (firstRow == secondRow) {
          charAt(firstRow, firstCol + 1) + charAt(secondRow, secondCol + 1) + 
          encipherMessage(cipherCoordinates, charAt, message.substring(1)) 
        }
        else if (firstCol == secondCol) {
          charAt(firstRow + 1, firstCol) + charAt(secondRow + 1, secondCol) +
          encipherMessage(cipherCoordinates, charAt, message.substring(1))
        }
        else {
          charAt(firstRow, secondCol) + charAt(secondRow, firstCol) + 
          encipherMessage(cipherCoordinates, charAt, message.substring(1))
        }
      }
      else {
        val (firstRow, firstCol) = cipherCoordinates(message.charAt(0))
        val (secondRow, secondCol) = cipherCoordinates(message.charAt(1))
                  
        if (firstRow == secondRow) {
          charAt(firstRow, firstCol + 1) + charAt(secondRow, secondCol + 1) + 
          encipherMessage(cipherCoordinates, charAt, message.substring(2)) 
        }
        else if (firstCol == secondCol) {
          charAt(firstRow + 1, firstCol) + charAt(secondRow + 1, secondCol) +
          encipherMessage(cipherCoordinates, charAt, message.substring(2))
        }
        else {
          charAt(firstRow, secondCol) + charAt(secondRow, firstCol) + 
          encipherMessage(cipherCoordinates, charAt, message.substring(2))
        }                      
      }
    }
    else {
      message
    }
  }

  /**
   * Deciphers the input message.
   * 
   * @param cipherCoordinates A function to get the row and column of a character in the cipher.
   * @param charAt A function to get the cipher character at the given row and column.
   * @param message The message to decipher.
   * @return The deciphered message.
   */
  def decipherMessage(cipherCoordinates: Char => Tuple2[Int,Int], charAt: (Int,Int) => String, message:String):String = {
    if (message.length % 2 != 0) throw new MalformedMessageException

    if (message.length > 0) {
      val (firstRow, firstCol) = cipherCoordinates(message.charAt(0))
      val (secondRow, secondCol) = cipherCoordinates(message.charAt(1))
      
      if (firstRow == secondRow) {
        charAt(firstRow, firstCol - 1) + charAt(secondRow, secondCol - 1) +
        decipherMessage(cipherCoordinates, charAt, message.substring(2)) 
      }
      else if (firstCol == secondCol) {
        charAt(firstRow - 1, firstCol) + charAt(secondRow - 1, secondCol) +
        decipherMessage(cipherCoordinates, charAt, message.substring(2))
      }
      else {
        charAt(firstRow, secondCol) + charAt(secondRow, firstCol) + 
        decipherMessage(cipherCoordinates, charAt, message.substring(2))
      }
    }
    else {
      message
    }
  }
}

/** An exception to be thrown when the message to be deciphered cannot be deciphered. */
class MalformedMessageException extends Exception
