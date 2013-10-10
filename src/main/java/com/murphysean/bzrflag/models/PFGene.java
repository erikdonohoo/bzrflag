package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class PFGene{
	protected String gene;
	protected int generation;
	protected float attRadius;
	protected float attSpread;
	protected float attStrength;
	protected float rejRadius;
	protected float rejSpread;
	protected float rejStrength;
	protected float tanRadius;
	protected float tanSpread;
	protected float tanStrength;
	protected List<PFGeneFitness> fitness;
	protected List<String> parentGenes;
	protected String mutations;

	public PFGene(){
		fitness = new ArrayList<>();
		parentGenes = new ArrayList<>();
	}

	public PFGene(float attRadius, float attSpread, float attStrength, float rejRadius, float rejSpread, float rejStrength, float tanRadius, float tanSpread, float tanStrength){
		this();

		this.attRadius = attRadius;
		this.attSpread = attSpread;
		this.attStrength = attStrength;

		this.rejRadius = rejRadius;
		this.rejSpread = rejSpread;
		this.rejStrength = rejStrength;

		this.tanRadius = tanRadius;
		this.tanSpread = tanSpread;
		this.tanStrength = tanStrength;
	}

	public static PFGene generateRandomPFGene(){
		PFGene pfGene = new PFGene();

		//In this case the radius will be actual, let's constrain between 1 and 5
		pfGene.setAttRadius((float)Math.random() * 5f);
		//The spread of influence past the radius in which the pull decreases, let's constrain between 10 and 50
		pfGene.setAttSpread(((float)Math.random() * 40f) + 10f);
		//This is the actual strength of the attractor, or the magnitude of the vector. The tank can only accept
		//speeds between 0-1, so that also seems like a good range, however since it combines with the other fields,
		//it might be necissary for the field to have a strength greater than one in order to compete with the other
		//fields. We'll make the range anywhere from 0, to twice the possible speed of the tank. 0-2
		pfGene.setAttStrength((float)Math.random() * 2f);

		//The rejector radius is a multiplier of the actual radius of the object, keep it between .5-2
		pfGene.setRejRadius((float)Math.random() * 1.5f + .5f);
		//Again a multiplier of the actual radius of the object, between 0 - 2
		pfGene.setRejSpread((float)Math.random() * 2f);
		pfGene.setRejStrength((float)Math.random() * 2f);

		//A multiplier of obstacle
		pfGene.setTanRadius((float)Math.random() * 1.5f + .5f);
		//Again a multiplier of the actual radius of the object, between 0 - 2
		pfGene.setTanSpread((float)Math.random() * 2f);
		pfGene.setTanStrength((float)Math.random() * 2f);

		pfGene.generateGeneFromCurrentSettings();
		pfGene.setGeneration(1);
		pfGene.setMutations("Randomly Generated Seed");
		//Now slap it into the database
		return pfGene;
	}

	public static PFGene getKnownWorkingPFGene(){
		PFGene pfGene = new PFGene();
		pfGene.setAttRadius(1.0f);
		pfGene.setAttSpread(25.0f);
		pfGene.setAttStrength(1.0f);
		pfGene.setRejRadius(1.0f);
		pfGene.setRejSpread(1.5f);
		pfGene.setRejStrength(1.0f);
		pfGene.setTanRadius(1.1f);
		pfGene.setTanSpread(1.3f);
		pfGene.setTanStrength(0.9f);

		pfGene.generateGeneFromCurrentSettings();
		pfGene.setGeneration(0);
		pfGene.setMutations("Known working pfgene");

		return pfGene;
	}

	public String generateGeneFromCurrentSettings(){
		NumberFormat formatter = new DecimalFormat("#0.00");
		String gene = formatter.format(attRadius) + "-" +
				formatter.format(attSpread) + "-" +
				formatter.format(attStrength) + "-" +
				formatter.format(rejRadius) + "-" +
				formatter.format(rejSpread) + "-" +
				formatter.format(rejStrength) + "-" +
				formatter.format(tanRadius) + "-" +
				formatter.format(tanSpread) + "-" +
				formatter.format(tanStrength);

		this.gene = gene;
		return gene;
	}

	public String getGene(){
		return gene;
	}

	public void setGene(String gene){
		this.gene = gene;
	}

	public int getGeneration(){
		return generation;
	}

	public void setGeneration(int generation){
		this.generation = generation;
	}

	public float getAttRadius(){
		return attRadius;
	}

	public void setAttRadius(float attRadius){
		this.attRadius = attRadius;
	}

	public float getAttSpread(){
		return attSpread;
	}

	public void setAttSpread(float attSpread){
		this.attSpread = attSpread;
	}

	public float getAttStrength(){
		return attStrength;
	}

	public void setAttStrength(float attStrength){
		this.attStrength = attStrength;
	}

	public float getRejRadius(){
		return rejRadius;
	}

	public void setRejRadius(float rejRadius){
		this.rejRadius = rejRadius;
	}

	public float getRejSpread(){
		return rejSpread;
	}

	public void setRejSpread(float rejSpread){
		this.rejSpread = rejSpread;
	}

	public float getRejStrength(){
		return rejStrength;
	}

	public void setRejStrength(float rejStrength){
		this.rejStrength = rejStrength;
	}

	public float getTanRadius(){
		return tanRadius;
	}

	public void setTanRadius(float tanRadius){
		this.tanRadius = tanRadius;
	}

	public float getTanSpread(){
		return tanSpread;
	}

	public void setTanSpread(float tanSpread){
		this.tanSpread = tanSpread;
	}

	public float getTanStrength(){
		return tanStrength;
	}

	public void setTanStrength(float tanStrength){
		this.tanStrength = tanStrength;
	}

	public List<PFGeneFitness> getFitness(){
		return fitness;
	}

	public void setFitness(List<PFGeneFitness> fitness){
		this.fitness = fitness;
	}

	public List<String> getParentGenes(){
		return parentGenes;
	}

	public void setParentGenes(List<String> parentGenes){
		this.parentGenes = parentGenes;
	}

	public String getMutations(){
		return mutations;
	}

	public void setMutations(String mutations){
		this.mutations = mutations;
	}
}
