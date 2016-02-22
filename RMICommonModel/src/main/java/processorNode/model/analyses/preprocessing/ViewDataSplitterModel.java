package processorNode.model.analyses.preprocessing;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

@XmlRootElement
public class ViewDataSplitterModel {
	
	private static Logger LOG = Log4JLogger.logger;

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

	public ViewDataSplitterModel(DataSplitterModel splitterPattern) {
		this.regexp = splitterPattern.getPattern().toString();
		this.outputName = splitterPattern.getOutputName();
		this.alias = splitterPattern.getAlias();
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
