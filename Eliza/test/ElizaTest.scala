import org.scalatest.FunSuite
import Eliza._

class ElizaTest extends FunSuite {
  
  test("strips problematic punctuation") {
    expect(true) {
      stripPunctuation('.') == ' '
    }
    expect(true) {
      stripPunctuation('?') == ' '
    }
    expect(true) {
      stripPunctuation('[') == ' '
    }
    expect(true) {
      stripPunctuation(']') == ' '
    }
    expect(true) {
      stripPunctuation(',') == ','
    }
    expect(true) {
      stripPunctuation('\'') == '\''
    }
    expect(true) {
      stripPunctuation('c') == 'c'
    }
  }

  test("finds topic if present") {
    expect(true) {
      getTopic("(.*) she said", "That's what she said") == "That's what"
    }
    intercept[MatchError] {
      getTopic("No match", "Different string")
    }
  }

  test("matches properly") {
    expect(true) {
      matches("(.*) she said", "That's what she said")
    }
    expect(false) {
      matches("No match", "Different string")
    }
  }
}
