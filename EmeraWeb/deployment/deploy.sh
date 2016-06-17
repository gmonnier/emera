#!/bin/bash

# Configuration building script
echo "Deploy EmeraWeb on remote server"

instance=ec2-52-201-216-121.compute-1.amazonaws.com


read -p "Provide instance IP or ID?" instance

if [ -z "$instance" ]; then
	instance="54.197.5.159"
fi

keyFile=~/Work_Area/Emera/Emera_WS/EmeraWeb/deployment/awskey/EmeraWebKey.pem

echo "Configuring instance prior to deployment - $instance ..."
ssh ubuntu@$instance -i ${keyFile} -y 'bash -s' < setup.sh

echo "Deploying Emera application on $instance ..."
scp -r -i ${keyFile} ../conf ${instance}:/EmeraWeb/
scp -i ${keyFile} ../executable/EmeraWeb.jar ${instance}:/EmeraWeb/