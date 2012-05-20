For some reason, attempting to run Eliza from the command line using
'scala -classpath bin Eliza'
does not work, due to some issue with the JFileChooser.  Please either run
Eliza using jEdit or with 'ant run.'  However, be aware that running through
ant causes I/O to be slightly jumbled.  The "You:" prompt doesn't show up until
after hitting enter.  (In other words, jEdit is preferred.)

The unit tests can be run with 'ant test,' as described in Yuri's readme.


Format of the rules file:

There three types of rules recognized by Eliza.  All rules must be on a single
line in the rules file.
1.  Pattern rules
These rules look like:
I want (.*) => Why do you want $x?

The left side matches the user input, and the right side provides a template for
the response.  On the left side, (.*) is used to capture the "topic" of the
user's input.  The $x in the response is replaced with the topic, after the
topic is processed for pronoun switching.

2.  Keyword rules
These rules look like:
love => Love is a complicated thing.

The left side contains a keyword or phrase, and the right side provides a
response to the keyword or phrase.  If no pattern rules match the user's input,
and the user's input matches at least one keyword rule, then a random keyword
response will be chosen.

3.  Generic rules
These rules look like:
I don't understand you.
What were you saying about $x?

These rules are used if the user's input did not match any pattern or keyword
rules.  If a generic rule contains $x, and if there are topics in memory, then a
random topic will be inserted into the generic rule to form a response.

There is a single fallback response, "I'm at a loss for words," provided by
Eliza.  This will be hit if the user's input did not match any keyword or
pattern rules, and either there are no generic rules or all of the generic rules
contain $x and no topics are in memory.
