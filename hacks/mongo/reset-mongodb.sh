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

## Stop MongoDB
./hacks/mongo/stop-mongodb.sh

# Reset MongoDB
## Delete mongodb data
rm -rf hacks/mongo/data

## Delete the docker container
docker compose rm -f -v