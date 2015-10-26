#!/bin/bash

if [ "$#" -ne 1 ]; then
    	echo "script takes json file as an argument"
	exit 1;
fi

#curl -X POST -H "Content-Type: application/json" 10.175.203.123:8080/v2/apps -d@"$@"
curl -X POST -H "Content-Type: application/json" 10.10.115.48:8080/v2/apps -d@"$@"
