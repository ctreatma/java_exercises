#!/bin/sh

java_home_not_set()
{
  echo "JAVA_HOME is not set to Java Development Kit";
}

if [ ! "$JAVA_HOME" ]; then
  java_home_not_set;
  exit;
fi

#target=:
#if [ ! "$1" ]; then
if [ "$1" != "" ]; then
  target=$1  
else
  target="build"
fi

_CP_=./lib
_CP_=${_CP_}:./lib/ant.jar
#_CP_=${_CP_}:./lib/optional.jar
_CP_=${_CP_}:./lib/xercesImpl.jar
_CP_=${_CP_}:./lib/xml-apis.jar
${JAVA_HOME}/bin/java -Xmx512M -cp "${JAVA_HOME}/lib/tools.jar:${_CP_}" org.apache.tools.ant.Main "$target"
