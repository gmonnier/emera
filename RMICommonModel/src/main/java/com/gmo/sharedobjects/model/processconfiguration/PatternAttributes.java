package com.gmo.sharedobjects.model.processconfiguration;

import java.io.Serializable;

import com.gmo.sharedobjects.util.StringSerializable;
import com.gmo.sharedobjects.util.StringSerializationException;

public class PatternAttributes implements Serializable, StringSerializable {

	private boolean allowOneMismatch;

	private boolean checkForShifted;

	private StringBuilder sb = new StringBuilder();

	public PatternAttributes() {
		allowOneMismatch = true;
		checkForShifted = true;
	}

	public PatternAttributes(boolean allowOneMismatch, boolean checkForShifted) {
		this.allowOneMismatch = allowOneMismatch;
		this.checkForShifted = checkForShifted;
	}

	public boolean isAllowOneMismatch() {
		return allowOneMismatch;
	}

	public void setAllowOneMismatch(boolean allowOneMismatch) {
		this.allowOneMismatch = allowOneMismatch;
	}

	public boolean isCheckForShifted() {
		return checkForShifted;
	}

	public void setCheckForShifted(boolean checkForShifted) {
		this.checkForShifted = checkForShifted;
	}

	@Override
	public String toString() {
		return "PatternAttributes [allowOneMismatch=" + allowOneMismatch + ", checkForShifted=" + checkForShifted + "]";
	}

	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		sb.append(allowOneMismatch);
		sb.append("#");
		sb.append(checkForShifted);
		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("#");
		if (splitted == null || splitted.length != 2) {
			throw new StringSerializationException();
		} else {
			this.allowOneMismatch = Boolean.parseBoolean(splitted[0]);
			this.checkForShifted = Boolean.parseBoolean(splitted[1]);
		}
	}

}
