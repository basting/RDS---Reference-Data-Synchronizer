package com.amdocs.bgh.rds.udp;

import java.util.ArrayList;

public class UDPStructure {
	private String title;
	private String refId;
	private String description;
	
	private ArrayList<LevelStructure> levelStructures;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(title).append(":").append(refId).append(":").append(levelStructures);
		return builder.toString();
	}
	
	public UDPStructure(){
		levelStructures = new ArrayList<LevelStructure>();
	}
	
	public void addLevelStructure(LevelStructure levelStructure){
		levelStructures.add(levelStructure);
	}
	
	public ArrayList<LevelStructure> getLevelStructures() {
		return levelStructures;
	}

	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
