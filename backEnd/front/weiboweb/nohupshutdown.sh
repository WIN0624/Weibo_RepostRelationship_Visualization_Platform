#!/bin/bash
PORT=$(netstat -ntulp | grep 8288)
if [[ -n $PORT  ]]; then
	echo "ready to close..."
	RESULT="000"
	while [[ $RESULT == "000" ]]; do
		echo "closing down..."
		RESULT=$(curl -X POST 127.0.0.1:12581/MyActuator/shutdown)
		sleep 2
	done
	echo "close off success"
fi
