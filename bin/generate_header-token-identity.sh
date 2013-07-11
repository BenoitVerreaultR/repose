##################
# general config #
##################

OUTPUT="config/header-token-identity.cfg.xml"

# sql configs (SQL_HOST, SQL_USER, SQL_PASS) should be configured in the profile of the ec2-user for safety

# file templates
TOP="<header-token-identity xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n\txmlns='http://docs.api.rackspacecloud.com/repose/header-token-identity/v1.0'\n\txsi:schemaLocation='http://docs.api.rackspacecloud.com/repose/header-token-identity/v1.0 ../config/header-token-identity-configuration.xsd'>\n\n\t<headers>";
BOT="\t</headers>\n\n\t<token-header>X-TOKEN</token-header>\n\n</header-token-identity>\n"
HEADER="\t\t<header id=\"{1}\" group=\"{1}\" token=\"{2}\" />"

SQL="select distinct p.name, p.api_key from riotapi.policy as p, riotapi.policy_method as pm, riotapi.method as m where p.id=pm.policy_methods_id and pm.method_id=m.id and m.api_id in (60,61,68);"
RESULT=`mysql -h $SQL_HOST -u $SQL_USER --password=$SQL_PASS -e "$SQL" -N`
GROUP_DATA=($RESULT)

echo -e $TOP > $OUTPUT

for (( i=0; i<${#GROUP_DATA[@]}; i+=2 ))
do  
    NAME=${GROUP_DATA[i]}
    KEY=${GROUP_DATA[i+1]}
    echo -e $HEADER | sed "s/{1}/$NAME/g" | sed "s/{2}/$KEY/g" >> $OUTPUT  
done

echo -e $BOT >> $OUTPUT
