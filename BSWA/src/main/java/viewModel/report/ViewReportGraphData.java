package viewModel.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewReportGraphData {

	private List<ViewGraphData> listData;
	
	public ViewReportGraphData() {
		listData = new ArrayList<>();
	}

	public List<ViewGraphData> getListData() {
		return listData;
	}

	public void setListData(List<ViewGraphData> listData) {
		this.listData = listData;
	}
	
	public void addDataToGraph(int index, double value, String name) {
		listData.add(new ViewGraphData(index, value, name));
	}
	
}
