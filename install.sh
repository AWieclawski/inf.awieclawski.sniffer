#!/bin/bash
# https://www.baeldung.com/docker-maven-build-multi-module-projects
echo "About to build docker using Dockerfile in ${PWD}" 
docker build -t jdk11-sniffer:from-dockerfile .
echo "About to run docker as detached container" 
docker run -d --name sniffer_3456 -p 3456:8080 jdk11-sniffer:from-dockerfile
echo "Presentation of active dockers"
sudo docker ps
echo "Sniffer WeClient available at: http://localhost:3333/dtos"
