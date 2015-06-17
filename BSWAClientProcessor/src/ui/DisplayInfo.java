package ui;

import java.util.ArrayList;
import java.util.List;

public class DisplayInfo {
	
	private List<Info> listinfos;

	public DisplayInfo() {
		listinfos = new ArrayList<Info>();
	}

	public List<Info> getListinfos() {
		return listinfos;
	}

	public void setListinfos(List<Info> listinfos) {
		this.listinfos = listinfos;
	}
	
	
}
