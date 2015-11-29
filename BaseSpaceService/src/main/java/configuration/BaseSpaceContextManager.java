package configuration;

import java.io.File;

import configuration.jaxb.basespace.BaseSpaceConfiguration;
import configuration.xmljaxb.AbstractConfigurationManager;

public class BaseSpaceContextManager extends
		AbstractConfigurationManager<BaseSpaceConfiguration> {

	private final static String contextFile = "conf/BaseSpaceContext.xml";

	private static BaseSpaceContextManager instance;

	private BaseSpaceContextManager() {
		super(new File(contextFile), new BaseSpaceConfiguration());
	}

	public static BaseSpaceContextManager getInstance() {
		if (instance == null) {
			instance = new BaseSpaceContextManager();
		}
		return instance;
	}

}
