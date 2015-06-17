package model.genelibrary;

import java.io.Serializable;

import util.StringSerializable;
import util.StringSerializationException;

public class ReferenceGene implements Serializable, StringSerializable {

	private String name;

	private String associatedSequence;

	private static final StringBuffer sb = new StringBuffer();

	private ReferenceGene() {
	}
	
	public ReferenceGene(String name, String associatedString) {
		super();
		this.name = name;
		this.associatedSequence = associatedString;
	}

	public int getSequenceLength() {
		return associatedSequence.length();
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.name = id;
	}

	public String getAssociatedSequence() {
		return associatedSequence;
	}

	public void setAssociatedSequence(String associatedString) {
		this.associatedSequence = associatedString;
	}

	@Override
	public String toString() {
		return "ReferenceGene [id=" + name + " , def=" + associatedSequence + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associatedSequence == null) ? 0 : associatedSequence.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ReferenceGene other = (ReferenceGene) obj;
		if (associatedSequence == null) {
			if (other.associatedSequence != null)
				return false;
		} else if (!associatedSequence.equals(other.associatedSequence))
			return false;
		return true;
	}

	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		sb.append(name);
		sb.append("#");
		sb.append(associatedSequence);
		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {

		String[] splitted = input.split("#");
		if (splitted.length != 2) {
			throw new StringSerializationException();
		}
		this.name = splitted[0];
		this.associatedSequence = splitted[1];

	}

}
