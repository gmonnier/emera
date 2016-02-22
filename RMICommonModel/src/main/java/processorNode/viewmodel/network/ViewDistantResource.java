package processorNode.viewmodel.network;

import javax.xml.bind.annotation.XmlRootElement;

import com.gmo.network.location.ClientLocation;

@XmlRootElement
public class ViewDistantResource {

	private String IP;
	
	private String name;
	
	private String status;
	
	private ClientLocation location;
	
	private ViewDistantResource() {
	}

	public ViewDistantResource(String iP, String name, String status, ClientLocation location) {
		super();
		IP = iP;
		this.name = name;
		this.status = status;
		this.location = location;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ClientLocation getLocation() {
		return location;
	}

	public void setLocation(ClientLocation location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "ViewDistantResource [IP=" + IP + ", name=" + name + ", status=" + status + ", location=" + location + "]";
	}
}
