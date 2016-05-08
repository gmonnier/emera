package com.gmo.processorNode.viewmodel.analyses.preprocessing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewDataSplitterModel implements Serializable {

	private String regexp;

	private String outputName;

	private String alias;

	public ViewDataSplitterModel() {
	}

	public ViewDataSplitterModel(String regexp, String outputFullPath, String alias) {
		this.regexp = regexp;
		this.outputName = outputFullPath;
		this.alias = alias;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
