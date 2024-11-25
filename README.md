# Sniffer with configuration by API

Reactive client request url by scheduler. Can be updated by its own REST API after request: 

`http://localhost:3333/dto/add` POST

Request data check:

`http://localhost:3333/dtos` GET

API supports also response error handling. 


## Requirements:

 - JDK 11 
 -- vide: `https://itslinuxfoss.com/how-to-fix-the-java-command-not-found-in-linux/`

 - Maven 
 -- vide: `https://linuxhint.com/mvn-command-found/`

 - Docker 
 -- vide: `https://docs.docker.com/get-docker/`

 - Linux OS (Ubuntu)

 - IDE for JDK with Lombok and Spring plugins 
 -- vide: `https://www.eclipse.org/downloads/packages/`
   (ex. standalone "Eclipse IDE for Enterprise Java and Web Developers" with soft link on the desktop) 
 -- vide: `https://www.baeldung.com/lombok-ide`
 

## Run:

1. Clone repository using: 
```
	$ git clone <repository-url> 
```
2. Get in main `pom.xml` directory and type in CLI:   
```
	$ chmod 755 install.sh  
	$ sudo ./install.sh
```
and docker container `sniffer` provide its own API, when running. 

 
Re-use docker container after installation: 
```
	$ docker start sniffer
``` 
Delete container: 
```
	$ docker rm sniffer
``` 


## Examples of:

- useful end-points:

 GET `http://localhost:3333/dtos`

 POST `http://localhost:3333/dtos` with BODY `[{"sniffedAddress": "https://www.baeldung.com/", "sniffActive": true,
 "pathVariables": ["spring-task-scheduler","cron-expressions","java-cron-expressions-wildcards-diff" ]}]`

GET `http://localhost:3333/cron`

POST `http://localhost:3333/cron` with BODY `{"expression":  "0 */3 * * * *"}`

