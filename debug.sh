#!/bin/bash
mvn install
ADDRESS="8000"
PORT="8080"
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address="$ADDRESS",suspend=n"
mvn hpi:run -Djetty.port=$PORT
