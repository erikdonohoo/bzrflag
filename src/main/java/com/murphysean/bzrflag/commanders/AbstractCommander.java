package com.murphysean.bzrflag.commanders;

import com.murphysean.bzrflag.events.BZRFlagEvent;
import com.murphysean.bzrflag.interfaces.Commander;
import com.murphysean.bzrflag.models.Game;
import com.murphysean.bzrflag.models.Team;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
		@JsonSubTypes.Type(value=PFEvolutionCommander.class, name="pfevolution"),
		@JsonSubTypes.Type(value=OccGridCommander.class, name="occgrid")
})
public abstract class AbstractCommander extends Team implements Commander{
	@JsonIgnore
	protected transient Game game;
	protected String type;

	public AbstractCommander(){
		this.type = "abstract";
	}

	public AbstractCommander(Game game){
		setGame(game);
		this.type = "abstract";
	}

	@Override
	public void sendBZRFlagEvent(BZRFlagEvent event){
		//Child classes should override if they have functionality
	}

	@JsonIgnore
	@Override
	public Game getGame(){
		return game;
	}

	@JsonIgnore
	@Override
	public void setGame(Game game){
		this.game = game;
		id = game.getTeam().getId();
		color = game.getTeam().getColor();
		playerCount = game.getTeam().getPlayerCount();
		base = game.getTeam().getBase();
		base.setTeamColor(color);
		flag = game.getTeam().getFlag();
		flag.setTeamColor(color);
		this.game.setTeam(this);
	}

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	@Override
	public boolean isOccGridRequired(){
		return false;
	}
}