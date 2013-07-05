pid=`ps aux | grep repose | grep sudo | awk {'print $2'}`
sudo kill -2 $pid &> /dev/null
sudo kill -2 $pid &> /dev/null
sudo kill -2 $pid &> /dev/null
sleep 1s
sudo kill -15 $pid &> /dev/null
sleep 1s
sudo kill -9 $pid &> /dev/null
