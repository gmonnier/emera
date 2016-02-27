package com.gmo.model.processconfiguration;

import com.gmo.util.NoSuchPatternException;

public interface DefaultConfigurationProvider {

	public ExtractionPattern getDefaultExtractionPattern() throws NoSuchPatternException;

	public PatternAttributes getDefaultPatternAttribute();

	public OutputAttributes getDefaultOutputAttributes();

}
