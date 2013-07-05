##################
# general config #
##################

OUTPUT="config/uri-access.cfg.xml"

# regions must be listed as a regex capture group ex(na|euw|eune)
REGIONS="(na|euw|eune)"

# sql configs (SQL_HOST, SQL_USER, SQL_PASS) should be configured in the profile of the ec2-user for safety

# file templates
TOP="<uri-access xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n\txmlns=\"http://docs.api.rackspacecloud.com/repose/uri-access/v1.0\"\n\txsi:schemaLocation='http://docs.api.rackspacecloud.com/repose/uri-access/v1.0 ../config/uri-access-configuration.xsd'>";
BOT="\n</uri-access>"
GROUP_BEGIN="\n\t<access-group id=\"{1}\" group=\"{1}\" default=\"false\">"
GROUP_END="\t</access-group>\n"
ACCESS="\t\t<access uri=\"{1}\" uri-regex=\"{2}\" http-methods=\"{3}\" />"

SQL="select distinct p.id, p.name from riotapi.policy as p, riotapi.policy_method as pm, riotapi.method as m where p.id=pm.policy_methods_id and pm.method_id=m.id and m.api_id in (60,61,68);"
RESULT=`mysql -h $SQL_HOST -u $SQL_USER --password=$SQL_PASS -e "$SQL" -N`
GROUP_DATA=($RESULT)

echo -e $TOP > $OUTPUT

for (( i=0; i<${#GROUP_DATA[@]}; i+=2 ))
do
    ID=${GROUP_DATA[i]}
    NAME=${GROUP_DATA[i+1]}
    
    echo -e $GROUP_BEGIN | sed "s/{1}/$NAME/g" >> $OUTPUT
    
    SQL="select m.path, m.http_method from riotapi.policy_method as pm, riotapi.method as m where pm.method_id=m.id and m.api_id in (60,61,68) and pm.policy_methods_id=$ID;"
    RESULT=`mysql -h $SQL_HOST -u $SQL_USER --password=$SQL_PASS -e "$SQL" -N`
    URI_DATA=($RESULT)
    
    for (( j=0; j<${#URI_DATA[@]}; j+=2 ))
    do
        URI=`echo ${URI_DATA[j]} | sed 's/\//\\\\\//g'`
        METHOD=${URI_DATA[j+1]}
        
        #transform URI to REGEX
        REGEX=`echo $URI | sed "s/{region}/$REGIONS/g" | sed "s/{[a-zA-Z0-9]*}/.*/g"`
        
        echo -e $ACCESS | sed "s/{1}/$URI/g" | sed "s/{2}/$REGEX/g" | sed "s/{3}/$METHOD/g" >> $OUTPUT
        
    done
    
    echo -e $GROUP_END >> $OUTPUT
    
done

echo -e $BOT >> $OUTPUT