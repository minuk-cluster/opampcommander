#!/usr/bin/env bash

# Start MongoDB

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

# Run MongoDB in a Docker container
docker compose up -d

# Print the uri for the MongoDB instance
echo "The MongoDB instances are running at the following URIs:"
echo "  mongodb://root:example@localhost:27017,localhost:27018,localhost:27019/commander?replicaSet=rs0&authSource=admin"