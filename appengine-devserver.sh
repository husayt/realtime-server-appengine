export DEV_SERVER=$HOME/.m2/repository/com/google/appengine/appengine-java-sdk/1.8.3/appengine-java-sdk/appengine-java-sdk-1.8.3/bin/dev_appserver.sh
chmod 770 $DEV_SERVER
$DEV_SERVER \
  --jvm_flag=-Dfile.encoding=UTF-8 \
  --disable_update_check --address=0.0.0.0 \
  target/realtime-server-appengine-0.3.0 \
  2>&1 | tee target/realtime-server-appengine-0.3.0/log.txt

##  --jvm_flag=-Xmx4096m --jvm_flag=-Xms2048m