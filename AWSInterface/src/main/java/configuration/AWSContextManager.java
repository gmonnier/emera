package configuration;

import java.io.File;

import configuration.jaxb.aws.AWSConfiguration;
import configuration.xmljaxb.AbstractConfigurationManager;


public class AWSContextManager extends AbstractConfigurationManager<AWSConfiguration> {

	private final static String contextFile = "conf/AWSContext.xml";

	private static AWSContextManager instance;

	private AWSContextManager() {
		super(new File(contextFile), new AWSConfiguration());
	}

	public static AWSContextManager getInstance() {
		if (instance == null) {
			instance = new AWSContextManager();
		}
		return instance;
	}
}
