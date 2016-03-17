package com.gmo.processorNode.viewmodel.defaultproviders;

import com.gmo.sharedobjects.model.processconfiguration.ExtractionPattern;
import com.gmo.sharedobjects.model.processconfiguration.OutputAttributes;
import com.gmo.sharedobjects.model.processconfiguration.PatternAttributes;
import com.gmo.sharedobjects.util.NoSuchPatternException;

public interface DefaultConfigurationProvider {

	public ExtractionPattern getDefaultExtractionPattern() throws NoSuchPatternException;

	public PatternAttributes getDefaultPatternAttribute();

	public OutputAttributes getDefaultOutputAttributes();

}
