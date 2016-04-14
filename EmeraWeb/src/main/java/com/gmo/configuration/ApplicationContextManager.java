package com.gmo.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.xmljaxb.AbstractConfigurationManager;
import com.gmo.generated.configuration.applicationcontext.ApplicationContext;
import com.gmo.generated.configuration.applicationcontext.Pattern;
import com.gmo.generated.configuration.applicationcontext.PatternsStorage;
import com.gmo.generated.configuration.applicationcontext.SplitPattern;
import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewDataSplitterModel;
import com.gmo.processorNode.viewmodel.defaultproviders.DefaultConfigurationProvider;
import com.gmo.sharedobjects.model.processconfiguration.ExtractionPattern;
import com.gmo.sharedobjects.model.processconfiguration.OutputAttributes;
import com.gmo.sharedobjects.model.processconfiguration.PatternAttributes;
import com.gmo.sharedobjects.util.NoSuchPatternException;

public class ApplicationContextManager extends AbstractConfigurationManager<ApplicationContext> implements DefaultConfigurationProvider {

	private final static String contextFile = "conf/ApplicationContext.xml";

	private final List<ExtractionPattern> listPatterns;

	private final List<ViewDataSplitterModel> listViewSplitPatterns;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private static ApplicationContextManager instance;

	private ApplicationContextManager() {
		super(new File(contextFile), new ApplicationContext());
		listPatterns = new ArrayList<ExtractionPattern>();

		// Init pattern list
		if (getConfig().getPatternsStorage() == null) {
			PatternsStorage stp = new PatternsStorage();
			stp.setDefaultPatternIndex(0);
			getConfig().setPatternsStorage(new PatternsStorage());
		}

		List<Pattern> patterns = getConfig().getPatternsStorage().getPatterns();
		for (int i = patterns.size() - 1; i >= 0; i--) {
			ExtractionPattern pattern = new ExtractionPattern(patterns.get(i).getValue(), patterns.get(i).getAlias());
			if (!pattern.isInvalidPattern()) {
				listPatterns.add(pattern);
			} else {
				// Remove current invalid pattern from the configuration list
				LOG.error("Invalid pattern received from configuration : " + patterns.get(i) + "   remove it from list");
				patterns.remove(i);
			}
		}

		listViewSplitPatterns = new ArrayList<ViewDataSplitterModel>();
		try {
			List<SplitPattern> splitPatterns = getConfig().getSplitPatternsStorage().getSplitPatterns();
			for (int i = splitPatterns.size() - 1; i >= 0; i--) {
				ViewDataSplitterModel viewSplitPattern = new ViewDataSplitterModel(splitPatterns.get(i).getValue(),splitPatterns.get(i).getOutputName(), splitPatterns.get(i).getAlias());
				listViewSplitPatterns.add(viewSplitPattern);
			}
		} catch (NullPointerException npe) {
			LOG.warn("No split pattern in configuration yet.");
		}
	}

	public static synchronized ApplicationContextManager getInstance() {
		if (instance == null) {
			instance = new ApplicationContextManager();
		}
		return instance;
	}

	public ExtractionPattern getDefaultExtractionPattern() throws NoSuchPatternException {
		int defaultIndex = getConfig().getPatternsStorage().getDefaultPatternIndex();
		if (defaultIndex < 0 || defaultIndex >= listPatterns.size()) {
			LOG.error("No default pattern found with following index : " + defaultIndex);
			if (listPatterns.size() > 0) {
				// Return first pattern in list
				getConfig().getPatternsStorage().setDefaultPatternIndex(0);
				return listPatterns.get(0);
			} else {
				// List is empty
				throw new NoSuchPatternException();
			}
		}
		return listPatterns.get(defaultIndex);
	}

	public List<ExtractionPattern> getListPatterns() {
		return listPatterns;
	}

	public List<ViewDataSplitterModel> getListViewSplitPatterns() {
		return listViewSplitPatterns;
	}

	/**
	 * Request to set a pattern as the default one.
	 * 
	 * @param patternString
	 *            the extraction string defining this pattern.
	 * @return true if pattern is set to default, false otherwise.
	 */
	public boolean requestSetDefaultPattern(String patternString) {
		for (int i = listPatterns.size() - 1; i >= 0; i--) {
			if (listPatterns.get(i).getExtractionSequence().equalsIgnoreCase(patternString)) {
				LOG.info("Patternindex " + i + " set as default one : pattern = " + listPatterns.get(i));
				getConfig().getPatternsStorage().setDefaultPatternIndex(i);
				return true;
			}
		}
		LOG.warn("No such pattern found with extraction string : " + patternString);
		return false;
	}

	/**
	 * Add a new pattern to the list of stored patterns.
	 * 
	 * @param patternString
	 *            the extraction string defining this pattern.
	 * @return true if pattern is valid and successfully added, false otherwise.
	 */
	public boolean requestAddPattern(ExtractionPattern pattern) {

		if (pattern.isInvalidPattern()) {
			LOG.warn("Trying to add an invalid pattern. Abort : patternStr = " + pattern.getExtractionSequence());
			return false;
		} else {
			for (int i = listPatterns.size() - 1; i >= 0; i--) {
				if (listPatterns.get(i).getExtractionSequence().equalsIgnoreCase(pattern.getExtractionSequence())) {
					LOG.info("Pattern already exists in the storage database, Abort. " + pattern.getExtractionSequence());
					return false;
				}
			}
			listPatterns.add(pattern);

			Pattern pat = new Pattern();
			pat.setAlias(pattern.getAlias());
			pat.setValue(pattern.getExtractionSequence());

			getConfig().getPatternsStorage().getPatterns().add(pat);
			LOG.info("Pattern added successfully to the storage list : patternStr = " + pattern.getExtractionSequence());

			// Don't forget to update the xml configuration file.
			getWriter().marshalXMLFileExternalThread();

			return true;
		}
	}

	/**
	 * Remove a new pattern from the list of stored patterns.
	 * 
	 * @param patternString
	 *            the extraction string defining this pattern to be removed.
	 * @return true if pattern was remove successfully, false otherwise.
	 */
	public boolean requestRemovePattern(String patternString) {

		int defaultIndex = getConfig().getPatternsStorage().getDefaultPatternIndex();

		for (int i = listPatterns.size() - 1; i >= 0; i--) {
			if (patternString.equalsIgnoreCase(listPatterns.get(i).getExtractionSequence())) {
				// Remove current pattern from list
				if (defaultIndex == i) {
					getConfig().getPatternsStorage().setDefaultPatternIndex(0);
				}
				listPatterns.remove(i);
				getConfig().getPatternsStorage().getPatterns().remove(i);
				return true;
			}
		}

		return false;
	}

	public void updateDefaultPatternIndex(ExtractionPattern pattern) {
		String patternSeq = pattern.getExtractionSequence();
		for (int i = listPatterns.size() - 1; i >= 0; i--) {
			if (patternSeq.equalsIgnoreCase(listPatterns.get(i).getExtractionSequence())) {
				getConfig().getPatternsStorage().setDefaultPatternIndex(i);
			}
		}
	}

	@Override
	public PatternAttributes getDefaultPatternAttribute() {
		return new PatternAttributes(getConfig().isAllowCharacterError(), getConfig().isAllowShifting());
	}

	@Override
	public OutputAttributes getDefaultOutputAttributes() {
		return new OutputAttributes(getConfig().isGenerateCSVOutput(), getConfig().isGeneratePDFOutput(), getConfig().isCheckForUnfoundEntries());
	}

}
