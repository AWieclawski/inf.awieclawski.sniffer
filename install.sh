#!/bin/bash

echo "About to build docker using Dockerfile in ${PWD}" 
docker build -t sniffer-jdk11 .
echo "About to run docker as detached container" 
docker run -d --name sniffer  -p 3333:8080 sniffer-jdk11
echo "Presentation of active dockers"
sudo docker ps
echo "Sniffer WeClient available at: http://localhost:3333/dtos"
