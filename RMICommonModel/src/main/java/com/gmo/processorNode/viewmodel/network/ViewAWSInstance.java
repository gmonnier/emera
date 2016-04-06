package com.gmo.processorNode.viewmodel.network;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewAWSInstance implements Serializable{

	private String id;

	private String status;
	
	private String privateIP;
	
	private String publicIP;
	
	private String instanceType;

	private ViewAWSInstance() {
	}

	public ViewAWSInstance(String id, String privateIP, String publicIP, String status, String instanceType) {
		super();
		this.id = id;
		this.privateIP = privateIP;
		this.status = status;
		this.instanceType = instanceType;
		this.publicIP = publicIP;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrivateIP() {
		return privateIP;
	}

	public void setPrivateIP(String privateIP) {
		this.privateIP = privateIP;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getPublicIP() {
		return publicIP;
	}

	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}

}
