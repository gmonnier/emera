package model.parameters;

import java.io.Serializable;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import util.StringSerializable;
import util.StringSerializationException;

public class PartialProcessConfiguration implements Serializable, StringSerializable {

	// The selected extraction pattern
	protected ExtractionPattern pattern;
	
	// Pattern attributes
	protected PatternAttributes patternAttributes;
	
	// Output configuration
	protected OutputAttributes outputAttributes;
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	private StringBuilder sb = new StringBuilder();
	
	public PartialProcessConfiguration() {
		
	}

	public ExtractionPattern getPattern() {
		return pattern;
	}

	public void setPattern(ExtractionPattern pattern) {
		this.pattern = pattern;
	}

	public PatternAttributes getPatternAttributes() {
		return patternAttributes;
	}

	public void setPatternAttributes(PatternAttributes patternAttributes) {
		this.patternAttributes = patternAttributes;
	}

	public OutputAttributes getOutputAttributes() {
		return outputAttributes;
	}

	public void setOutputAttributes(OutputAttributes outputAttributes) {
		this.outputAttributes = outputAttributes;
	}


	@Override
	public String toString() {
		return "ProcessConfiguration -----> \n pattern=" + pattern + "\npatternAttributes=" + patternAttributes + "\noutputAttributes=" + outputAttributes + "]";
	}
	

	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		sb.append(pattern.getObjectAsString());
		sb.append("##");
		sb.append(patternAttributes.getObjectAsString());
		sb.append("##");
		sb.append(outputAttributes.getObjectAsString());
		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("##");
		if(splitted == null || splitted.length != 3 ) {
			throw new StringSerializationException();
		} else {
			this.pattern = new ExtractionPattern();
			this.pattern.convertStringToObject(splitted[0]);
			
			this.patternAttributes = new PatternAttributes();
			this.patternAttributes.convertStringToObject(splitted[1]);
			
			this.outputAttributes = new OutputAttributes();
			this.outputAttributes.convertStringToObject(splitted[2]);
		}
	}
}
