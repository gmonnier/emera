package com.gmo.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.xmljaxb.AbstractConfigurationManager;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.generated.configuration.storageconfig.StorageConfiguration;
import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.systemUtil.SystemCommand;

public class StorageConfigurationManager extends AbstractConfigurationManager<StorageConfiguration> {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private final static String contextFile = "conf/StorageConfiguration.xml";

	private static StorageConfigurationManager instance;

	private List<ModelFileStored> listStoredLibraries;

	private List<ModelFileStored> listStoredData;

	private StorageConfigurationManager() {
		super(new File(contextFile), new StorageConfiguration());

		listStoredLibraries = new ArrayList<ModelFileStored>();
		listStoredData = new ArrayList<ModelFileStored>();

		updateModel();
	}

	/**
	 * Retrieve and fill data and library files list from storage location.
	 */
	public void updateModel() {

		listStoredLibraries.clear();
		listStoredData.clear();

		String pathLib = getConfig().getLibrariesFilesRoot();
		String pathData = getConfig().getDataFilesRoot();
		SystemCommand scmd = new SystemCommand();

		LOG.debug("Extract storage files into " + pathLib);
		try {
			File[] listLibs = scmd.listFilesOnly(pathLib);
			if (listLibs != null) {
				for (int i = 0; i < listLibs.length; i++) {
					File libFile = listLibs[i];
					ModelFileStored lib = new ModelFileStored(libFile);
					lib.setSystemFile(libFile);
					listStoredLibraries.add(lib);
				}
				LOG.debug("Set Libraries list : " + listStoredLibraries);
			} else {
				LOG.error("No stored data file extracted from " + pathLib);
			}

			File[] listData = scmd.listFilesOnly(pathData);
			if (listData != null) {
				for (int i = 0; i < listData.length; i++) {
					File dataFile = listData[i];
					ModelFileStored dat = new ModelFileStored(dataFile);
					dat.setSystemFile(dataFile);
					listStoredData.add(dat);
				}
				LOG.debug("Set Data list : " + listStoredData);
			} else {
				LOG.warn("No stored data file extracted from " + pathData);
			}
		} catch (SecurityException e) {
			LOG.error("Security Exception thrown when looking for stored files :", e);
		}

	}

	/**
	 * Get a resource with its absolute path
	 * 
	 * @param absoluteFilePath
	 * @return
	 * @throws NoSuchElementException
	 */
	public ModelFileStored getWithPath(InputType type, String fileName) throws NoSuchElementException {

		List<ModelFileStored> listFiles;
		switch (type) {
		case DATA: {
			listFiles = listStoredData;
			break;
		}
		case LIBRARY:
		default: {
			listFiles = listStoredLibraries;
			break;
		}
		}

		for (Iterator<ModelFileStored> iterator = listFiles.iterator(); iterator.hasNext();) {
			ModelFileStored modelFileStored = (ModelFileStored) iterator.next();
			if (fileName.equalsIgnoreCase(modelFileStored.getSystemFile().getName())) {
				return modelFileStored;
			}
		}

		throw new NoSuchElementException();
	}

	public ModelFileStored getWithID(String fileID) throws NoSuchElementException {
		for (Iterator<ModelFileStored> iterator = listStoredData.iterator(); iterator.hasNext();) {
			ModelFileStored modelFileStored = (ModelFileStored) iterator.next();
			if (fileID.equals(modelFileStored.getId())) {
				return modelFileStored;
			}
		}
		for (Iterator<ModelFileStored> iterator = listStoredLibraries.iterator(); iterator.hasNext();) {
			ModelFileStored modelFileStored = (ModelFileStored) iterator.next();
			if (fileID.equals(modelFileStored.getId())) {
				return modelFileStored;
			}
		}

		throw new NoSuchElementException();
	}

	public static synchronized StorageConfigurationManager getInstance() {
		if (instance == null) {
			instance = new StorageConfigurationManager();
		}
		return instance;
	}

	public List<ModelFileStored> getListStoredLibraries() {
		return listStoredLibraries;
	}

	public List<ModelFileStored> getListStoredData() {
		return listStoredData;
	}
	
}
