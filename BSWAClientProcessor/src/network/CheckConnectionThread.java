package network;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

public class CheckConnectionThread extends Thread {
	
	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private ProcessorClient processorClient;
	
	public CheckConnectionThread(ProcessorClient processorClient) {
		this.processorClient = processorClient;
	}

	@Override
	public void run() {

		while (true) {
			
			if(processorClient.initializeConnexion(true)){
				break;
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				LOG.error("Interrupted exception while attempting to reinit the connection");
			}
			
		}
	}

}
