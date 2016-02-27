package processorNode.viewmodel.defaultproviders;

import com.gmo.model.processconfiguration.ExtractionPattern;
import com.gmo.model.processconfiguration.OutputAttributes;
import com.gmo.model.processconfiguration.PatternAttributes;
import com.gmo.util.NoSuchPatternException;

public interface DefaultConfigurationProvider {

	public ExtractionPattern getDefaultExtractionPattern() throws NoSuchPatternException;

	public PatternAttributes getDefaultPatternAttribute();

	public OutputAttributes getDefaultOutputAttributes();

}
