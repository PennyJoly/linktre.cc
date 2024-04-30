#!/bin/bash
echo "开始停止进程 : site.jar"
pid=`ps -ef |grep java|grep site.jar|awk '{print $2}'`
echo '停止旧进程 pid为:'$pid
if [ -n "$pid" ]
then
kill -9 $pid
fi
echo "开始执行linktre.cc部署脚本"
cd /home/service/site/
export BUILD_ID=dontkillme
nohup java -jar site.jar -Xms64m -Xmx128m -XX:PermSize=128m -XX:MaxPermSize=256m -XX:MaxNewSize=256m --spring.profiles.active=dev --server.port=8090 > nohup.out 2>&1 &
echo "linktre.cc部署完成"
tail -f nohup.out
