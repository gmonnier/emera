package com.gmo.sharedobjects.model.genelibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.gmo.sharedobjects.util.StringSerializable;
import com.gmo.sharedobjects.util.StringSerializationException;

public class GeneLibrary implements Serializable, StringSerializable {

	@XmlTransient
	private List<ReferenceGene> genes;

	private static final StringBuffer sb = new StringBuffer();

	public GeneLibrary() {
		genes = new ArrayList<>();
	}

	public void add(ReferenceGene newGene) {
		genes.add(newGene);
	}

	@XmlTransient
	public List<ReferenceGene> getGenes() {
		return genes;
	}

	@XmlTransient
	public void setGenes(List<ReferenceGene> genes) {
		this.genes = genes;
	}

	public int size() {
		return genes.size();
	}

	@Override
	public String toString() {
		return "GeneLibrary [genesIds size=" + genes.size() + "]";
	}

	@Override
	public String getObjectAsString() {
		sb.setLength(0);
		for (int i = 0; i < genes.size(); i++) {
			sb.append(genes.get(i).getObjectAsString());
			if (i != genes.size() - 1) {
				sb.append("##");
			}
		}
		return sb.toString();
	}

	@Override
	public void convertStringToObject(String input) throws StringSerializationException {
		String[] splitted = input.split("##");
		for (int i = 0; i < splitted.length; i++) {
			ReferenceGene gene = new ReferenceGene("","");
			gene.convertStringToObject(splitted[i]);
			genes.add(gene);
		}
	}

}
