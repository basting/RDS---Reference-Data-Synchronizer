package com.amdocs.bgh.rds.udp;

import java.util.ArrayList;

public class LevelStructure {
	private boolean hardcoded;
	
	private int level;
	
	private String fromTable;
	private String titleFrom; 
	private String refidFrom;
	private String parentKey;
	private String extsrcKey;
	private String condition;
	
	private ArrayList<UDPLElem> udpElements;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(udpElements == null){
			builder.append(level).append(":").append(fromTable).append(":").
					append(titleFrom).append(":").append(refidFrom).append(":").
					append(parentKey).append(":").append(extsrcKey).append(":").
					append(condition);
		}else{
			builder.append(udpElements);
		}
		return builder.toString();
	}
	
	public ArrayList<UDPLElem> getUdpElements() {
		return udpElements;
	}

	public void setUdpElements(ArrayList<UDPLElem> udpElements) {
		this.udpElements = udpElements;
	}

	public void addElement(UDPLElem elem){
		if(udpElements == null){
			udpElements = new ArrayList<UDPLElem>();
		}
		udpElements.add(elem);
	}
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getExtsrcKey() {
		return extsrcKey;
	}
	public void setExtsrcKey(String extsrcKey) {
		this.extsrcKey = extsrcKey;
	}
	public String getFromTable() {
		return fromTable;
	}
	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}
	public boolean isHardcoded() {
		return hardcoded;
	}
	public void setHardcoded(boolean hardcoded) {
		this.hardcoded = hardcoded;
	}
	public String getParentKey() {
		return parentKey;
	}
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}
	public String getRefidFrom() {
		return refidFrom;
	}
	public void setRefidFrom(String refidFrom) {
		this.refidFrom = refidFrom;
	}
	public String getTitleFrom() {
		return titleFrom;
	}
	public void setTitleFrom(String titleFrom) {
		this.titleFrom = titleFrom;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
