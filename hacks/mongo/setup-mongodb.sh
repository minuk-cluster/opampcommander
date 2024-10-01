#!/usr/bin/env bash

# Find the path for mongodb data
## get current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
## move to the root directory if it is in the hacks or mongo directory
if [[ $DIR == *"/hacks" ]]; then
  cd ../
elif [[ $DIR == *"/mongo" ]]; then
  cd ../../
fi

## set the path for mongodb data
MONGODB_DATA_PATH=$PWD/hacks/mongo/data
MONGODB_PKI_PATH=$MONGODB_DATA_PATH/pki
## create the directory if it does not exist
mkdir -p $MONGODB_PKI_PATH

openssl rand -base64 741 > $MONGODB_PKI_PATH/keyfile
chmod 0400 $MONGODB_PKI_PATH/keyfile