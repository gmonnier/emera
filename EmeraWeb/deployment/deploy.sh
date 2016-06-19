#!/bin/bash

# Configuration building script
echo "Deploy EmeraWeb on remote server"

instance=ec2-52-201-216-121.compute-1.amazonaws.com


read -p "Provide instance IP or ID?" instance

if [ -z "$instance" ]; then
	instance="54.227.227.221"
fi

keyFile=~/Work_Area/Emera/Emera_WS/EmeraWeb/deployment/awskey/EmeraWebKey.pem

echo "Configuring instance prior to deployment - $instance ..."
ssh ubuntu@$instance -i ${keyFile} -y 'bash -s' < setup.sh

echo "Deploying Emera application on $instance ..."
scp -i ${keyFile} -r ../conf ubuntu@${instance}:/EmeraWeb
scp -i ${keyFile} executable/EmeraWeb.jar ubuntu@${instance}:/EmeraWeb
scp -i ${keyFile} -r ../src/main/webapp/resources ubuntu@${instance}:/EmeraWeb/src/main/webapp