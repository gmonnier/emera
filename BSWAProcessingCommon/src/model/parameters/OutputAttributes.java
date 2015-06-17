package model.parameters;

import java.io.Serializable;

import util.StringSerializable;
import util.StringSerializationException;

public class OutputAttributes implements Serializable, StringSerializable {

	private boolean generateCSV;

	private boolean generatePDF;

	private boolean generateStatisticsOnUnfoundgRNA;
	
	private StringBuilder sb = new StringBuilder();

	public OutputAttributes() {
		generateCSV = true;
		generatePDF = true;
		generateStatisticsOnUnfoundgRNA = false;
	}

	public OutputAttributes(boolean generateCSV, boolean generatePDF, boolean generateStatisticsOnUnfoundgRNA) {
		this.generateCSV = generateCSV;
		this.generatePDF = generatePDF;
		this.generateStatisticsOnUnfoundgRNA = generateStatisticsOnUnfoundgRNA;
	}

	public boolean isGenerateCSV() {
		return generateCSV;
	}

	public void setGenerateCSV(boolean generateCSV) {
		this.generateCSV = generateCSV;
	}

	public boolean isGeneratePDF() {
		return generatePDF;
	}

	public void setGeneratePDF(boolean generatePDF) {
		this.generatePDF = generatePDF;
	}

	public boolean isGenerateStatisticsOnUnfoundgRNA() {
		return generateStatisticsOnUnfoundgRNA;
	}

	public void setGenerateStatisticsOnUnfoundgRNA(boolean generateStatisticsOnUnfoundgRNA) {
		this.generateStatisticsOnUnfoundgRNA = generateStatisticsOnUnfoundgRNA;
	}

	@Override
	public String toString() {
		return "OutputAttributes [generateCSV=" + generateCSV + ", generatePDF=" + generatePDF + ", generateStatisticsOnUnfoundgRNA=" + generateStatisticsOnUnfoundgRNA + "]";
	}
	
	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		sb.append(generateCSV);
		sb.append("#");
		sb.append(generatePDF);
		sb.append("#");
		sb.append(generateStatisticsOnUnfoundgRNA);
		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("#");
		if (splitted == null || splitted.length != 3) {
			throw new StringSerializationException();
		} else {
			this.generateCSV = Boolean.parseBoolean(splitted[0]);
			this.generatePDF = Boolean.parseBoolean(splitted[1]);
			this.generateStatisticsOnUnfoundgRNA = Boolean.parseBoolean(splitted[2]);
		}
	}
}
