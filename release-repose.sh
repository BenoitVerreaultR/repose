#!/bin/bash

HOST=10.96.1.46
EAR=./project-set/components/filter-bundle/target/filter-bundle-2.7.1-SNAPSHOT.ear
JAR=./project-set/core/valve/target/repose-valve.jar
DEPLOY_FOLDER=/srv/repose
SSH=~/.ssh/mroot

FULL_SSH=`ls $SSH`

rsync -ave "ssh -i $FULL_SSH" ./bin/*.sh ec2-user@$HOST:$DEPLOY_FOLDER/
rsync -ave "ssh -i $FULL_SSH" ./config/*.xml ec2-user@$HOST:$DEPLOY_FOLDER/config/
rsync -ave "ssh -i $FULL_SSH" ./config/*.properties ec2-user@$HOST:$DEPLOY_FOLDER/config/
rsync -ave "ssh -i $FULL_SSH" ./newrelic/* ec2-user@$HOST:$DEPLOY_FOLDER/newrelic/
rsync -ave "ssh -i $FULL_SSH" $EAR ec2-user@$HOST:$DEPLOY_FOLDER/
rsync -ave "ssh -i $FULL_SSH" $JAR ec2-user@$HOST:$DEPLOY_FOLDER/

ESCAPED_DEPLOY_FOLDER=`echo $DEPLOY_FOLDER | sed 's/\//\\\\\//g'`

ssh -i $SSH ec2-user@$HOST "
mv $DEPLOY_FOLDER/config/system-model.cfg.xml $DEPLOY_FOLDER/config/_system-model.cfg.xml;
sed 's/hostname=\"localhost\" http-port=\"8080\"/hostname=\"localhost\" http-port=\"80\"/g' $DEPLOY_FOLDER/config/_system-model.cfg.xml > $DEPLOY_FOLDER/config/system-model.cfg.xml;
rm -f $DEPLOY_FOLDER/config/_system-model.cfg.xml;

mv $DEPLOY_FOLDER/config/container.cfg.xml $DEPLOY_FOLDER/config/_container.cfg.xml;
sed 's/\/var\/repose/$ESCAPED_DEPLOY_FOLDER/g' $DEPLOY_FOLDER/config/_container.cfg.xml > $DEPLOY_FOLDER/config/container.cfg.xml;
rm -f $DEPLOY_FOLDER/config/_container.cfg.xml;

mv $DEPLOY_FOLDER/config/container.cfg.xml $DEPLOY_FOLDER/config/_container.cfg.xml;
sed 's/\/usr\/share\/repose\/filters/$ESCAPED_DEPLOY_FOLDER/g' $DEPLOY_FOLDER/config/_container.cfg.xml > $DEPLOY_FOLDER/config/container.cfg.xml;
rm -f $DEPLOY_FOLDER/config/_container.cfg.xml;

mv $DEPLOY_FOLDER/config/http-logging.cfg.xml $DEPLOY_FOLDER/config/_http-logging.cfg.xml;
sed 's/\/var\/log\/repose/$ESCAPED_DEPLOY_FOLDER\/logs/g' $DEPLOY_FOLDER/config/_http-logging.cfg.xml > $DEPLOY_FOLDER/config/http-logging.cfg.xml;
rm -f $DEPLOY_FOLDER/config/_http-logging.cfg.xml;

mv $DEPLOY_FOLDER/config/log4j.properties $DEPLOY_FOLDER/config/_log4j.properties;
sed 's/\/var\/log\/repose/$ESCAPED_DEPLOY_FOLDER\/logs/g' $DEPLOY_FOLDER/config/_log4j.properties > $DEPLOY_FOLDER/config/log4j.properties;
rm -f $DEPLOY_FOLDER/config/_log4j.properties;

chmod 0644 $DEPLOY_FOLDER/config/*.xml;
chmod 0644 $DEPLOY_FOLDER/config/*.properties;
chmod 0755 $DEPLOY_FOLDER/*.jar;
chmod 0755 $DEPLOY_FOLDER/*.ear;
chmod 0755 $DEPLOY_FOLDER/*.sh;
"
