package model.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import util.StringSerializable;
import util.StringSerializationException;

public class ChunkResult implements Serializable, StringSerializable {

	private String chunkId;

	private long linesProcessed;

	// Occurences of all grNAs
	private Map<String, Integer> occurences;

	private final StringBuilder sb = new StringBuilder();

	public ChunkResult() {
	}

	public ChunkResult(String chunkId) {
		this.linesProcessed = 0;
		this.chunkId = chunkId;
		occurences = Collections.synchronizedMap(new HashMap<String, Integer>());
	}

	public String getChunkId() {
		return chunkId;
	}

	public void setChunkId(String chunkId) {
		this.chunkId = chunkId;
	}

	public Map<String, Integer> getOccurences() {
		return occurences;
	}

	public void setOccurences(HashMap<String, Integer> occurences) {
		this.occurences = occurences;
	}

	public long getLinesProcessed() {
		return linesProcessed;
	}

	public void setLinesProcessed(long linesProcessed) {
		this.linesProcessed = linesProcessed;
	}

	@Override
	public String getObjectAsString() {

		sb.append(chunkId);
		sb.append("#");
		sb.append(linesProcessed);

		if (occurences == null || occurences.size() == 0) {
			return sb.toString();
		}

		Iterator<Map.Entry<String, Integer>> it = occurences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
			sb.append("#");
			sb.append(pair.getKey());
			sb.append("#");
			sb.append(pair.getValue());
		}

		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("#");
		if (splitted == null || splitted.length < 2) {
			throw new StringSerializationException();
		}
		this.chunkId = splitted[0];
		this.linesProcessed = Integer.parseInt(splitted[1]);

		this.occurences = new HashMap<String, Integer>();

		for (int i = 2; (i+1) < splitted.length; i += 2) {
			String occurenceStr = "";
			try {
				String grnaKey = splitted[i];
				occurenceStr = splitted[i + 1];
				int grnaOcc = Integer.parseInt(occurenceStr);
				occurences.put(grnaKey, grnaOcc);
			} catch (NumberFormatException nfe) {
				throw new StringSerializationException(nfe, " Trying to parseInt value : " + occurenceStr + "\n complete input: " + input);
			} catch (ArrayIndexOutOfBoundsException ooe) {
				throw new StringSerializationException(ooe);
			}
		}
	}

	public synchronized void incrementLinesProcessed(int linesProcessed) {
		this.linesProcessed += linesProcessed;
	}

}
