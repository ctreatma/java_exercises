#!/bin/sh

usage()
{
  echo "Usage: p2p.sh <bootstrap node IP address> <bootstrap node port> <data store directory> [<gateway port>]";
}

java_home_not_set()
{
  echo "JAVA_HOME is not set to Java Development Kit";
}

if [ ! "$JAVA_HOME" ]; then
  java_home_not_set;
  exit;
fi

bootstrap_ip=
bootstrap_port=
data_store=
gateway_port=
if [ ! "$1" ]; then
  usage;
  exit;
elif [ ! "$2" ]; then
  usage;
  exit;
elif [ ! "$3" ]; then
  usage;
  exit;
else
  bootstrap_ip=$1
  bootstrap_port=$2
  data_store=$3
  if [ ! "$4" ]; then
    java -cp target/WEB-INF/lib/cis-555.jar:target/WEB-INF/lib/FreePastry-2.1.zip:target/WEB-INF/lib/hadoop-0.20.2-core.jar:target/WEB-INF/lib/htmlparser.jar:target/WEB-INF/lib/je-4.0.92.jar:target/WEB-INF/lib/servlet-api.jar edu.upenn.cis555.mustang.peer.P2PServer "$bootstrap_ip" "$bootstrap_port" "${data_store}"
  else
    gateway_port=$4
    java -cp target/WEB-INF/lib/cis-555.jar:target/WEB-INF/lib/FreePastry-2.1.zip:target/WEB-INF/lib/hadoop-0.20.2-core.jar:target/WEB-INF/lib/htmlparser.jar:target/WEB-INF/lib/je-4.0.92.jar:target/WEB-INF/lib/servlet-api.jar edu.upenn.cis555.mustang.peer.P2PServer "$bootstrap_ip" "$bootstrap_port" "${data_store}" "$gateway_port"
  fi
fi

