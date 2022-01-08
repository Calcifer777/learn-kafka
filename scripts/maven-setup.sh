#!/bin/bash - 

set -o nounset                              # Treat unset variables as an error

mvn archetype:generate -DgroupId=com.calcifer -DartifactId=kafka-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false

