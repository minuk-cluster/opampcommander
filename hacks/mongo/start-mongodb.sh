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

# Run the MongoDB replica set initialization script
docker compose exec mongo1 mongosh -u root -p example --eval 'rs.initiate({ _id: "rs0", version: 1, members: [ { _id: 0, host: "mongo1:27017" }, { _id: 1, host: "mongo2:27018" }, { _id: 2, host: "mongo3:27019" } ] })'

# Print the uri for the MongoDB instance
echo "The MongoDB instances are running at the following URIs:"
echo "  mongodb://root:example@localhost:27017,localhost:27018,localhost:27019/commander?replicaSet=rs0&authSource=admin"