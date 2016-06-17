#!/bin/bash

sudo su -

echo "Cleanup Emera deployment directory"
deploymentDir=/EmeraWeb
if [ -d ${deploymentDir} ]; then
	rm -rf ${deploymentDir}
fi
mkdir ${deploymentDir}

java_install_needed=false
if type -p java; then
    echo "found java executable in PATH"
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "found java executable in JAVA_HOME"     
    _java="$JAVA_HOME/bin/java"
else
	java_install_needed=true
    echo "no java executable found"
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" > "1.7" ]]; then
        echo "version is more than 1.7"
    else         
    	java_install_needed=true
        echo "version is less or equal than 1.7"
    fi
fi

if [ "$java_install_needed" = true ] ; then
	echo "Update apt repositories..."
	sudo add-apt-repository ppa:webupd8team/java
	sudo apt-get update
	
	echo "Install java..."
	echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
	apt-get install -y oracle-java8-installer
fi

mkdir -p ${deploymentDir}/conf
mkdir -p ${deploymentDir}/logs

chown ubuntu:ubuntu ${deploymentDir}
chown ubuntu:ubuntu ${deploymentDir}/conf
chown ubuntu:ubuntu ${deploymentDir}/logs

chmod -R 777 ${deploymentDir}