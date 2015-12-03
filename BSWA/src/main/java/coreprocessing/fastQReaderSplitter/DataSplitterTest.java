package coreprocessing.fastQReaderSplitter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import systemUtil.SystemCommand;
import logger.JavaStyleLogger;
import logger.Log4JLogger;
import model.processconfig.files.ModelFileStored;
import coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import coreprocessing.fastQReaderDispatcher.ReadDispatchException;
import coreprocessing.fastQReaderPrinter.DataReaderPrinter;

public class DataSplitterTest {
	
		// Set up logging properties.
		static {
			// Clear logs directory
			new SystemCommand().removeAllINDirectory("logs");

			// Set up the log4j logger
			Log4JLogger.setup(true, true, "conf/logging", "BSWA_test_DataSplitter");

			JavaStyleLogger.setup(false, "conf/logging");

		}

		// log4j logger - Main logger
		private static Logger LOG = Log4JLogger.logger;

		public static void main(String[] args) {
			List<ModelFileStored> input = new ArrayList<ModelFileStored>();
			ModelFileStored mfs = new ModelFileStored(new File("/Users/gmonnie/Documents/emera_data/150727-gl36-gl3d-yl3d_S1_L001_R1_001.fastq"));
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
			
			List<DataSplitterModel> models = new ArrayList<DataSplitterModel>();
			models.add(new DataSplitterModel("\\A...GTAG", "/Users/gmonnie/Documents/emera_data/splitted/GTAGA.fastq", ""));
			models.add(new DataSplitterModel("\\A...CAAT", "/Users/gmonnie/Documents/emera_data/splitted/CAATG.fastq", ""));

			DataReaderSplitter splitter = new DataReaderSplitter(input, models, listener);
			try {
				splitter.readAndSplit();
			} catch (ReadDispatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
