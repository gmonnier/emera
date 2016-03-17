package com.gmo.coreprocessing.fastQReaderDispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.model.data.DataChunk;

public class ChunkQueueBuffer {

	private static Logger LOG = Log4JLogger.logger;

	private final static int MAX_QUEUE_SIZE = 20;

	private BlockingDeque<DataChunk> chunkBuffer = new LinkedBlockingDeque<DataChunk>(MAX_QUEUE_SIZE);

	private Map<DataChunk, String> inprocessChunks = Collections.synchronizedMap(new HashMap<DataChunk, String>());

	private boolean producerDone = false;

	public void add(DataChunk chunk) throws InterruptedException {
		try {
			LOG.debug("Waiting to add new chunk to buffer : " + chunk);
			chunkBuffer.put(chunk);
			LOG.debug("New Chunk added to buffer " + chunk);
		} catch (InterruptedException e) {
			LOG.debug("Interrupted exception thrown while attempting to add chunk data in the queue");
		}
	}

	/**
	 * Remove a chunk from both temporary list and from the buffer.
	 * 
	 * @param chunkID
	 */
	public void remove(String chunkID) {
		DataChunk toremove = new DataChunk(chunkID);

		LOG.debug("Remove chunck from buffer : " + chunkID);
		String reminProc = inprocessChunks.remove(toremove);
		boolean rembuffer = chunkBuffer.remove(toremove);

		if (reminProc != null && rembuffer) {
			LOG.debug("Datachunk with ID " + chunkID + " successfully remove from temporary set and buffer");
		} else {
			LOG.error("Datachunk with ID " + chunkID + " not found either in buffer or temp set : reminProc = " + reminProc + " rembuffer = " + rembuffer);
		}

	}

	/**
	 * Release ALL chunks associated with given distant resource ID.
	 * 
	 * @param resourceID
	 */
	public void releaseChunks(String resourceID) {
		LOG.debug("Release chunks associated with dist resource " + resourceID);
		LOG.debug("Info on buffer before resources release : producerDone = " + producerDone + "   inprocessChunks map size: " + inprocessChunks.size() + "     bufferSize : " + chunkBuffer);
		Iterator<Map.Entry<DataChunk, String>> iterator = inprocessChunks.entrySet().iterator();

		List<DataChunk> associatedChunks = new ArrayList<DataChunk>();
		while (iterator.hasNext()) {
			Map.Entry<DataChunk, String> pair = (Map.Entry<DataChunk, String>) iterator.next();
			if (pair.getValue().equals(resourceID)) {
				associatedChunks.add(pair.getKey());
			}
		}

		for (DataChunk dataChunk : associatedChunks) {
			inprocessChunks.remove(dataChunk);
		}
		
		LOG.debug("Info on buffer after resources release : producerDone = " + producerDone + "   inprocessChunks map size: " + inprocessChunks.size() + "     bufferSize : " + chunkBuffer);
	}

	public boolean isBufferTerminated() {
		if (producerDone && inprocessChunks.isEmpty()) {
			return true;
		} else {
			LOG.debug("Buffer not yet terminated : producerDone = " + producerDone + "   inprocessChunks map size: " + inprocessChunks.size() + "     bufferSize : " + chunkBuffer);
			return false;
		}
	}

	public DataChunk takeNextChunckForProcess(String distResourceID) {

		LOG.debug("Request to take next chunk in the buffer from " + distResourceID);

		/**
		 * Define a hwile loop here to wait that a chunk become available
		 */
		DataChunk availableChunk = null;

		lookForChunks: while (availableChunk == null) {
			for (Iterator<DataChunk> iterator = chunkBuffer.iterator(); iterator.hasNext();) {
				DataChunk dataChunk = (DataChunk) iterator.next();
				if (inprocessChunks.get(dataChunk) == null) {
					availableChunk = dataChunk;
					break lookForChunks;
				}
			}

			if (producerDone) {
				break;
			}
			// Usually should not wait since the queue size should be greater
			// than the number of running machines
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				LOG.error("Interrupted exception while waiting for new chunk");
			}
		}

		if (availableChunk != null) {
			inprocessChunks.put(availableChunk, distResourceID);
		}
		return availableChunk;

	}

	public boolean isProducerDone() {
		return producerDone;
	}

	public void producerDone() {
		producerDone = true;
	}

}
