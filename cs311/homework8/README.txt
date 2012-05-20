Charles Treatman
CS 311 Homework 8

This program runs on the Linux workstations.  I got it to run on occs
on a Windows machine with Xwin 32 running, but when I try to run it on
occs when I'm on Linux, I get an error regarding X11 not being able to
find the monitor.  I'm pretty sure this is not my fault.  When running
on Windows or OCCS, though, the program seems to screw up my GUI
sometimes, placing the components differently than when the program
runs on Linux.  I have tried to rectify the situation, but this is
admittedly the first time I've worked with Java since 150/151, so I'm
more than a little rusty.

This GUI is in my opinion a bit hackish, but it does demonstrate the
use of JDBC, even if it has not lived up to the glory of my original
design.

To compile and run, assuming correct placement of JDBC classes:
javac homework8.java
java homework8

In the Fussers panel, you can input first name and/or last name and
all possible matches will be displayed in the output window.  In the
vacancies panel, simply put the number of rooms you are looking for
and press submit.  IN the Add Student panel you must fill in all
fields--this means that to create an off-campus student you must
manually enter "null" in the boxes for dorm and room. IN the lease and
ownership panels, you must provide BOTH the Tnumber/SSN and street,
otherwise you will get an error message.

Enjoy.