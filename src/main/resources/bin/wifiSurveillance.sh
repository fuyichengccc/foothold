#!/bin/sh

basepath=$(cd `dirname $0`; pwd)
#设置kafka版本为0.10否则会报错

function main {
    export SPARK_KAFKA_VERSION=0.10
    spark2-submit \
    --executor-cores 1 \
    --executor-memory 1G \
    --driver-memory 1G \
    --conf spark.default.parallelism=25 \
    --class com.jinp.videobigdata.surveillance.WifiSurveillanceApp \
    --conf 'spark.driver.extraJavaOptions=-Dlog4j.configuration=file:/home/whs/surveillance/conf/log4j-wifiSurveillance.properties'\
    --conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
    --conf 'spark.yarn.executor.memoryOverhead=4096' \
    ${basepath}/../lib/jp-whs-surveillance-1.0-jar-with-dependencies.jar
}

function start {
    echo wifi_surveillance starting......
    nohup ${basepath}/wifiSurveillance.sh main > /dev/null 2>&1 &
}

function stop {
    echo wifi_surveillance stoping......
    ps aux | grep  com.jinp.videobigdata.surveillance.WifiSurveillanceApp |grep -v grep| awk '{print $2}'| xargs kill -9
}

# check arguments
TYPE=$1

case ${TYPE} in
    "start")
        start
        ;;
    "stop")
        stop
        ;;
    "main")
        main
        ;;
    "restart")
        stop
        start
        ;;
    "")
        echo "require a command"
        ;;
esac