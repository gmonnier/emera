package viewModel.analyses.preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

@XmlRootElement
public class ViewDataSplitterModel {
	
	private static Logger LOG = Log4JLogger.logger;

	private String regexp;

	private String outputName;

	private int associatedSequencesCount;

	public ViewDataSplitterModel() {

	}

	public ViewDataSplitterModel(String regexp, String outputFullPath) {
		this.regexp = regexp;
		this.outputName = outputFullPath;
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

}
