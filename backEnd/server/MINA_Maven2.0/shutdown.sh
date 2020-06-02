#!/bin/bash

netstat -anp | grep ":8770 " | awk -F" " '{print$7}'| awk '{split($0,arr,"/");print arr[1]}' | xargs kill
