package viewModel.report;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewGraphData {
	
	private int index;
	
	private String name;
	
	private double value;
	
	private ViewGraphData(){
	}

	public ViewGraphData(int index, double value, String name) {
		this.index = index;
		this.value = value;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}
