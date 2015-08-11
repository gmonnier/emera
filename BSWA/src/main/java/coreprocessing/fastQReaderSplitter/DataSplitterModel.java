package coreprocessing.fastQReaderSplitter;

public class DataSplitterModel {
	
	private String pattern;
	
	private String outputName;

	public DataSplitterModel(String pattern, String outputName) {
		this.pattern = pattern;
		this.outputName = outputName;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
	
	
}
