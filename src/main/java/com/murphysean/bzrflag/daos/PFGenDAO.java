package com.murphysean.bzrflag.daos;

import com.datastax.driver.core.*;
import com.murphysean.bzrflag.models.PFGene;
import com.murphysean.bzrflag.models.PFGeneFitness;
import com.murphysean.bzrflag.singletons.CassandraConnection;

import java.util.ArrayList;
import java.util.List;

public class PFGenDAO{
	protected Session session;

	public PFGenDAO(){
		this.session = CassandraConnection.getInstance().getSession();
	}

	public PFGenDAO(Session session){
		this.session = session;
	}

	public PFGene createPFGene(PFGene pfGene){
		PreparedStatement statement = session.prepare("INSERT INTO pfgenetics (gene, generation, attradius, attspread, attstrength, rejradius, rejspread, rejstrength, tanradius, tanspread, tanstrength, parentgenes, mutations)\n" +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");

		BoundStatement boundStatement = new BoundStatement(statement)
				.setString("gene",pfGene.getGene())
				.setInt("generation",pfGene.getGeneration())
				.setFloat("attradius",pfGene.getAttRadius())
				.setFloat("attspread",pfGene.getAttSpread())
				.setFloat("attstrength",pfGene.getAttStrength())
				.setFloat("rejradius",pfGene.getRejRadius())
				.setFloat("rejspread",pfGene.getRejSpread())
				.setFloat("rejstrength",pfGene.getRejStrength())
				.setFloat("tanradius",pfGene.getTanRadius())
				.setFloat("tanspread",pfGene.getTanSpread())
				.setFloat("tanstrength",pfGene.getTanStrength())
				.setList("parentgenes",pfGene.getParentGenes())
				.setString("mutations",pfGene.getMutations());

		session.execute(boundStatement);

		return pfGene;
	}

	public PFGene readPFGene(String gene){
		PreparedStatement statement = session.prepare("SELECT * FROM pfgenetics WHERE gene = ?");

		BoundStatement boundStatement = new BoundStatement(statement)
				.setString("gene",gene);

		ResultSet resultSet = session.execute(boundStatement);
		Row row = resultSet.one();
		if(row != null){
			PFGene pfGene = new PFGene();
			pfGene.setGene(row.getString("gene"));
			pfGene.setGeneration(row.getInt("generation"));
			pfGene.setAttRadius(row.getFloat("attradius"));
			pfGene.setAttSpread(row.getFloat("attspread"));
			pfGene.setAttStrength(row.getFloat("attstrength"));

			pfGene.setRejRadius(row.getFloat("rejradius"));
			pfGene.setRejSpread(row.getFloat("rejspread"));
			pfGene.setRejStrength(row.getFloat("rejstrength"));

			pfGene.setTanRadius(row.getFloat("tanradius"));
			pfGene.setTanSpread(row.getFloat("tanspread"));
			pfGene.setTanStrength(row.getFloat("tanstrength"));

			pfGene.setParentGenes(row.getList("parentgenes",String.class));
			pfGene.setMutations(row.getString("mutations"));
			return pfGene;
		}

		return null;
	}

	public void addFitnessToGene(String gene, float fitness){
		PreparedStatement statement = session.prepare("UPDATE pfgenetics SET fitness = fitness + [?] WHERE gene = ?");

		BoundStatement boundStatement = new BoundStatement(statement)
				.setFloat("fitness",fitness)
				.setString("gene",gene);

		session.execute(boundStatement);
	}

	public List<PFGene> readPFGenes(){
		List<PFGene> ret = new ArrayList<>();

		PreparedStatement statement = session.prepare("SELECT * FROM pfgenetics");
		BoundStatement boundStatement = new BoundStatement(statement);

		ResultSet resultSet = session.execute(boundStatement);

		for(Row row : resultSet){
			PFGene pfGene = new PFGene();
			pfGene.setGene(row.getString("gene"));
			pfGene.setGeneration(row.getInt("generation"));
			pfGene.setAttRadius(row.getFloat("attradius"));
			pfGene.setAttSpread(row.getFloat("attspread"));
			pfGene.setAttStrength(row.getFloat("attstrength"));

			pfGene.setRejRadius(row.getFloat("rejradius"));
			pfGene.setRejSpread(row.getFloat("rejspread"));
			pfGene.setRejStrength(row.getFloat("rejstrength"));

			pfGene.setTanRadius(row.getFloat("tanradius"));
			pfGene.setTanSpread(row.getFloat("tanspread"));
			pfGene.setTanStrength(row.getFloat("tanstrength"));

			pfGene.setParentGenes(row.getList("parentgenes",String.class));
			pfGene.setMutations(row.getString("mutations"));
			ret.add(pfGene);
		}

		return ret;
	}

	public List<PFGene> readPFGenes(int generation){
		List<PFGene> ret = new ArrayList<>();

		PreparedStatement statement = session.prepare("SELECT * FROM pfgenetics WHERE generation = ?");
		BoundStatement boundStatement = new BoundStatement(statement)
				.setInt("generation",generation);

		ResultSet resultSet = session.execute(boundStatement);

		for(Row row : resultSet){
			PFGene pfGene = new PFGene();
			pfGene.setGene(row.getString("gene"));
			pfGene.setGeneration(generation);
			pfGene.setAttRadius(row.getFloat("attradius"));
			pfGene.setAttSpread(row.getFloat("attspread"));
			pfGene.setAttStrength(row.getFloat("attstrength"));

			pfGene.setRejRadius(row.getFloat("rejradius"));
			pfGene.setRejSpread(row.getFloat("rejspread"));
			pfGene.setRejStrength(row.getFloat("rejstrength"));

			pfGene.setTanRadius(row.getFloat("tanradius"));
			pfGene.setTanSpread(row.getFloat("tanspread"));
			pfGene.setTanStrength(row.getFloat("tanstrength"));

			pfGene.setParentGenes(row.getList("parentgenes",String.class));
			pfGene.setMutations(row.getString("mutations"));
			ret.add(pfGene);
		}

		return ret;
	}

	public void createPFGeneFitness(String gene, long fitness, String map, String note){
		PreparedStatement statement = session.prepare("INSERT INTO pfgeneticsfitness(gene, fitness, map, note) VALUES (?,?,?,?)");

		BoundStatement boundStatement = new BoundStatement(statement)
				.setString("gene",gene)
				.setInt("fitness",(int)fitness)
				.setString("map",map)
				.setString("note",note);

		session.execute(boundStatement);
	}

	public List<PFGeneFitness> readPFGeneFitnessForGene(String gene){
		List<PFGeneFitness> ret = new ArrayList<>();

		PreparedStatement statement = session.prepare("SELECT * FROM pfgeneticsfitness WHERE gene = ?");
		BoundStatement boundStatement = new BoundStatement(statement)
				.setString("gene",gene);

		ResultSet resultSet = session.execute(boundStatement);

		for(Row row : resultSet){
			PFGeneFitness pfGeneFitness = new PFGeneFitness();
			pfGeneFitness.setGene(row.getString("gene"));
			pfGeneFitness.setFitness(row.getInt("fitness"));
			pfGeneFitness.setMap(row.getString("map"));
			pfGeneFitness.setNote(row.getString("note"));
			ret.add(pfGeneFitness);
		}

		return ret;
	}

	public PFGene readPFGeneFitness(PFGene gene){
		gene.setFitness(readPFGeneFitnessForGene(gene.getGene()));
		return gene;
	}
}