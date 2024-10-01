#!/usr/bin/env bash

# Stop MongoDB

# Find the path for mongodb data
## get current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
## move to the root directory if it is in the hacks or mongo directory
if [[ $DIR == *"/hacks" ]]; then
  cd ../
elif [[ $DIR == *"/mongo" ]]; then
  cd ../../
fi

cd hacks/mongo

# Stop MongoDB in a Docker container
docker compose down