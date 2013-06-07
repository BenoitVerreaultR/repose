#!/bin/bash

sudo \
java -jar \
     -Xms2g \
     -Xmx2g \
     -XX:MaxPermSize=256m \
     -D-XX:NewSize=512m \
     -XX:+UseConcMarkSweepGC \
     -XX:+CMSParallelRemarkEnabled \
     -XX:CMSInitiatingOccupancyFraction=70 \
     -XX:+UseCMSInitiatingOccupancyOnly \
     -XX:+HeapDumpOnOutOfMemoryError \
     -Dnewrelic.environment=test \
     -javaagent:newrelic/newrelic.jar \
     /srv/repose/repose-valve.jar \
     START \
     -s 8188 \
     -c /srv/repose/config \
     &> /dev/null &
