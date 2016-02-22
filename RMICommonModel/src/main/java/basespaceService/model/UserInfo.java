package basespaceService.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String id;
	
	// Needed for JAXB JSON encoder
	public UserInfo() {}
	
	public UserInfo(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
