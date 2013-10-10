package com.murphysean.bzrflag.resources;

import com.murphysean.bzrflag.daos.PFGenDAO;
import com.murphysean.bzrflag.models.PFGene;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/pfgenes")
public class PFGeneResource{
	PFGenDAO pfGenDAO = new PFGenDAO();

	@GET
	public Response getPFGenes(@DefaultValue(value = "0") @QueryParam(value = "generation") int generation){
		if(generation != 0)
			return Response.ok(pfGenDAO.readPFGenes(generation)).build();

		return Response.ok(pfGenDAO.readPFGenes()).build();
	}

	@GET
	@Path("/{gene}")
	public Response getPFGene(@PathParam(value = "gene") String gene){
		PFGene pfGene = pfGenDAO.readPFGene(gene);
		if(pfGene != null)
			pfGenDAO.readPFGeneFitness(pfGene);
		return Response.ok(pfGene).build();
	}

	@PUT
	@Path("/{gene}")
	public Response createUpdatePFGene(@PathParam(value = "gene") String gene, PFGene pfGene){
		pfGene.setGene(gene);
		pfGene.setFitness(null);
		pfGenDAO.createPFGene(pfGene);

		return Response.ok(pfGene).build();
	}
}
