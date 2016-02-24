package coreprocessing.fastQReaderSplitter;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public class DataSplitterModel {
	
	private static Logger LOG = Log4JLogger.logger;
	
	private Pattern pattern;
	
	private String outputName;
	
	private String alias;
	
	private BufferedWriter writter;
	
	private int associatedSequencesCount;

	public DataSplitterModel(String regexp, String outputName, String alias) {
		this.pattern = Pattern.compile(regexp);
		this.outputName = outputName;
		this.alias = alias;
		this.associatedSequencesCount = 0;
		/*try {
			writter = new BufferedWriter(new FileWriter(outputName));
		} catch (IOException e) {
			LOG.error("Unable to create buffered writer for data splitter on : " + outputName);
		}*/
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getOutputName() {
		return outputName;
	}
	
	public BufferedWriter getWritter() {
		return writter;
	}

	public int getAssociatedSequencesCount() {
		return associatedSequencesCount;
	}

	public void setAssociatedSequencesCount(int associatedSequencesCount) {
		this.associatedSequencesCount = associatedSequencesCount;
	}

	public boolean fitPattern(String input) {
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	public void incrementSequenceCount() {
		associatedSequencesCount++;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	
}
