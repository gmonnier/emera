package com.gmo.processorNode.viewmodel;

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

@XmlRootElement
public class ViewCreateProcessConfiguration {

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

	public ViewCreateProcessConfiguration(DefaultConfigurationProvider defaultConfig) {

		selectedLibraries = new ArrayList<ViewFile>();
		selectedDataFiles = new ArrayList<ViewFile>();

		//ApplicationContext defaultConfig = ApplicationContextManager.getInstance().getConfig();

		/*try {
			pattern = ApplicationContextManager.getInstance().getDefaultExtractionPattern();
		} catch (NoSuchPatternException e) {
			LOG.warn("No default pattern available");
		}
		patternAttributes = new PatternAttributes(defaultConfig.isAllowCharacterError(), defaultConfig.isAllowShifting());
		outputAttributes = new OutputAttributes(defaultConfig.isGenerateCSVOutput(), defaultConfig.isGeneratePDFOutput(), defaultConfig.isCheckForUnfoundEntries());*/
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

}
