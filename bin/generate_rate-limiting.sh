##################
# general config #
##################

OUTPUT="config/rate-limiting.cfg.xml"

# sql configs (SQL_HOST, SQL_USER, SQL_PASS) should be configured in the profile of the ec2-user for safety

# file templates
TOP="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<rate-limiting datastore=\"distributed/hash-ring\" use-capture-groups=\"true\" overLimit-429-responseCode=\"true\" xmlns=\"http://docs.rackspacecloud.com/repose/rate-limiting/v1.0\">\n\n\t<request-endpoint uri-regex=\"/api/limits/\" />";
BOT="\n\t<limit-group id=\"User_Standard\" groups=\"User_Standard\" default=\"true\">\n\t\t<limit uri=\"/*\" uri-regex=\"(/).*\" http-methods=\"ALL\" unit=\"SECOND\" value=\"0\" />\n\t</limit-group>\n\n</rate-limiting>\n"
GROUP="\n\t<limit-group id=\"{1}\" groups=\"{1}\" default=\"false\">\n\t\t<limit uri=\"/*\" uri-regex=\"(/).*\" http-methods=\"ALL\" unit=\"SECOND\" value=\"50\"/>\n\t</limit-group>"

SQL="select distinct p.name from riotapi.policy as p, riotapi.policy_method as pm, riotapi.method as m where p.id=pm.policy_methods_id and pm.method_id=m.id and m.api_id in (60,61,68);"
RESULT=`mysql -h $SQL_HOST -u $SQL_USER --password=$SQL_PASS -e "$SQL" -N`
GROUP_DATA=($RESULT)

echo -e $TOP > $OUTPUT

for (( i=0; i<${#GROUP_DATA[@]}; i++ ))
do  
    NAME=${GROUP_DATA[i]}
    echo -e $GROUP | sed "s/{1}/$NAME/g" >> $OUTPUT  
done

echo -e $BOT >> $OUTPUT