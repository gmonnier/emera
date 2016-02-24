package com.gmo.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gmo.util.StringSerializable;
import com.gmo.util.StringSerializationException;

public class DataChunk implements Serializable, StringSerializable {

	private List<String> linesToProcess;
	
	private String chunkID;
	
	private final StringBuilder sb = new StringBuilder();
	
	public DataChunk(){
		linesToProcess = new ArrayList<String>();
		chunkID = UUID.randomUUID().toString();
	}
	
	public DataChunk(String chunkID) {
		linesToProcess = new ArrayList<String>();
		this.chunkID = chunkID;
	}

	public void addLineToProcess(String line) {
		linesToProcess.add(line);
	}

	public List<String> getLinesToProcess() {
		return linesToProcess;
	}

	public String getChunkID() {
		return chunkID;
	}

	@Override
	public String toString() {
		return "DataChunk [chunkID=" + chunkID + ", linesToProcess=" + linesToProcess.size() + "]";
	}

	public void setChunkID(String chunkID) {
		this.chunkID = chunkID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkID == null) ? 0 : chunkID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataChunk other = (DataChunk) obj;
		if (chunkID == null) {
			if (other.chunkID != null)
				return false;
		} else if (!chunkID.equals(other.chunkID))
			return false;
		return true;
	}

	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		// Append first the chunkID
		sb.append(chunkID);
		sb.append("#");
		for (int i = 0; i < linesToProcess.size() - 1; i++) {
			sb.append(linesToProcess.get(i));
			sb.append("#");
		}
		// Dont forget to add the last item
		sb.append(linesToProcess.get(linesToProcess.size() - 1));

		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("#");
		if(splitted == null || splitted.length < 1) {
			throw new StringSerializationException();
		}
		this.chunkID = splitted[0];
		
		for (int i = 1; i < splitted.length; i++) {
			linesToProcess.add(splitted[i]);
		}
	}
}
