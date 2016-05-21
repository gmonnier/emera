Emera <img src="https://cloud.githubusercontent.com/assets/11952499/13207451/544a2f2c-d8c3-11e5-821c-9512e11ee8ad.png" width="350" align="right"> 
====================
**DNA Sequences analysis platform**
 

Purpose of the platform
---------------------

Provide scientists a way to connect to their Illumina BaseSpace account.
Retrieve, and start lookup analyses on these data by leveraging nowadays technologies such as parrallel processing. One kind of analysis is offered today : subsequences lookup (by pattern definition) and generation of various occurences reports on the genes. 
This project provide a highly scalable, fault tolerant system based on Amazon Web Services, as well as Physical machines to perform the computations. The client app is available amongst the projects.

Current status info
---------------------

+ Yet, deployment on [Emera](http://www.gilles-monnier.com:8082) Emiting on port 8082. Be sure your firewall allow connections to this port.
+ Contact gilles_monnier@yahoo.fr for detailed info.

High level architecture
---------------------

![highleveldiagramm](https://cloud.githubusercontent.com/assets/11952499/13207296/afd79926-d8c1-11e5-8110-e8e0bbbf3a1e.png)

Development setup
---------------------

+ Installation of JDK/JRE

[JDK downloads](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

+ Installation of Eclipse

[Eclipse downloads](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars1)

+ Installation of Maven plugin for eclipse

Directly from the eclipse plugin manager, enter the following url : http://download.eclipse.org/technology/m2e/releases

+ Installation of assets pipeline
```
$ npm install --global gulp
$ npm install --save-dev gulp gulp-concat
$ npm install -D gulp-uglify
$ npm install -D gulp-ng-annotate
$ npm install --save-dev gulp-clean
```

+ Run ```gulp js``` command line to precompile js assets
+ Run ```gulp watch``` command line to run watcher service on javascript files
