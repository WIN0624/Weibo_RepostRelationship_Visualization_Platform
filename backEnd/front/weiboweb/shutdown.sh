#!/bin/bash
PORT=$(netstat -ntulp | grep 9293)
# 如果PORT不为空说明有程序占用该端口号
if [[ -n $PORT ]]; then
        echo "The server is open, ready to close"
        RESULT="000"
        while [[ $RESULT == "000" ]]; do
                echo "Closing down ..."
                # 优雅关闭springboot项目
                RESULT=$(curl -X POST 127.0.0.1:12581/MyActuator/shutdown)
                sleep 2
        done
        echo "Close off success"
fi
echo "Being started ..."