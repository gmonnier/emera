package com.gmo.coreprocessing.fastQReaderPrinter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import com.gmo.logger.JavaStyleLogger;
import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.systemUtil.SystemCommand;

public class DataReaderTest {

	// Set up logging properties.
	static {
		// Clear logs directory
		new SystemCommand().removeAllINDirectory("logs");

		// Set up the log4j logger
		Log4JLogger.setup(true, true, "conf/logging", "BSWA_test_DataPrinter");

		JavaStyleLogger.setup(false, "conf/logging");

	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) {
		List<ModelFileStored> input = new ArrayList<ModelFileStored>();
		ModelFileStored mfs = new ModelFileStored(new File("/Users/gmonnie/Documents/emera_data/splitted/CAATG.fastq"));
		input.add(mfs);

		IReaderDispatcherListener listener = new IReaderDispatcherListener() {

			@Override
			public void readProgress(int lineRead, int percent) {
				// TODO Auto-generated method stub

			}

			@Override
			public void readDone(int totalCount) {
				LOG.debug("DONE");
			}
		};
		DataReaderPrinter printer = new DataReaderPrinter(input, listener);
		try {
			printer.readAndPrint();
		} catch (ReadDispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
