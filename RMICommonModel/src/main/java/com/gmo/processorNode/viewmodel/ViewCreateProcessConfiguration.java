package com.gmo.processorNode.viewmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.defaultproviders.DefaultConfigurationProvider;
import com.gmo.sharedobjects.model.processconfiguration.ExtractionPattern;
import com.gmo.sharedobjects.model.processconfiguration.OutputAttributes;
import com.gmo.sharedobjects.model.processconfiguration.PatternAttributes;
import com.gmo.sharedobjects.util.NoSuchPatternException;
import com.gmo.sharedobjects.util.StringSerializable;
import com.gmo.sharedobjects.util.StringSerializationException;

@XmlRootElement
public class ViewCreateProcessConfiguration implements Serializable, StringSerializable {

	// The selected extraction pattern
	private ExtractionPattern pattern;

	// Pattern attributes
	private PatternAttributes patternAttributes;

	// Output configuration
	private OutputAttributes outputAttributes;

	// List of library files
	private List<ViewFile> selectedLibraries;

	// List of data files
	private List<ViewFile> selectedDataFiles;
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	private StringBuilder sb = new StringBuilder();

	private ViewCreateProcessConfiguration(){
		// Needed for JAXB serialization
	}
			
	public ViewCreateProcessConfiguration(DefaultConfigurationProvider defaultConfig) {

		selectedLibraries = new ArrayList<ViewFile>();
		selectedDataFiles = new ArrayList<ViewFile>();
		
		if(defaultConfig != null) {
			try {
				pattern = defaultConfig.getDefaultExtractionPattern();
			} catch (NoSuchPatternException e) {
				LOG.warn("No default pattern available");
			}
			patternAttributes = defaultConfig.getDefaultPatternAttribute();
			outputAttributes = defaultConfig.getDefaultOutputAttributes();
		}
		
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

	public List<ViewFile> getSelectedLibraries() {
		return selectedLibraries;
	}

	public void setSelectedLibraries(List<ViewFile> selectedLibraries) {
		this.selectedLibraries = selectedLibraries;
	}

	public List<ViewFile> getSelectedDataFiles() {
		return selectedDataFiles;
	}

	public void setSelectedDataFiles(List<ViewFile> selectedDataFiles) {
		this.selectedDataFiles = selectedDataFiles;
	}

	@Override
	public String toString() {
		return "ProcessConfiguration [pattern=" + pattern + ", patternAttributes=" + patternAttributes + ", outputAttributes=" + outputAttributes + ", selectedLibraries=" + selectedLibraries
				+ ", selectedDataFiles=" + selectedDataFiles + "]";
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
		if (splitted == null || splitted.length != 3) {
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

	public List<ViewFile> getAllRequestedFiles() {
		ArrayList<ViewFile> requestedFiles = new ArrayList<>();
		requestedFiles.addAll(selectedDataFiles);
		requestedFiles.addAll(selectedLibraries);
		return requestedFiles;
	}

}
