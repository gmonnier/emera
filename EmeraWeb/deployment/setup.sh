#!/bin/bash

sudo su -

echo "Cleanup Emera deployment directory"
deploymentDir=/EmeraWeb
if [ -d ${deploymentDir} ]; then
	rm -rf ${deploymentDir}
fi
mkdir ${deploymentDir}

echo "Update apt repositories..."
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update

echo "Install java..."
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
apt-get install -y oracle-java8-installer

mkdir -p /EmeraWeb/conf