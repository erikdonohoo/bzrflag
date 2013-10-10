package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class PFGeneFitness{
	protected String gene;
	protected String map;
	protected int fitness;
	protected String note;

	public String getGene(){
		return gene;
	}

	public void setGene(String gene){
		this.gene = gene;
	}

	public String getMap(){
		return map;
	}

	public void setMap(String map){
		this.map = map;
	}

	public int getFitness(){
		return fitness;
	}

	public void setFitness(int fitness){
		this.fitness = fitness;
	}

	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}
}
