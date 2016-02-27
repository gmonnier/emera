package com.gmo.model.parameters;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.gmo.util.StringSerializable;
import com.gmo.util.StringSerializationException;

@XmlRootElement
public class ExtractionPattern implements Serializable, StringSerializable {

	private int skippedCharCount;

	private int grnaSubSequenceLength;

	private boolean invalidPattern;

	private String extractionSequence;

	private String alias;

	private StringBuilder sb = new StringBuilder();

	// Needed for JAXB JSON encoder/ serialization
	public ExtractionPattern() {
	}

	public ExtractionPattern(String extractionSequence, String alias) {

		this.extractionSequence = extractionSequence;
		this.alias = alias;
		grnaSubSequenceLength = 0;
		invalidPattern = false;
		skippedCharCount = 0;

		try {
			char currentChar;
			while (true) {
				currentChar = extractionSequence.charAt(skippedCharCount);
				if (currentChar == 'a' || currentChar == 'A' || currentChar == 't' || currentChar == 'T' || currentChar == 'g' || currentChar == 'G' || currentChar == 'c' || currentChar == 'C') {
					skippedCharCount++;
				} else {
					break;
				}
			}

			int startSubSeq = skippedCharCount;
			while (true) {
				currentChar = extractionSequence.charAt(startSubSeq);
				if (!(currentChar == 'a' || currentChar == 'A' || currentChar == 't' || currentChar == 'T' || currentChar == 'g' || currentChar == 'G' || currentChar == 'c' || currentChar == 'C')) {
					grnaSubSequenceLength++;
					startSubSeq++;
				} else {
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Invalid entered pattern");
			invalidPattern = true;
		}

	}

	public String getExtractionSequence() {
		return extractionSequence;
	}

	public void setExtractionSequence(String extractionSequence) {
		this.extractionSequence = extractionSequence;
	}

	public boolean isInvalidPattern() {
		return invalidPattern;
	}

	public void setSkippedCharCount(int skippedCharCount) {
		this.skippedCharCount = skippedCharCount;
	}

	public int getSkippedCharCount() {
		return skippedCharCount;
	}

	public int getGrnaSubSequenceLength() {
		return grnaSubSequenceLength;
	}

	public void setGrnaSubSequenceLength(int grnaSubSequenceLength) {
		this.grnaSubSequenceLength = grnaSubSequenceLength;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return "skippedCharCount = " + skippedCharCount + "   grnaSubSequenceLength = " + grnaSubSequenceLength;
	}

	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		sb.append(skippedCharCount);
		sb.append("#");
		sb.append(grnaSubSequenceLength);
		sb.append("#");
		sb.append(invalidPattern);
		sb.append("#");
		sb.append(extractionSequence);
		if (alias != null) {
			sb.append("#");
			sb.append(alias);
		}
		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("#");
		if (splitted == null || splitted.length < 4) {
			throw new StringSerializationException();
		} else {
			try {
				this.skippedCharCount = Integer.parseInt(splitted[0]);
				this.grnaSubSequenceLength = Integer.parseInt(splitted[1]);
				this.invalidPattern = Boolean.parseBoolean(splitted[2]);
				this.extractionSequence = splitted[3];
				if(splitted.length > 4) {
					this.alias = splitted[4];
				}
			} catch (NumberFormatException nfe) {
				throw new StringSerializationException();
			}
		}
	}

}
